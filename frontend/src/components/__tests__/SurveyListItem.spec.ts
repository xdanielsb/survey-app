import { mount } from '@vue/test-utils'
import SurveyListItem from '../SurveyListItem.vue'
import { describe, it, expect } from 'vitest'
import type { Survey } from '@/types/survey.ts'

describe('SurveyListItem.vue', () => {
  it('mounts without errors', () => {
    const dummySurvey: Survey = { id: 1, title: 'Test Survey', questions: [] }
    const wrapper = mount(SurveyListItem, {
      props: { survey: dummySurvey },
    })
    expect(wrapper.exists()).toBe(true)
  })
})
