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
