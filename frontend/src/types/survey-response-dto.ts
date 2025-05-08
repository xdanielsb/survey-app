import type { AnswerDTO } from '@/types/answer-dto.ts'

export interface SurveyResponseDTO {
  surveyId: number;
  answers: AnswerDTO[];
}
