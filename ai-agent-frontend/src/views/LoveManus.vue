<template>
  <div class="love-manus-container">
    <chat-room
      ref="chatRoomRef"
      title="AI 超级智能体"
      @send-message="handleSendMessage"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'
import ChatRoom from '../components/ChatRoom.vue'
import api from '../api'
import { ElMessage } from 'element-plus'

const chatRoomRef = ref<InstanceType<typeof ChatRoom> | null>(null)
let eventSource: EventSource | null = null
let currentResponse = ''

// 发送消息并处理SSE连接
const handleSendMessage = (message: string) => {
  if (!message.trim()) return
  
  // 关闭之前的连接
  closeEventSource()
  currentResponse = ''
  
  try {
    eventSource = api.createLoveManusChatSse(message)
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
        
        // 累加响应内容
        currentResponse += event.data
        
        // 更新聊天内容
        if (chatRoomRef.value) {
          chatRoomRef.value.updateAiMessage(currentResponse)
        }
      } catch (e) {
        console.error('处理SSE响应失败:', e)
      }
    }
  }
  
  eventSource.onerror = (error) => {
    console.error('SSE连接错误:', error)
    closeEventSource()
    if (chatRoomRef.value) {
      chatRoomRef.value.completeAiMessage()
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
  height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}
</style> 