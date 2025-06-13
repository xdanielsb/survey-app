import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import App from '@/App.vue'
import { createPinia } from 'pinia'

// Mocks for router, logger, and store
let _auth = { isAuthenticated: false, email: '', isPremium: false, logout: vi.fn() }

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
  RouterView: { template: '<div>RouterView</div>' },
  RouterLink: { template: '<a><slot /></a>' },
  createRouter: () => ({
    beforeEach: vi.fn(),
  }),
  createWebHistory: vi.fn(),
}))

vi.mock('@/plugins/logger', () => ({
  logger: { info: vi.fn() },
}))

vi.mock('@/stores/authStore', () => ({
  useAuthStore: () => _auth,
}))

describe('App.vue', () => {
  beforeEach(() => {
    // Reset mock store state before each test
    _auth = { isAuthenticated: false, email: '', isPremium: false, logout: vi.fn() }
  })
  afterEach(() => {
    vi.clearAllMocks()
  })

  it('mounts without errors and shows login button when logged out', async () => {
    _auth.isAuthenticated = false
    const wrapper = mount(App, { global: { plugins: [createPinia()] } })
    await flushPromises()
    const hasLogin = wrapper.findAll('button').some((b) => b.text().toLowerCase().includes('login'))
    expect(hasLogin).toBe(true)
  })

  it('shows user email and dropdown menu when logged in', async () => {
    _auth.isAuthenticated = true
    _auth.email = 'topg@daniel.com'
    _auth.isPremium = true
    const wrapper = mount(App, { global: { plugins: [createPinia()] } })
    await flushPromises()
    expect(wrapper.text()).toContain('topg@daniel.com')
    const options = wrapper.findAll('option').map((o) => o.text().toLowerCase())
    expect(options).toContain('buy credits')
  })
})
