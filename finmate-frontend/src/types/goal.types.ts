export type GoalType = 'TRAVEL' | 'EMERGENCY_FUND' | 'INVESTMENT' | 'PURCHASE' | 'OTHER'

export const GOAL_TYPE_LABELS: Record<GoalType, string> = {
  TRAVEL: 'Voyage',
  EMERGENCY_FUND: "Fonds d'urgence",
  INVESTMENT: 'Investissement',
  PURCHASE: 'Achat',
  OTHER: 'Autre',
}

export interface Goal {
  id: string
  goalName: string
  goalType: GoalType
  targetAmount: number
  targetDate: string
  monthlyContribution: number
  createdAt: string
}

export interface CreateGoalPayload {
  goalName: string
  goalType: GoalType
  targetAmount: number
  targetDate: string
  monthlyContribution: number
}

export interface UpdateGoalPayload {
  goalName?: string
  goalType?: GoalType
  targetAmount?: number
  targetDate?: string
  monthlyContribution?: number
}

export interface GoalProgress {
  goalId: string
  savedAmount: number
  targetAmount: number
  remainingAmount: number
  percent: number
  monthsNeeded: number | null
  estimatedCompletionDate: string | null
}
