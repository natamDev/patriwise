import type { ExpenseCategory } from './expense.types'

export interface BudgetSummary {
  monthlyIncome: number
  totalExpenses: number
  remainingIncome: number
  savingsRate: number
  expensesByCategory: Partial<Record<ExpenseCategory, number>>
  month: string
}

export interface MonthTrend {
  month: string
  income: number
  expenses: number
}
