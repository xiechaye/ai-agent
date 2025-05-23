export enum ChatRole {
  USER = 'user',
  AI = 'ai'
}

export interface ChatMessage {
  id: string
  role: ChatRole
  content: string
  timestamp: number
  fileLink?: string  // 可选的文件下载链接
  isExecuting?: boolean // 是否正在执行中
}

export enum LoveAppMode {
  BASIC = 'basic',
  RAG = 'rag',
  TOOL = 'tool'
} 