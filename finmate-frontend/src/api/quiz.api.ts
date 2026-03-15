import api from '@/config'

export interface QuizQuestion {
  id: string
  question: string
  options: string[]
}

export interface AnswerResult {
  correct: boolean
  explanation: string
  correctAnswer: number
}

export interface QuizStats {
  answered: number
  correct: number
}

export const quizApi = {
  getAll(): Promise<QuizQuestion[]> {
    return api.get<QuizQuestion[]>('/api/quizzes').then((r) => r.data)
  },
  answer(id: string, selectedOption: number): Promise<AnswerResult> {
    return api.post<AnswerResult>(`/api/quizzes/${id}/answer`, { selectedOption }).then((r) => r.data)
  },
  getStats(): Promise<QuizStats> {
    return api.get<QuizStats>('/api/quizzes/stats').then((r) => r.data)
  },
}
