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
  
  await nextTick()
  scrollToBottom()
  
  emit('send-message', msgToSend)
}

// 添加AI消息
const addAiMessage = (content: string) => {
  const aiMessage: ChatMessage = {
    id: uuidv4(),
    role: ChatRole.AI,
    content,
    timestamp: Date.now()
  }
  
  messages.value.push(aiMessage)
  nextTick(() => {
    scrollToBottom()
  })
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
  addAiMessage,
  setLoading: (value: boolean) => {
    loading.value = value
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