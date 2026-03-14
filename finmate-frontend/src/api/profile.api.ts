import api from '@/config'
import type {
  CreateFinancialProfilePayload,
  FinancialProfile,
  UpdateFinancialProfilePayload,
} from '@/types/profile.types'

export const profileApi = {
  create(payload: CreateFinancialProfilePayload): Promise<FinancialProfile> {
    return api.post<FinancialProfile>('/api/profile', payload).then((r) => r.data)
  },

  get(): Promise<FinancialProfile> {
    return api.get<FinancialProfile>('/api/profile').then((r) => r.data)
  },

  update(payload: UpdateFinancialProfilePayload): Promise<FinancialProfile> {
    return api.patch<FinancialProfile>('/api/profile', payload).then((r) => r.data)
  },
}
