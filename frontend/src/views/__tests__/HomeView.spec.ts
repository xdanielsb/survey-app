import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import HomeView from '../HomeView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from '@/router/index.ts'
import { createTestingPinia } from '@pinia/testing'
import axios from 'axios' // adjust if your route definitions are elsewhere

const router = createRouter({
  history: createWebHistory(),
  routes,
})

vi.mock('axios', () => {
  const mockAxiosInstance = {
    get: vi.fn(),
    post: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  }

  return {
    default: {
      create: vi.fn(() => mockAxiosInstance),
      ...mockAxiosInstance,
    },
  }
})

vi.mock('@/services/surveyService', () => ({
  fetchSurveys: vi.fn().mockResolvedValue([
    { id: 1, title: 'Customer Feedback' },
    { id: 2, title: 'Product Satisfaction' },
  ]),
}))

const mockedAxios = vi.mocked(axios, true)

describe('HomeView', () => {
  beforeEach(() => {
    mockedAxios.get.mockResolvedValue({
      data: [
        { id: 1, title: 'Customer Feedback' },
        { id: 2, title: 'Product Satisfaction' },
      ],
    })
  })

  it('displays surveys from API', async () => {
    const wrapper = mount(HomeView, {
      global: {
        plugins: [
          createTestingPinia({
            createSpy: vi.fn,
          }),
          router,
        ],
        stubs: {
          RouterLink: true,
        },
      },
    })

    await new Promise((resolve) => setTimeout(resolve)) // wait for `onMounted` to resolve

    expect(wrapper.text()).toContain('Customer Feedback')
    expect(wrapper.text()).toContain('Product Satisfaction')
  })
})
