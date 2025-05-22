import { object, string, array } from 'yup'
import type { InferType } from 'yup'

export interface QuestionInput {
  text: string
}

export interface SurveyForm {
  title: string
  questions: QuestionInput[]
}

const surveySchema = object({
  title: string().required('Survey title is required'),
  questions: array()
    .of(
      object({
        text: string().required('Question cannot be empty'),
      }),
    )
    .min(1, 'At least one question is required'),
})

export type SurveyFormSchema = InferType<typeof surveySchema>
export const typedSurveySchema = surveySchema
