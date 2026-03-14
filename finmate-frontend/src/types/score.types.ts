export interface FinancialScore {
  score: number
  label: 'poor' | 'improving' | 'healthy'
  explanation: string
  savingsRateScore: number
  expenseControlScore: number
  financialStabilityScore: number
  goalProgressScore: number
}
