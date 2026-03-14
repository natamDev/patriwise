import api from '@/config'
import type { FinancialScore } from '@/types/score.types'

export const scoreApi = {
  getScore(): Promise<FinancialScore> {
    return api.get<FinancialScore>('/api/score').then((r) => r.data)
  },
}
