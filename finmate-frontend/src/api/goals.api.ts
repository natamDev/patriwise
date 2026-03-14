import api from '@/config'
import type { Goal, CreateGoalPayload, UpdateGoalPayload, GoalProgress } from '@/types/goal.types'

export const goalsApi = {
  list(): Promise<Goal[]> {
    return api.get<Goal[]>('/api/goals').then((r) => r.data)
  },
  create(payload: CreateGoalPayload): Promise<Goal> {
    return api.post<Goal>('/api/goals', payload).then((r) => r.data)
  },
  update(id: string, payload: UpdateGoalPayload): Promise<Goal> {
    return api.patch<Goal>(`/api/goals/${id}`, payload).then((r) => r.data)
  },
  delete(id: string): Promise<void> {
    return api.delete(`/api/goals/${id}`).then(() => undefined)
  },
  getProgress(id: string): Promise<GoalProgress> {
    return api.get<GoalProgress>(`/api/goals/${id}/progress`).then((r) => r.data)
  },
  addContribution(id: string, amount: number): Promise<GoalProgress> {
    return api.post<GoalProgress>(`/api/goals/${id}/contributions`, { amount }).then((r) => r.data)
  },
}
