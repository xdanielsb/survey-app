import type { SurveyResultQuestion } from '@/types/survey-resutls-question.ts'

export interface SurveyResults {
  title: string;
  questions: SurveyResultQuestion[];
}
