import type { QuestionResult } from '@/types/question-result.ts'

export interface SurveyResults {
  surveyId: number
  surveyTitle: string
  questionResults: QuestionResult[]
}
