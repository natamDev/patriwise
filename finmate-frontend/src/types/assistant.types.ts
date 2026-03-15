export interface BiasAlert {
  biasType: string
  biasLabel: string
  explanation: string
  alternative: string
}

export interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  conceptCard?: ConceptCard
  fomoAlert?: FomoAlert
  biasAlert?: BiasAlert
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
  biasAlert: BiasAlert | null
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

export interface InvestmentExplanation {
  topic: string
  topicLabel: string
  definition: string
  example: string
  risk: string
  simpleSummary: string
  keyPoints: string[]
}

export interface Motivation {
  message: string
  currentStreak: number
  longestStreak: number
  financialScore: number
  scoreLabel: string
  averageGoalProgress: number
  activeGoals: number
}

export interface ProjectionScenario {
  label: string
  returnPct: number
  capitalInvested: number
  capitalFinal: number
  interestGain: number
}

export interface FinancialProjection {
  monthlyInvestment: number
  horizonYears: number
  currency: string
  conservative: ProjectionScenario
  moderate: ProjectionScenario
  optimistic: ProjectionScenario
  explanation: string
}

export interface DecisionCoaching {
  sessionId: string
  recommendation: string
  decisionContext: string
  whyInvesting: string
  investmentHorizon: string
  riskTolerance: string
  financialGoal: string
}

export interface RiskExplanation {
  topic: string
  topicLabel: string
  definition: string
  example: string
  historicalExample: string
  howToReact: string
  simpleSummary: string
  keyPoints: string[]
}

export interface InvestmentSimulation {
  monthlyInvestment: number
  expectedReturn: number
  horizonYears: number
  capitalInvested: number
  capitalFinal: number
  interestGain: number
  explanation: string
}

export const RISK_TOPICS = [
  { key: 'MARKET_VOLATILITY', label: '📉 Volatilité', description: 'Fluctuations normales' },
  { key: 'MARKET_CYCLES', label: '🔁 Cycles de marché', description: 'Hauts et bas récurrents' },
  { key: 'MARKET_CRASHES', label: '💥 Crises boursières', description: 'Effondrements historiques' },
  { key: 'DIVERSIFICATION', label: '🌍 Diversification', description: 'Réduire le risque' },
] as const

export const INVESTMENT_TOPICS = [
  { key: 'ETF', label: '📈 ETF', description: 'Fonds indiciels cotés' },
  { key: 'DIVERSIFICATION', label: '🌍 Diversification', description: 'Répartir les risques' },
  { key: 'COMPOUND_INTEREST', label: '🔄 Intérêts composés', description: 'La magie du temps' },
  { key: 'LONG_TERM_INVESTING', label: '⏳ Long terme', description: 'Investir avec patience' },
] as const

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
