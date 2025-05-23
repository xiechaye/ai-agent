<template>
  <div class="love-app-container">
    <chat-room
      ref="chatRoomRef"
      title="AI 恋爱大师"
      :showModeSelector="true"
      @send-message="handleSendMessage"
      @mode-change="handleModeChange"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import ChatRoom from '../components/ChatRoom.vue'
import api from '../api'
import { LoveAppMode } from '../types/chat'
import { v4 as uuidv4 } from 'uuid'
import { ElMessage } from 'element-plus'

const chatRoomRef = ref<InstanceType<typeof ChatRoom> | null>(null)
const chatId = ref(uuidv4())
const currentMode = ref<LoveAppMode>(LoveAppMode.BASIC)
let eventSource: EventSource | null = null

// 发送消息并处理SSE连接
const handleSendMessage = (message: string) => {
  if (!message.trim()) return
  
  // 关闭之前的连接
  closeEventSource()
  
  // 根据当前模式创建对应的SSE连接
  try {
    switch (currentMode.value) {
      case LoveAppMode.BASIC:
        eventSource = api.createLoveAppChatSse(chatId.value, message)
        break
      case LoveAppMode.RAG:
        eventSource = api.createLoveAppRagChatSse(chatId.value, message)
        break
      case LoveAppMode.TOOL:
        eventSource = api.createLoveAppToolChatSse(chatId.value, message)
        break
    }
    
    setupEventListeners()
  } catch (error) {
    console.error('创建SSE连接失败:', error)
    ElMessage.error('连接失败，请稍后再试')
    if (chatRoomRef.value) {
      chatRoomRef.value.setLoading(false)
    }
  }
}

// 设置SSE事件监听器
const setupEventListeners = () => {
  if (!eventSource) return
  
  let fullResponse = ''
  
  eventSource.onmessage = (event) => {
    if (event.data) {
      // 累加响应内容
      fullResponse += event.data
      
      // 更新聊天内容
      if (chatRoomRef.value) {
        chatRoomRef.value.addAiMessage(fullResponse)
      }
    }
  }
  
  eventSource.onerror = (error) => {
    console.error('SSE连接错误:', error)
    closeEventSource()
    if (chatRoomRef.value) {
      chatRoomRef.value.setLoading(false)
    }
  }
  
  eventSource.addEventListener('close', () => {
    closeEventSource()
    if (chatRoomRef.value) {
      chatRoomRef.value.setLoading(false)
    }
  })
}

// 关闭SSE连接
const closeEventSource = () => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
}

// 处理模式切换
const handleModeChange = (mode: LoveAppMode) => {
  currentMode.value = mode
  ElMessage.info(`已切换到${mode === LoveAppMode.BASIC ? '基础模式' : mode === LoveAppMode.RAG ? '知识库模式' : '工具模式'}`)
}

// 组件卸载时关闭SSE连接
onUnmounted(() => {
  closeEventSource()
})
</script>

<style scoped>
.love-app-container {
  height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}
</style> 