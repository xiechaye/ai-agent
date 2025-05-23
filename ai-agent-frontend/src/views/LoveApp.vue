<template>
  <div class="love-app-container">
    <div class="return-btn">
      <el-button type="primary" size="small" @click="goBack">
        <el-icon><arrow-left /></el-icon> 返回主页
      </el-button>
    </div>
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
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const chatRoomRef = ref<InstanceType<typeof ChatRoom> | null>(null)
const chatId = ref(uuidv4())
const currentMode = ref<LoveAppMode>(LoveAppMode.BASIC)
let eventSource: EventSource | null = null
let currentResponse = ''
let isToolResponse = false

// 返回主页
const goBack = () => {
  router.push('/')
}

// 发送消息并处理SSE连接
const handleSendMessage = (message: string) => {
  if (!message.trim()) return
  
  // 关闭之前的连接
  closeEventSource()
  currentResponse = ''
  isToolResponse = false
  
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
  
  eventSource.onmessage = (event) => {
    if (event.data) {
      // 检查是否是工具调用
      if (event.data.includes('工具:') && !isToolResponse) {
        isToolResponse = true
        currentResponse = event.data
        if (chatRoomRef.value) {
          chatRoomRef.value.updateAiMessage(currentResponse)
        }
        // 重置当前响应，准备下一条消息
        currentResponse = ''
        return
      }
      
      // 如果当前是工具响应且收到了新的普通消息
      if (isToolResponse && !event.data.includes('工具:')) {
        isToolResponse = false
        currentResponse = event.data
      } else {
        // 累加响应内容
        currentResponse += event.data
      }
      
      // 更新聊天内容
      if (chatRoomRef.value) {
        chatRoomRef.value.updateAiMessage(currentResponse)
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
  
  eventSource.addEventListener('close', () => {
    closeEventSource()
    if (chatRoomRef.value) {
      chatRoomRef.value.completeAiMessage()
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
  position: relative;
}

.return-btn {
  position: absolute;
  top: 30px;
  left: 30px;
  z-index: 10;
}
</style> 