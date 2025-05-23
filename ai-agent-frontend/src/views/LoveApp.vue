<template>
  <main class="love-app-container">
    <header class="app-header">
      <div class="container d-flex justify-between align-center">
        <div class="return-btn">
          <el-button type="primary" size="small" @click="goBack" class="btn-with-icon">
            <el-icon><arrow-left /></el-icon>
            <span>返回主页</span>
          </el-button>
        </div>
        <h1 class="app-title hidden-xs">AI 恋爱大师</h1>
        <div class="header-actions"></div>
      </div>
    </header>
    
    <section class="app-content">
      <chat-room
        ref="chatRoomRef"
        title="AI 恋爱大师"
        :showModeSelector="true"
        @send-message="handleSendMessage"
        @mode-change="handleModeChange"
      />
    </section>
  </main>
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