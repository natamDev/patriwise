import api from '@/config'
import type {
  ChatResponse,
  DecisionCoaching,
  FinancialAnalysis,
  FinancialProjection,
  InvestmentExplanation,
  InvestmentSimulation,
  Motivation,
  Recommendation,
  RiskExplanation,
  SavingsCoaching,
} from '@/types/assistant.types'

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
  investmentEducation(topic: string): Promise<InvestmentExplanation> {
    return api.post<InvestmentExplanation>('/api/assistant/investment-education', { topic }).then((r) => r.data)
  },
  motivation(): Promise<Motivation> {
    return api.post<Motivation>('/api/assistant/motivation').then((r) => r.data)
  },
  financialProjection(): Promise<FinancialProjection> {
    return api.post<FinancialProjection>('/api/assistant/financial-projection').then((r) => r.data)
  },
  decisionCoaching(
    decisionContext: string, whyInvesting: string, investmentHorizon: string,
    riskTolerance: string, financialGoal: string
  ): Promise<DecisionCoaching> {
    return api.post<DecisionCoaching>('/api/assistant/decision-coaching', {
      decisionContext, whyInvesting, investmentHorizon, riskTolerance, financialGoal,
    }).then((r) => r.data)
  },
  riskEducation(topic: string): Promise<RiskExplanation> {
    return api.post<RiskExplanation>('/api/assistant/risk-education', { topic }).then((r) => r.data)
  },
  investmentSimulation(monthlyInvestment: number, expectedReturn: number, horizonYears: number): Promise<InvestmentSimulation> {
    return api.post<InvestmentSimulation>('/api/assistant/investment-simulation', {
      monthlyInvestment, expectedReturn, horizonYears,
    }).then((r) => r.data)
  },
}
