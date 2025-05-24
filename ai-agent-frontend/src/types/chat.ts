export const ChatRole = {
  USER: 'user',
  AI: 'ai'
} as const

export type ChatRole = typeof ChatRole[keyof typeof ChatRole]

export interface ChatMessage {
  id: string
  role: ChatRole
  content: string
  timestamp: number
  fileLink?: string  // 可选的文件下载链接
  isExecuting?: boolean // 是否正在执行命令
}

export const LoveAppMode = {
  BASIC: 'basic',
  RAG: 'rag',
  TOOL: 'tool'
} as const

export type LoveAppMode = typeof LoveAppMode[keyof typeof LoveAppMode] 