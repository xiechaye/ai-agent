// import axios from 'axios'


// 根据环境变量设置 API 基础 URL
const BASE_URL = import.meta.env.MODE === 'development'
    ? 'http://localhost:8123/api' // 生产环境使用相对路径，适用于前后端部署在同一域名下
    : '/api' // 开发环境指向本地后端服务


export default {
  /**
   * 创建SSE连接 - AI恋爱大师基础模式
   * @param chatId 聊天ID
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveAppChatSse(chatId: string, message: string): EventSource {
    const url = `${BASE_URL}/ai/love_app/chat/sse?chatId=${encodeURIComponent(chatId)}&message=${encodeURIComponent(message)}`
    const eventSource = new EventSource(url)
    
    // 添加错误处理，区分正常关闭和真正的错误
    eventSource.onerror = () => {
      // 当SSE连接关闭时，readyState会变为CLOSED (2)
      if (eventSource.readyState === EventSource.CLOSED) {
        // 正常关闭，无需报错，可以在此执行完成后的逻辑
        eventSource.close()
      }
      // 真正的错误情况也不输出到控制台
    }
    
    return eventSource
  },

  /**
   * 创建SSE连接 - AI恋爱大师RAG模式
   * @param chatId 聊天ID
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveAppRagChatSse(chatId: string, message: string): EventSource {
    const url = `${BASE_URL}/ai/love_app/chat/rag/sse?chatId=${encodeURIComponent(chatId)}&message=${encodeURIComponent(message)}`
    const eventSource = new EventSource(url)
    
    // 添加错误处理，区分正常关闭和真正的错误
    eventSource.onerror = () => {
      // 当SSE连接关闭时，readyState会变为CLOSED (2)
      if (eventSource.readyState === EventSource.CLOSED) {
        // 正常关闭，无需报错，可以在此执行完成后的逻辑
        eventSource.close()
      }
      // 真正的错误情况也不输出到控制台
    }
    
    return eventSource
  },

  /**
   * 创建SSE连接 - AI恋爱大师工具模式
   * @param chatId 聊天ID
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveAppToolChatSse(chatId: string, message: string): EventSource {
    const url = `${BASE_URL}/ai/love_app/chat/tool/sse?chatId=${encodeURIComponent(chatId)}&message=${encodeURIComponent(message)}`
    const eventSource = new EventSource(url)
    
    // 添加错误处理，区分正常关闭和真正的错误
    eventSource.onerror = () => {
      // 当SSE连接关闭时，readyState会变为CLOSED (2)
      if (eventSource.readyState === EventSource.CLOSED) {
        // 正常关闭，无需报错，可以在此执行完成后的逻辑
        eventSource.close()
      }
      // 真正的错误情况也不输出到控制台
    }
    
    return eventSource
  },

  /**
   * 创建SSE连接 - AI超级智能体
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveManusChatSse(message: string): EventSource {
    // 双重编码防止URL解析问题
    const encodedMessage = encodeURIComponent(message)
    const url = `${BASE_URL}/ai/love_manus/chat/sse?message=${encodedMessage}`

    // 创建EventSource对象，设置withCredentials为true以便在跨域请求中发送cookies
    const eventSource = new EventSource(url, { withCredentials: true })
    
    // 添加错误处理，区分正常关闭和真正的错误
    eventSource.onerror = () => {
      // 当SSE连接关闭时，readyState会变为CLOSED (2)
      if (eventSource.readyState === EventSource.CLOSED) {
        // 正常关闭，无需报错，可以在此执行完成后的逻辑
        eventSource.close()
      }
      // 真正的错误情况也不输出到控制台
    }

    return eventSource
  }
}
