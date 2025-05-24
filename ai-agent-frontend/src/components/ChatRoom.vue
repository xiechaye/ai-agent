<template>
  <div class="chat-container">
    <div class="chat-header">
      <h2>{{ title }}</h2>
      <div v-if="showModeSelector" class="mode-selector">
        <el-radio-group v-model="currentMode" @change="handleModeChange" size="small">
          <el-radio-button value="basic">基础模式</el-radio-button>
          <el-radio-button value="rag">知识库模式</el-radio-button>
          <el-radio-button value="tool">工具模式</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    
    <div class="messages-container" ref="messagesContainer">
      <div v-for="msg in messages" :key="msg.id" :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']">
        <div class="message-avatar ai-avatar" v-if="msg.role === 'ai'">
          <span class="ai-letter">A</span>
        </div>
        <div class="message-content">
          <div class="message-bubble">
          <p>{{ msg.content }}</p>
            <!-- 如果消息包含PDF或其他文档文件链接，显示下载按钮 -->
            <div v-if="msg.fileLink && isDocumentFile(msg.fileLink)" class="file-download">
              <el-button type="primary" size="small" @click="downloadFile(msg.fileLink)" class="download-btn">
                <el-icon><Download /></el-icon>
                <span>下载文件</span>
              </el-button>
            </div>
            <!-- 如果消息正在执行中，显示执行中的指示器 -->
            <div v-if="msg.isExecuting" class="executing-indicator">
              <el-icon class="executing-icon"><Loading /></el-icon>
              <span>AI正在执行命令，请耐心等待...</span>
            </div>
          </div>
          <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
        </div>
        <div class="message-avatar user-avatar" v-if="msg.role === 'user'">
          <el-icon><User /></el-icon>
        </div>
      </div>
      
      <div v-if="loading" class="message ai-message">
        <div class="message-avatar ai-avatar">
          <span class="ai-letter">A</span>
        </div>
        <div class="message-content">
          <div class="message-bubble">
            <p class="typing">AI正在输入中</p>
          </div>
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
        @keyup.enter.exact.prevent="sendMessage"
        class="chat-input"
      />
      <!-- 发送/停止按钮 - 根据状态动态切换 -->
      <el-button 
        v-if="!loading && !isGenerating && typingTimer === null && !sseActive" 
        type="primary" 
        :disabled="!inputMessage.trim()" 
        @click="sendMessage"
        class="send-button"
        round
      >
        <el-icon class="send-icon"><Position /></el-icon>
        <span class="send-text">发送</span>
      </el-button>
      
      <el-button 
        v-else
        type="danger" 
        @click="stopGeneration"
        class="stop-button"
        round
      >
        <el-icon class="stop-icon"><Close /></el-icon>
        <span class="stop-text">停止</span>
      </el-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch, nextTick, onUnmounted } from 'vue'
import type { ChatMessage } from '../types/chat'
import { ChatRole, LoveAppMode } from '../types/chat'
import { ElMessage } from 'element-plus'
import { v4 as uuidv4 } from 'uuid'
import { User, Position, Download, Loading, Close } from '@element-plus/icons-vue'

