import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import HomeView from '../HomeView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from '@/router'
import { createTestingPinia } from '@pinia/testing'
import { fetchSurveys } from '@/services/surveyService'

vi.mock('@/services/surveyService', () => ({
  fetchSurveys: vi.fn(),
}))

const router = createRouter({
  history: createWebHistory(),
  routes,
})

describe('HomeView.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('displays paginated surveys and pagination controls', async () => {
    ;(fetchSurveys as unknown as ReturnType<typeof vi.fn>).mockResolvedValue({
      content: [
        { id: 1, title: 'Customer Feedback' },
        { id: 2, title: 'Product Satisfaction' },
      ],
      totalPages: 3,
      totalElements: 15,
    })

    const wrapper = mount(HomeView, {
      global: {
        plugins: [createTestingPinia({ createSpy: vi.fn }), router],
        stubs: {
          RouterLink: true,
        },
      },
    })

    // Wait for reactivity + watchEffect to finish
    await new Promise((resolve) => setTimeout(resolve, 0))

    expect(wrapper.text()).toContain('Customer Feedback')
    expect(wrapper.text()).toContain('Product Satisfaction')
    expect(wrapper.text()).toContain('Page 1 of 3')
  })
})
