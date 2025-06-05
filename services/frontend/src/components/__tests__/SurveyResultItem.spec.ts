import { mount } from '@vue/test-utils'
import SurveyResultItem from '../SurveyResultItem.vue'
import { describe, it, expect } from 'vitest'
import type { QuestionResult } from '@/types/question-result.ts'

describe('SurveyResultItem.vue', () => {
  it('mounts without errors with minimal valid props', () => {
    const question: QuestionResult = {
      questionId: 1,
      questionText: 'What is your favorite color?',
      totalResponses: 100,
      averageScore: 4.5,
      distribution: {
        'Very Satisfied': 60,
        Neutral: 25,
        Unsatisfied: 15,
      },
    }

    const wrapper = mount(SurveyResultItem, {
      props: { question },
    })
    expect(wrapper.exists()).toBe(true)
  })
})
