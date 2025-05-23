<template>
  <main class="love-manus-container">
    <header class="app-header">
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
    </header>
    
    <section class="app-content">
      <chat-room
        ref="chatRoomRef"
        title="AI 超级智能体"
        @send-message="handleSendMessage"
        @stop-generation="handleStopGeneration"
      />
    </section>
  </main>
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
let inactivityTimer: NodeJS.Timeout | null = null // 添加不活跃检测定时器

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
    // 设置SSE连接为活跃状态
    chatRoomRef.value.setSseActive(true)
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

// 处理停止生成
const handleStopGeneration = () => {
  // 关闭SSE连接
  closeEventSource()  // 这里会调用setSseActive(false)
  
  // 如果有定时器，清除它们
  if (inactivityTimer) {
    clearTimeout(inactivityTimer)
    inactivityTimer = null
  }
  
  // 在ChatRoom组件中已处理UI状态更新
}

// 设置SSE事件监听器
const setupEventListeners = () => {
  if (!eventSource) return
  
  // 添加SSE连接打开的处理
  eventSource.onopen = () => {
    // 创建初始空AI消息，为打字机效果做准备
    if (chatRoomRef.value) {
      // 先结束加载状态，然后创建一个空消息
      chatRoomRef.value.setLoading(false)
      // 创建一个空消息
      chatRoomRef.value.updateAiMessage('')
      // 立即显示执行中状态
      chatRoomRef.value.setMessageExecuting(true)
    }
  }
  
  let isFirstMessage = true
  let lastActivityTime = Date.now()
  let isExecutingShown = true  // 默认开始就显示执行中状态
  
  // 设置定时器检查连接是否活跃
  const startInactivityCheck = () => {
    // 清除现有的定时器
    if (inactivityTimer) {
      clearTimeout(inactivityTimer)
    }
    
    // 每2秒检查一次是否有新消息
    inactivityTimer = setTimeout(() => {
      const inactiveTime = Date.now() - lastActivityTime
      
      // 如果超过2秒没有活动，但SSE连接仍然打开
      if (inactiveTime > 2000 && eventSource && eventSource.readyState === EventSource.OPEN) {
        // 确保一直显示执行中状态
        if (chatRoomRef.value && !isExecutingShown) {
          chatRoomRef.value.setMessageExecuting(true)
          isExecutingShown = true
        }
        
        // 继续检查
        startInactivityCheck()
      } else if (eventSource && eventSource.readyState === EventSource.OPEN) {
        // 连接仍然活跃，继续检查
        startInactivityCheck()
      }
    }, 2000)
  }
  
  // 启动初始检查
  startInactivityCheck()
  
  // 处理接收到的消息
  let executingTimerId: NodeJS.Timeout | null = null
  
  eventSource.onmessage = (event) => {
    // 更新最后活动时间
    lastActivityTime = Date.now()
    
    // 收到新消息，临时隐藏执行中状态
    if (isExecutingShown && chatRoomRef.value) {
      chatRoomRef.value.setMessageExecuting(false)
      isExecutingShown = false
      
      // 清除之前的定时器
      if (executingTimerId) {
        clearTimeout(executingTimerId)
      }
      
      // 设置新的定时器，如果1秒内没有收到新消息，重新显示执行中状态
      executingTimerId = setTimeout(() => {
        if (chatRoomRef.value && eventSource && eventSource.readyState === EventSource.OPEN) {
          chatRoomRef.value.setMessageExecuting(true)
          isExecutingShown = true
        }
      }, 1000)
    }
    
    if (event.data) {
      try {
        // 检查SSE响应是否为完成消息
        if (event.data.includes('[DONE]')) {
          if (inactivityTimer) {
            clearTimeout(inactivityTimer)
            inactivityTimer = null
          }
          if (executingTimerId) {
            clearTimeout(executingTimerId)
            executingTimerId = null
          }
          closeEventSource()  // 这里会调用setSseActive(false)
          if (chatRoomRef.value) {
            // 确保消息完成时"AI正在执行命令"的状态不会被清除
            // 只有当确认命令执行完成后，才清除执行中状态
            // 此处只完成消息本身，不清除执行状态
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
        // 错误处理
      }
    }
  }
  
  eventSource.onerror = (error) => {
    // 清除定时器
    if (inactivityTimer) {
      clearTimeout(inactivityTimer)
      inactivityTimer = null
    }
    if (executingTimerId) {
      clearTimeout(executingTimerId)
      executingTimerId = null
    }
    
    // 直接关闭连接，不尝试重连
    closeEventSource()  // 这里会调用setSseActive(false)
    
    if (chatRoomRef.value) {
      // 确保清除执行中状态
      chatRoomRef.value.setMessageExecuting(false)
      chatRoomRef.value.completeAiMessage()
    }
  }
}

// 关闭SSE连接
const closeEventSource = () => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
    
    // 通知ChatRoom组件SSE连接已关闭
    if (chatRoomRef.value) {
      chatRoomRef.value.setSseActive(false)
    }
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
  position: relative;
  overflow: hidden;
}

.app-content > :deep(.chat-container) {
  height: 100%;
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