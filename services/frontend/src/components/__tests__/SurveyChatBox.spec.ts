import { mount } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import SurveyChatBox from '../SurveyChatBox.vue'

vi.mock('@/services/surveyService', () => ({
  askSurveyQuestion: vi.fn().mockResolvedValue({ answer: 'pong' }),
}))

describe('SurveyChatBox.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('sends question and displays response', async () => {
    const wrapper = mount(SurveyChatBox, { props: { surveyId: 1 } })
    const input = wrapper.find('input')
    await input.setValue('ping')
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('ping')
    await wrapper.vm.$nextTick()
    expect(wrapper.text()).toContain('pong')
  })
})
