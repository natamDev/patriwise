export type ExpenseCategory =
  | 'HOUSING'
  | 'TRANSPORT'
  | 'FOOD'
  | 'SUBSCRIPTIONS'
  | 'ENTERTAINMENT'
  | 'SHOPPING'
  | 'HEALTH'
  | 'OTHER'

export const CATEGORY_LABELS: Record<ExpenseCategory, string> = {
  HOUSING: 'Logement',
  TRANSPORT: 'Transport',
  FOOD: 'Alimentation',
  SUBSCRIPTIONS: 'Abonnements',
  ENTERTAINMENT: 'Loisirs',
  SHOPPING: 'Shopping',
  HEALTH: 'Santé',
  OTHER: 'Autre',
}

export interface Expense {
  id: string
  userId: string
  amount: number
  category: ExpenseCategory
  description: string
  expenseDate: string
  createdAt: string
}

export interface CreateExpensePayload {
  amount: number
  category: ExpenseCategory
  description: string
  expenseDate: string
}

export interface UpdateExpensePayload {
  amount?: number
  category?: ExpenseCategory
  description?: string
  expenseDate?: string
}
