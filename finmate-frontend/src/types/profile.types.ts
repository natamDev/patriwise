export type EmploymentStatus = 'STUDENT' | 'EMPLOYEE' | 'FREELANCER' | 'UNEMPLOYED'
export type FinancialExperienceLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'

export interface FinancialProfile {
  id: string
  userId: string
  monthlyIncome: number
  employmentStatus: EmploymentStatus
  age: number
  financialExperienceLevel: FinancialExperienceLevel
  currency: string
  createdAt: string
  updatedAt: string
}

export interface CreateFinancialProfilePayload {
  monthlyIncome: number
  employmentStatus: EmploymentStatus
  age: number
  financialExperienceLevel: FinancialExperienceLevel
  currency: string
}

export interface UpdateFinancialProfilePayload {
  monthlyIncome?: number
  employmentStatus?: EmploymentStatus
  age?: number
  financialExperienceLevel?: FinancialExperienceLevel
  currency?: string
}
