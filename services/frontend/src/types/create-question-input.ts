export interface CreateSurveyInput {
  title: string
  questions: CreateQuestionInput[]
}

export interface CreateQuestionInput {
  questionText: string
  position: number
}
