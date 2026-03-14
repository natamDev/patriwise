import api from '@/config'
import type { BudgetSummary, MonthTrend } from '@/types/budget.types'

export const budgetApi = {
  getSummary(month: string): Promise<BudgetSummary> {
    return api.get<BudgetSummary>('/api/budget/summary', { params: { month } }).then((r) => r.data)
  },
  getTrend(months = 6): Promise<MonthTrend[]> {
    return api.get<MonthTrend[]>('/api/budget/trend', { params: { months } }).then((r) => r.data)
  },
}
