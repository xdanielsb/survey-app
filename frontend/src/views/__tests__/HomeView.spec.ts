import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import HomeView from '../HomeView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from '@/router/index.ts'
import axios from 'axios' // adjust if your route definitions are elsewhere

createRouter({
  history: createWebHistory(),
  routes,
})

vi.mock('axios')
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
