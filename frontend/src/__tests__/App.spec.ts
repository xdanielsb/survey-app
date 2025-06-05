import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import App from '@/App.vue'

// Mocks for router, logger, and store
let _auth = { isAuthenticated: false, email: '', isPremium: false, logout: vi.fn() }

vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
  RouterView: { template: '<div>RouterView</div>' },
  RouterLink: { template: '<a><slot /></a>' },
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
    const wrapper = mount(App)
    await flushPromises()
    expect(wrapper.find('button').text().toLowerCase()).toContain('login')
  })

  it('shows user email and logout when logged in', async () => {
    _auth.isAuthenticated = true
    _auth.email = 'alice@example.com'
    _auth.isPremium = true
    const wrapper = mount(App)
    await flushPromises()
    expect(wrapper.text()).toContain('alice@example.com')
    expect(wrapper.find('button').text().toLowerCase()).toContain('logout')
    expect(wrapper.find('svg.text-yellow-500').exists()).toBe(true)
  })
})
