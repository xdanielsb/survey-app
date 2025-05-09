export interface QuestionResult {
  questionId: number
  questionText: string
  totalResponses: number
  averageScore: number
  distribution: Record<string, number>
}
