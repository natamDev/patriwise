import api from '@/config'

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  conceptCard?: ConceptCard
}

export interface ConceptCard {
  name: string
  definition: string
  example: string
  risk: string
  simpleSummary: string
}

export interface ChatResponse {
  conversationId: string
  reply: string
  conceptCard: ConceptCard | null
}

export interface Recommendation {
  id: string
  recommendationType: string
  message: string
  suggestedAction: string
  createdAt: string
}

export const assistantApi = {
  chat(conversationId: string | null, message: string): Promise<ChatResponse> {
    return api
      .post<ChatResponse>('/api/assistant/chat', { conversationId, message })
      .then((r) => r.data)
  },
  coaching(): Promise<Recommendation> {
    return api.post<Recommendation>('/api/assistant/coaching').then((r) => r.data)
  },
}
