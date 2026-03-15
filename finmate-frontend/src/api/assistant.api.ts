import api from '@/config'

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  conceptCard?: ConceptCard
  fomoAlert?: FomoAlert
}

export interface ConceptCard {
  name: string
  definition: string
  example: string
  risk: string
  simpleSummary: string
}

export interface FomoAlert {
  biasType: string
  triggerCategory: string
  explanation: string
  alternative: string
}

export interface ChatResponse {
  conversationId: string
  reply: string
  conceptCard: ConceptCard | null
  fomoAlert: FomoAlert | null
}

export interface Recommendation {
  id: string
  recommendationType: string
  message: string
  suggestedAction: string
  createdAt: string
}

export interface FinancialAnalysis {
  analysis: string
  expenseByCategory: Record<string, number>
  savingCapacity: number
  spendingAlerts: string[]
}

export interface GoalSummary {
  goalId: string
  goalName: string
  goalType: string
  targetAmount: number
  savedAmount: number
  remainingAmount: number
  percent: number
  monthsNeeded: number | null
  estimatedCompletionDate: string | null
  monthlyContribution: number
  optimizedContribution: number | null
}

export interface SavingsCoaching {
  coaching: string
  goals: GoalSummary[]
}

const CATEGORY_LABELS: Record<string, string> = {
  HOUSING: 'Logement',
  TRANSPORT: 'Transport',
  FOOD: 'Alimentation',
  SUBSCRIPTIONS: 'Abonnements',
  ENTERTAINMENT: 'Loisirs',
  SHOPPING: 'Shopping',
  HEALTH: 'Santé',
  OTHER: 'Autres',
}

export function categoryLabel(cat: string): string {
  return CATEGORY_LABELS[cat] ?? cat
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
  financialAnalysis(): Promise<FinancialAnalysis> {
    return api.post<FinancialAnalysis>('/api/assistant/financial-analysis').then((r) => r.data)
  },
  savingsCoaching(): Promise<SavingsCoaching> {
    return api.post<SavingsCoaching>('/api/assistant/savings-coaching').then((r) => r.data)
  },
}
