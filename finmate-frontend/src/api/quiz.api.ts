import api from '@/config'
import type { QuizQuestion, AnswerResult, QuizStats } from '@/types/quiz.types'

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
