import api from '@/config'

export interface BadgeStatus {
  id: string
  name: string
  description: string
  icon: string
  unlocked: boolean
  unlockedAt: string | null
}

export const badgesApi = {
  getBadges(): Promise<BadgeStatus[]> {
    return api.get<BadgeStatus[]>('/api/badges').then((r) => r.data)
  },
}
