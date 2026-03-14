import api from '@/config'
import type { SavingStreak } from '@/types/streak.types'

export const streakApi = {
  getStreak(): Promise<SavingStreak> {
    return api.get<SavingStreak>('/api/streak').then((r) => r.data)
  },
}
