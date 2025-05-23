<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>{{ title }}</h2>
      <div v-if="showModeSelector" class="mode-selector">
        <el-radio-group v-model="currentMode" @change="handleModeChange">
          <el-radio-button label="basic">基础模式</el-radio-button>
          <el-radio-button label="rag">知识库模式</el-radio-button>
          <el-radio-button label="tool">工具模式</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    <div class="messages-container" ref="messagesContainer">
      <div v-for="msg in messages" :key="msg.id" :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']">
        <div class="message-content">
          <p>{{ msg.content }}</p>
          <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
        </div>
      </div>
      <div v-if="loading" class="message ai-message">
        <div class="message-content">
          <p class="typing">AI正在输入中...</p>
        </div>
      </div>
    </div>
    <div class="input-container">
      <el-input
        v-model="inputMessage"
        placeholder="请输入消息..."
        type="textarea"
        :rows="2"
        :disabled="loading"
        @keyup.enter="sendMessage"
      />
      <el-button type="primary" :disabled="!inputMessage.trim() || loading" @click="sendMessage">
        发送
      </el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { ChatMessage, ChatRole, LoveAppMode } from '../types/chat'
import { ElMessage } from 'element-plus'
import { v4 as uuidv4 } from 'uuid'

const props = defineProps({
  title: {
    type: String,
    default: '聊天室'
  },
  showModeSelector: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send-message', 'mode-change'])

const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const loading = ref(false)
const currentMode = ref<LoveAppMode>(LoveAppMode.BASIC)
const messagesContainer = ref<HTMLElement | null>(null)
const currentAiMessageId = ref<string | null>(null)
let typingTimer: NodeJS.Timeout | null = null // 用于打字机效果的计时器
let fullText = '' // 完整的文本内容
let displayedText = '' // 当前显示的文本内容
let typingSpeed = 10 // 打字速度（毫秒/字符）

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  const userMessage: ChatMessage = {
    id: uuidv4(),
    role: ChatRole.USER,
    content: inputMessage.value.trim(),
    timestamp: Date.now()
  }
  
  messages.value.push(userMessage)
  const msgToSend = inputMessage.value.trim()
  inputMessage.value = ''
  loading.value = true
  
  // 创建新的AI消息占位（空内容）
  currentAiMessageId.value = uuidv4()
  
  await nextTick()
  scrollToBottom()
  
  emit('send-message', msgToSend)
}

// 更新或添加AI消息
const updateAiMessage = (content: string) => {
  // 如果有正在进行的打字效果，清除它
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  
  // 检查是否有工具调用前缀
  if (content.startsWith('工具:') || content.startsWith('Tools:')) {
    // 为工具调用结果创建新消息
    const toolMessageId = uuidv4()
    messages.value.push({
      id: toolMessageId,
      role: ChatRole.AI,
      content,
      timestamp: Date.now()
    })
    nextTick(() => {
      scrollToBottom()
    })
    return
  }
  
  // 如果已有AI消息ID，则更新现有消息
  if (currentAiMessageId.value) {
    const index = messages.value.findIndex(msg => msg.id === currentAiMessageId.value)
    if (index !== -1) {
      messages.value[index].content = content
    } else {
      // 创建新消息
      addNewAiMessage(content)
    }
  } else {
    // 如果没有当前AI消息ID，则创建新消息
    addNewAiMessage(content)
  }
  
  nextTick(() => {
    scrollToBottom()
  })
}

// 添加新的AI消息
const addNewAiMessage = (content: string) => {
  currentAiMessageId.value = uuidv4()
  messages.value.push({
    id: currentAiMessageId.value,
    role: ChatRole.AI,
    content,
    timestamp: Date.now()
  })
}

// 直接添加新消息而不更新现有消息或重置loading状态
const addAiMessage = (content: string) => {
  const messageId = uuidv4()
  messages.value.push({
    id: messageId,
    role: ChatRole.AI,
    content,
    timestamp: Date.now()
  })
  
  nextTick(() => {
    scrollToBottom()
  })
}

// 更新或添加AI消息（带打字机效果）
const updateAiMessageWithTyping = (content: string) => {
  // 保存完整文本
  fullText = content
  
  // 如果已有消息正在进行打字效果，先清除之前的计时器
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  
  // 如果没有当前AI消息，创建一个新的
  if (!currentAiMessageId.value) {
    addNewAiMessage('')
  }
  
  // 查找当前消息索引
  const index = messages.value.findIndex(msg => msg.id === currentAiMessageId.value)
  if (index === -1) return
  
  // 获取已显示的文本长度
  const currentText = messages.value[index].content || ''
  
  // 如果新内容包含当前显示的文本，则从当前位置继续打字
  if (fullText.includes(currentText) && currentText.length > 0) {
    displayedText = currentText
  } else {
    // 如果内容不匹配，则重置
    displayedText = ''
  }
  
  // 启动打字机效果
  startTypingEffect(index)
}

// 启动打字机效果
const startTypingEffect = (messageIndex: number) => {
  if (messageIndex === -1 || !fullText) return
  
  // 如果已经显示了全部文本，不需要打字效果
  if (displayedText === fullText) return
  
  // 计算还有多少字符需要显示
  const remainingChars = fullText.length - displayedText.length
  
  // 根据剩余字符数调整打字速度，文本越长，速度越快
  let adjustedSpeed = Math.max(2, Math.min(typingSpeed, 
    remainingChars > 1000 ? 1 : // 超长文本 1ms/字符
    remainingChars > 500 ? 2 : // 长文本 2ms/字符
    remainingChars > 200 ? 3 : // 中等文本 3ms/字符
    5)) // 短文本 5ms/字符
  
  // 计算每次应添加的字符数
  let charsToAddEachTime = remainingChars > 1000 ? 10 :
                           remainingChars > 500 ? 5 :
                           remainingChars > 200 ? 3 : 1
  
  typingTimer = setInterval(() => {
    // 如果已经完成了打字，清除计时器
    if (displayedText.length >= fullText.length) {
      if (typingTimer) {
        clearInterval(typingTimer)
        typingTimer = null
      }
      return
    }
    
    // 添加多个字符，而不是一个，以提高性能和流畅度
    const newLength = Math.min(displayedText.length + charsToAddEachTime, fullText.length)
    displayedText = fullText.substring(0, newLength)
    
    // 更新消息
    messages.value[messageIndex].content = displayedText
    
    // 滚动到底部 (只在必要时调用，避免频繁渲染)
    if (newLength % 30 === 0 || newLength === fullText.length) {
      nextTick(() => {
        scrollToBottom()
      })
    }
  }, adjustedSpeed)
}

// 完成AI消息响应
const completeAiMessage = () => {
  // 清除打字机效果的计时器
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  
  // 如果还有未完成的打字内容，立即显示完整内容
  if (currentAiMessageId.value && fullText) {
    const index = messages.value.findIndex(msg => msg.id === currentAiMessageId.value)
    if (index !== -1) {
      messages.value[index].content = fullText
    }
  }
  
  currentAiMessageId.value = null
  loading.value = false
  fullText = ''
  displayedText = ''
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 处理模式切换
const handleModeChange = (mode: LoveAppMode) => {
  emit('mode-change', mode)
}

// 暴露方法给父组件
defineExpose({
  updateAiMessage,
  updateAiMessageWithTyping,
  addAiMessage,
  completeAiMessage,
  setLoading: (value: boolean) => {
    loading.value = value
    if (!value) {
      currentAiMessageId.value = null
      // 清除打字机效果
      if (typingTimer) {
        clearInterval(typingTimer)
        typingTimer = null
      }
      fullText = ''
      displayedText = ''
    }
  }
})

// 监听消息列表变化，自动滚动到底部
watch(
  messages,
  () => {
    nextTick(() => {
      scrollToBottom()
    })
  },
  { deep: true }
)

// 组件挂载后滚动到底部
onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-width: 800px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  background-color: #f9f9f9;
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #ffffff;
  border-radius: 8px 8px 0 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.chat-header h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.mode-selector {
  margin-top: 10px;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.message {
  margin-bottom: 16px;
  display: flex;
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 12px;
  border-radius: 12px;
  position: relative;
}

.user-message .message-content {
  background-color: #e1f3ff;
}

.ai-message .message-content {
  background-color: #ffffff;
}

.message-content p {
  margin: 0;
  word-break: break-word;
  white-space: pre-wrap;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  display: block;
  text-align: right;
}

.input-container {
  padding: 16px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  background-color: #ffffff;
  border-radius: 0 0 8px 8px;
}

.input-container .el-input {
  flex: 1;
  margin-right: 12px;
}

.typing {
  display: flex;
  align-items: center;
}

.typing::after {
  content: '...';
  animation: ellipsis 1.2s infinite;
  width: 15px;
  display: inline-block;
  text-align: left;
}

@keyframes ellipsis {
  0% { content: '.'; }
  33% { content: '..'; }
  66% { content: '...'; }
}
</style> 