import type { Answer } from '@/types/answer.ts'

export interface SurveyResponse {
  surveyId: number
  answers: Answer[]
}
