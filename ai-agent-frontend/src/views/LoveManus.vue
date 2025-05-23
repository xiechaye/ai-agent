<template>
  <div class="love-manus-container">
    <div class="app-header">
      <div class="container d-flex justify-between align-center">
        <div class="return-btn">
          <el-button type="primary" size="small" @click="goBack" class="btn-with-icon">
            <el-icon><arrow-left /></el-icon>
            <span>返回主页</span>
          </el-button>
        </div>
        <h1 class="app-title hidden-xs">AI 超级智能体</h1>
        <div class="header-actions"></div>
      </div>
    </div>
    
    <div class="app-content">
      <chat-room
        ref="chatRoomRef"
        title="AI 超级智能体"
        @send-message="handleSendMessage"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import ChatRoom from '../components/ChatRoom.vue'
import api from '../api'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const chatRoomRef = ref<InstanceType<typeof ChatRoom> | null>(null)
let eventSource: EventSource | null = null
let currentResponse = ''
let isToolResponse = false
let lastUserInput = ''
let fullResponse = '' // 用于存储完整的响应内容

// 返回主页
const goBack = () => {
  router.push('/')
}

// 发送消息并处理SSE连接
const handleSendMessage = (message: string) => {
  if (!message.trim()) return
  
  // 存储用户输入
  lastUserInput = message.trim()
  
  // 关闭之前的连接
  closeEventSource()
  currentResponse = ''
  isToolResponse = false
  fullResponse = '' // 重置完整响应
  
  // 设置加载状态
  if (chatRoomRef.value) {
    chatRoomRef.value.setLoading(true)
  }
  
  connectSSE(lastUserInput)
}

// 建立SSE连接
const connectSSE = (message: string) => {
  try {
    eventSource = api.createLoveManusChatSse(message)
    setupEventListeners()
  } catch (error) {
    handleConnectionError('连接失败，请稍后再试')
  }
}

// 处理连接错误
const handleConnectionError = (errorMessage: string) => {
  ElMessage.error(errorMessage)
  if (chatRoomRef.value) {
    chatRoomRef.value.setLoading(false)
    chatRoomRef.value.addAiMessage(`错误: ${errorMessage}`)
  }
}

// 设置SSE事件监听器
const setupEventListeners = () => {
  if (!eventSource) return
  
  // 添加SSE连接打开的处理
  eventSource.onopen = () => {
    // 移除控制台日志
    
    // 创建初始空AI消息，为打字机效果做准备
    if (chatRoomRef.value) {
      // 先结束加载状态，然后创建一个空消息
      chatRoomRef.value.setLoading(false)
      // 创建一个空消息
      chatRoomRef.value.updateAiMessage('')
    }
  }
  
  let isFirstMessage = true
  
  eventSource.onmessage = (event) => {
    if (event.data) {
      try {
        // 检查SSE响应是否为完成消息
        if (event.data.includes('[DONE]')) {
          closeEventSource()
          if (chatRoomRef.value) {
            chatRoomRef.value.completeAiMessage()
          }
          return
        }
        
        // 将新的响应添加到完整响应中，每个部分之间添加换行符
        if (fullResponse) {
          fullResponse += '\n\n' + event.data
        } else {
          fullResponse = event.data
        }
        
        // 立即更新消息
        if (chatRoomRef.value) {
          // 所有消息都使用打字机效果
          chatRoomRef.value.updateAiMessageWithTyping(fullResponse)
          isFirstMessage = false
        }
      } catch (e) {
        // 移除错误日志
      }
    }
  }
  
  eventSource.onerror = (error) => {
    // 移除错误日志
    
    // 直接关闭连接，不尝试重连
    closeEventSource()
    
    if (chatRoomRef.value) {
      chatRoomRef.value.completeAiMessage()
      // 不再显示错误消息
    }
  }
}

// 关闭SSE连接
const closeEventSource = () => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}

// 组件卸载时关闭SSE连接
onUnmounted(() => {
  closeEventSource()
})
</script>

<style scoped>
.love-manus-container {
  min-height: var(--app-height);
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
}

.app-header {
  height: var(--header-height);
  background-color: var(--bg-secondary);
  box-shadow: var(--box-shadow-light);
  padding: 0 var(--spacing-md);
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
}

.app-title {
  font-size: var(--font-size-lg);
  color: var(--primary-color);
  margin: 0;
  text-align: center;
}

.btn-with-icon {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.app-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

@media (max-width: 768px) {
  .app-header {
    height: auto;
    padding: var(--spacing-sm);
  }
  
  .btn-with-icon span {
    display: none;
  }
}
</style> 