// 使用defineProps代替const props
defineProps({
  title: {
    type: String,
    default: '聊天室'
  },
  showModeSelector: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send-message', 'mode-change', 'stop-generation'])

const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const loading = ref(false)
const isGenerating = ref(false) // 控制是否正在生成消息
const currentMode = ref<LoveAppMode>(LoveAppMode.BASIC)
const messagesContainer = ref<HTMLElement | null>(null)
const currentAiMessageId = ref<string | null>(null)
let typingTimer: ReturnType<typeof setTimeout> | null = null // 用于打字机效果的计时器
let fullText = '' // 完整的文本内容
let displayedText = '' // 当前显示的文本内容
let typingSpeed = 10 // 打字速度（毫秒/字符）
const sseActive = ref(false)

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 检查是否为文档类型文件
const isDocumentFile = (url: string): boolean => {
  // 检查URL是否以常见文档格式结尾
  const docExtensions = ['.pdf', '.docx', '.xlsx', '.pptx', '.zip', '.rar']
  return docExtensions.some(ext => url.toLowerCase().endsWith(ext))
}

// 停止消息生成
const stopGeneration = () => {
  // 发出停止生成的事件
  emit('stop-generation')
  
  // 停止当前的打字效果
  if (typingTimer) {
    clearTimeout(typingTimer)
    typingTimer = null
  }
  
  // 更新状态
  isGenerating.value = false
  
  // 如果有当前消息，添加停止生成的提示
  if (currentAiMessageId.value) {
    const index = messages.value.findIndex(msg => msg.id === currentAiMessageId.value)
    if (index !== -1) {
      messages.value[index].content += '\n\n[用户已停止生成]'
      messages.value[index].isExecuting = false
    }
    
    // 完成当前消息
    currentAiMessageId.value = null
  }
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
  isGenerating.value = true // 设置生成状态为true
  sseActive.value = true // 设置SSE连接为活跃状态
  
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
    clearTimeout(typingTimer)
    typingTimer = null
  }
  
  // 检查是否有工具调用前缀
  if (content.startsWith('工具:') || content.startsWith('Tools:')) {
    // 为工具调用结果创建新消息
    const toolMessageId = uuidv4()
    const fileLink = extractFileLink(content)
    
    messages.value.push({
      id: toolMessageId,
      role: ChatRole.AI,
      content,
      timestamp: Date.now(),
      fileLink
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
      // 检测是否包含文件下载链接
      const fileLink = extractFileLink(content)
      messages.value[index].content = content
      messages.value[index].fileLink = fileLink
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
  // 检测是否包含文件下载链接
  const fileLink = extractFileLink(content)
  
  messages.value.push({
    id: currentAiMessageId.value,
    role: ChatRole.AI,
    content,
    timestamp: Date.now(),
    fileLink
  })
}

// 直接添加新消息而不更新现有消息或重置loading状态
const addAiMessage = (content: string) => {
  const messageId = uuidv4()
  // 检测是否包含文件下载链接
  const fileLink = extractFileLink(content)
  
  messages.value.push({
    id: messageId,
    role: ChatRole.AI,
    content,
    timestamp: Date.now(),
    fileLink
  })
  
  nextTick(() => {
    scrollToBottom()
  })
}

// 提取消息中的文件下载链接
const extractFileLink = (content: string): string | undefined => {
  // 匹配OSS上传成功消息，例如：PDF generated and uploaded successfully to OSS: https://...
  const ossRegex = /uploaded successfully to OSS:\s*(https:\/\/.*\.(?:pdf|docx|xlsx|pptx|zip|rar))/i
  const ossMatch = content.match(ossRegex)
  
  if (ossMatch && ossMatch[1]) {
    return ossMatch[1]
  }
  
  // 匹配普通URL，限定文件类型为常见文档和压缩格式
  const urlRegex = /(https?:\/\/[^\s]+\.(?:pdf|docx|xlsx|pptx|zip|rar))/gi
  const urlMatch = content.match(urlRegex)
  
  if (urlMatch && urlMatch[0]) {
    return urlMatch[0]
  }
  
  return undefined
}

// 下载文件
const downloadFile = (url: string) => {
  // 创建一个临时的a标签来触发下载
  const link = document.createElement('a')
  link.href = url
  
  // 从URL中提取文件名
  const fileName = url.substring(url.lastIndexOf('/') + 1)
  link.download = fileName
  
  // 添加到文档中，触发点击，然后移除
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  ElMessage.success('开始下载文件: ' + fileName)
}

// 更新或添加AI消息（带打字机效果）
const updateAiMessageWithTyping = (content: string, useTypingEffect: boolean = true) => {
  // 保存完整文本
  fullText = content
  
  // 设置生成状态为true，确保显示停止按钮
  isGenerating.value = true
  
  // 如果已有消息正在进行打字效果，先清除之前的计时器
  if (typingTimer) {
    clearTimeout(typingTimer)
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
  
  // 如果不使用打字效果，直接更新全部内容
  if (!useTypingEffect) {
    messages.value[index].content = fullText
    // 检测是否包含文件下载链接
    messages.value[index].fileLink = extractFileLink(fullText)
    nextTick(() => {
      scrollToBottom()
    })
    return
  }
  
  // 否则启动打字机效果
  startTypingEffect(index)
}

// 设置消息的执行中状态
const setMessageExecuting = (isExecuting: boolean) => {
  if (!currentAiMessageId.value) return
  
  const index = messages.value.findIndex(msg => msg.id === currentAiMessageId.value)
  if (index !== -1) {
    messages.value[index].isExecuting = isExecuting
    
    // 当设置为执行中状态时，保持isGenerating为true
    if (isExecuting) {
      isGenerating.value = true
    } else {
      // 当设置为非执行中状态时，检查是否还有其他执行中消息
      const hasOtherExecutingMessages = messages.value.some((m, i) => i !== index && m.isExecuting === true)
      if (!hasOtherExecutingMessages) {
        isGenerating.value = false
      }
    }
  }
}

// 完成AI消息响应
const completeAiMessage = () => {
  // 清除打字机效果的计时器
  if (typingTimer) {
    clearTimeout(typingTimer)
    typingTimer = null
  }
  
  // 如果还有未完成的打字内容，立即显示完整内容
  if (currentAiMessageId.value && fullText) {
    const index = messages.value.findIndex(msg => msg.id === currentAiMessageId.value)
    if (index !== -1) {
      messages.value[index].content = fullText
      // 检测是否包含文件下载链接
      messages.value[index].fileLink = extractFileLink(fullText)
      // 清除执行中状态
      messages.value[index].isExecuting = false
    }
  }
  
  currentAiMessageId.value = null
  loading.value = false
  
  // 注意：这里不重置isGenerating或sseActive，由父组件控制
  
  fullText = ''
  displayedText = ''
}

// 滚动到底部 - 始终滚动到底部，不考虑用户滚动行为
const scrollToBottom = () => {
  if (messagesContainer.value) {
    // 直接设置scrollTop为scrollHeight，确保滚动到最底部
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 启动打字机效果
const startTypingEffect = (messageIndex: number) => {
  if (messageIndex === -1 || !fullText) return
  
  // 如果已经显示了全部文本，不需要打字效果
  if (displayedText === fullText) return
  
  // 设置生成状态为true，确保显示停止按钮
  isGenerating.value = true
  
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
  
  // 使用递归函数实现重复调用效果
  const typeNextChars = () => {
    // 如果已经完成了打字，清除状态
    if (displayedText.length >= fullText.length) {
      typingTimer = null
      
      // 检查是否有执行中的消息，如果没有则重置生成状态
      const hasExecutingMessages = messages.value.some(msg => msg.isExecuting === true)
      if (!hasExecutingMessages) {
        isGenerating.value = false
      }
      return
    }
    
    // 添加多个字符，而不是一个，以提高性能和流畅度
    const newLength = Math.min(displayedText.length + charsToAddEachTime, fullText.length)
    displayedText = fullText.substring(0, newLength)
    
    // 更新消息
    messages.value[messageIndex].content = displayedText
    
    // 每次文本更新后立即滚动到底部
    scrollToBottom()
    
    // 设置下一次调用
    typingTimer = setTimeout(typeNextChars, adjustedSpeed)
  }
  
  // 开始打字效果
  typeNextChars()
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
  setMessageExecuting,
  setLoading: (value: boolean) => {
    loading.value = value
    if (!value) {
      currentAiMessageId.value = null
      
      // 清除打字机效果
      if (typingTimer) {
        clearTimeout(typingTimer)
        typingTimer = null
      }
      fullText = ''
      displayedText = ''
    }
  },
  // 添加新方法给父组件控制SSE连接状态
  setSseActive: (active: boolean) => {
    sseActive.value = active
    // 当SSE连接结束时，检查是否需要重置生成状态
    if (!active) {
      const hasExecutingMessages = messages.value.some(msg => msg.isExecuting === true)
      if (!hasExecutingMessages) {
        isGenerating.value = false
      }
    }
  }
})

// 监听消息列表变化，自动滚动到底部
watch(
  messages,
  () => {
    // 立即滚动，不使用nextTick
      scrollToBottom()
  },
  { deep: true }
)

// 组件挂载后设置滚动
onMounted(() => {
  scrollToBottom()
})

// 组件卸载时清理资源
onUnmounted(() => {
  // 确保清除所有计时器
  if (typingTimer) {
    clearTimeout(typingTimer)
    typingTimer = null
  }
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-width: 90%;
  width: 900px;
  margin: 0 auto;
  border-radius: var(--border-radius-medium);
  box-shadow: var(--box-shadow);
  background-color: var(--bg-secondary);
  overflow: hidden;
  position: relative;
  padding-bottom: 80px; /* 添加底部填充，防止消息被输入框遮挡 */
}

.chat-header {
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--border-light);
  background-color: var(--primary-color);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.chat-header h2 {
  margin: 0 0 var(--spacing-sm) 0;
  color: white;
  font-weight: 500;
}

.mode-selector {
  margin-top: var(--spacing-sm);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
  background-color: var(--bg-primary);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding-bottom: calc(var(--spacing-lg) * 3); /* 增加底部填充，避免最后的消息被输入框遮挡 */
}

.message {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  animation: fadeIn 0.3s ease;
}

.user-message {
  flex-direction: row-reverse;
  align-self: flex-end;
}

.ai-message {
  align-self: flex-start;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--primary-light);
  color: var(--primary-color);
  flex-shrink: 0;
}

.ai-avatar {
  background-color: var(--primary-color);
  color: white;
  font-weight: bold;
}

.ai-letter {
  font-size: 18px;
  font-weight: 600;
}

.user-avatar {
  background-color: #e6f7ff;
  color: #1890ff;
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: 80%;
}

.message-bubble {
  padding: var(--spacing-md);
  border-radius: var(--border-radius-large);
  position: relative;
  word-break: break-word;
  text-align: left;
}

.user-message .message-bubble {
  background-color: #e6f7ff;
  border-top-right-radius: 4px;
}

.ai-message .message-bubble {
  background-color: white;
  border-top-left-radius: 4px;
}

.message-bubble p {
  margin: 0;
  white-space: pre-wrap;
  line-height: 1.5;
  text-align: left;
}

.user-message .message-bubble p {
  color: #1890ff;
  text-align: left;
}

.ai-message .message-bubble p {
  color: var(--text-primary);
  text-align: left;
}

.file-download {
  margin-top: var(--spacing-md);
  display: flex;
  justify-content: flex-start;
}

.download-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  background-color: var(--primary-light);
  color: var(--primary-color);
  border-color: transparent;
  transition: all var(--transition-normal);
}

.download-btn:hover {
  background-color: var(--primary-color);
  color: white;
}

.executing-indicator {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-md);
  color: #b88230;
  font-size: var(--font-size-sm);
  background-color: #fff9e6;
  padding: 10px 14px;
  border-radius: var(--border-radius-medium);
  border-left: 3px solid #e6a23c;
  animation: pulse-bg 2s infinite;
  font-weight: 500;
}

.executing-icon {
  animation: spin 1.5s linear infinite;
  color: #e6a23c;
  font-size: 18px;
}

@keyframes pulse-bg {
  0% { background-color: #fff9e6; }
  50% { background-color: #faecd8; }
  100% { background-color: #fff9e6; }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.message-time {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
  align-self: flex-end;
}

.input-container {
  padding: var(--spacing-md);
  border-top: 1px solid var(--border-light);
  display: flex;
  align-items: flex-end;
  gap: var(--spacing-sm);
  background-color: white;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  box-sizing: border-box;
}

.chat-input {
  flex: 1;
}

.send-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.stop-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
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

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式样式 */
@media (max-width: 992px) {
  .chat-container {
    max-width: 95%;
    height: 100%;
    border-radius: 0;
    box-shadow: none;
  }
  
  .input-container {
    max-width: 95%;
  }
}

@media (max-width: 768px) {
  .chat-container {
    max-width: 100%;
    width: 100%;
  }
  
  .input-container {
    max-width: 100%;
  }
  
  .messages-container {
    padding: var(--spacing-md);
  }
  
  .message-content {
    max-width: 90%;
  }
  
  .send-text {
    display: none;
  }
}

@media (max-width: 480px) {
  .chat-header h2 {
    font-size: var(--font-size-lg);
  }
  
  .mode-selector {
    transform: scale(0.9);
  }
  
  .message-avatar {
    width: 32px;
    height: 32px;
  }
  
  .message-bubble {
    padding: var(--spacing-sm);
  }
}
</style> 