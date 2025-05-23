export enum ChatRole {
  USER = 'user',
  AI = 'ai'
}

export interface ChatMessage {
  id: string
  role: ChatRole
  content: string
  timestamp: number
}

export enum LoveAppMode {
  BASIC = 'basic',
  RAG = 'rag',
  TOOL = 'tool'
} 