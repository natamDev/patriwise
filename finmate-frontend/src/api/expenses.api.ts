import api from '@/config'
import type {
  CreateExpensePayload,
  Expense,
  UpdateExpensePayload,
} from '@/types/expense.types'

export const expensesApi = {
  create(payload: CreateExpensePayload): Promise<Expense> {
    return api.post<Expense>('/api/expenses', payload).then((r) => r.data)
  },

  list(month: string): Promise<Expense[]> {
    return api.get<Expense[]>('/api/expenses', { params: { month } }).then((r) => r.data)
  },

  update(id: string, payload: UpdateExpensePayload): Promise<Expense> {
    return api.patch<Expense>(`/api/expenses/${id}`, payload).then((r) => r.data)
  },

  delete(id: string): Promise<void> {
    return api.delete(`/api/expenses/${id}`).then(() => {})
  },
}
