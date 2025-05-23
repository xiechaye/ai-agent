import axios from 'axios'

const BASE_URL = 'http://localhost:8123/api/ai'

export default {
  /**
   * 创建SSE连接 - AI恋爱大师基础模式
   * @param chatId 聊天ID
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveAppChatSse(chatId: string, message: string): EventSource {
    const url = `${BASE_URL}/love_app/chat/sse?chatId=${encodeURIComponent(chatId)}&message=${encodeURIComponent(message)}`
    return new EventSource(url)
  },

  /**
   * 创建SSE连接 - AI恋爱大师RAG模式
   * @param chatId 聊天ID
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveAppRagChatSse(chatId: string, message: string): EventSource {
    const url = `${BASE_URL}/love_app/chat/rag/sse?chatId=${encodeURIComponent(chatId)}&message=${encodeURIComponent(message)}`
    return new EventSource(url)
  },

  /**
   * 创建SSE连接 - AI恋爱大师工具模式
   * @param chatId 聊天ID
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveAppToolChatSse(chatId: string, message: string): EventSource {
    const url = `${BASE_URL}/love_app/chat/tool/sse?chatId=${encodeURIComponent(chatId)}&message=${encodeURIComponent(message)}`
    return new EventSource(url)
  },

  /**
   * 创建SSE连接 - AI超级智能体
   * @param message 消息内容
   * @returns EventSource实例
   */
  createLoveManusChatSse(message: string): EventSource {
    const url = `${BASE_URL}/love_manus/chat/sse?message=${encodeURIComponent(message)}`
    return new EventSource(url)
  }
} 