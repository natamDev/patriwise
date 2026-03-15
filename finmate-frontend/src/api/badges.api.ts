import api from '@/config'
import type { BadgeStatus } from '@/types/badge.types'

export const badgesApi = {
  getBadges(): Promise<BadgeStatus[]> {
    return api.get<BadgeStatus[]>('/api/badges').then((r) => r.data)
  },
}
