import type { SurveyForm } from '@/validation/surveySchema.ts'
import { typedSurveySchema } from '@/validation/surveySchema.ts'

import { describe, it, expect, assert } from 'vitest'
describe('typedSurveySchema', () => {
  const createSurvey = (overrides?: Partial<SurveyForm>): SurveyForm => ({
    title: 'Customer Feedback Survey',
    questions: [{ text: 'How satisfied are you with our service?' }],
    ...overrides,
  })

  describe('valid cases', () => {
    it('should validate a complete and correct survey', async () => {
      const survey = createSurvey()
      await expect(typedSurveySchema.validate(survey)).resolves.toEqual(survey)
    })
  })

  describe('invalid cases', () => {
    it('should throw if title is missing', async () => {
      const { title, ...rest } = createSurvey()
      assert(title.length > 0) // To satisfy TypeScript (unused var)
      await expect(typedSurveySchema.validate(rest)).rejects.toThrow('Survey title is required')
    })

    it('should throw if questions array is empty', async () => {
      const survey = createSurvey({ questions: [] })
      await expect(typedSurveySchema.validate(survey)).rejects.toThrow(
        'At least one question is required',
      )
    })

    it('should throw if a question text is empty', async () => {
      const survey = createSurvey({ questions: [{ text: '' }] })
      await expect(typedSurveySchema.validate(survey)).rejects.toThrow('Question cannot be empty')
    })

    it('should validate multiple valid questions', async () => {
      const survey = createSurvey({
        questions: [
          { text: 'What do you like about our product?' },
          { text: 'What could we improve?' },
        ],
      })

      await expect(typedSurveySchema.validate(survey)).resolves.toEqual(survey)
    })

    it('should throw if any question in a multi-question array is invalid', async () => {
      const survey = createSurvey({
        questions: [{ text: 'Good question' }, { text: '' }],
      })

      await expect(typedSurveySchema.validate(survey)).rejects.toThrow('Question cannot be empty')
    })
  })
})
