import { mount } from '@vue/test-utils'
import SurveyQuestionInput from '../SurveyQuestionInput.vue'
import { describe, it, expect } from 'vitest'
import type { Question } from '@/types/question.ts'

describe('SurveyQuestionInput.vue', () => {
  it('mounts without errors', () => {
    const dummyQuestion: Question = { id: 42, questionText: 'Sample Question?', position: 1 }
    const wrapper = mount(SurveyQuestionInput, {
      props: { question: dummyQuestion, likertOptions: [] },
    })
    expect(wrapper.exists()).toBe(true)
  })
})
