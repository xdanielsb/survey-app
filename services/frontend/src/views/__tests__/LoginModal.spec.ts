import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginModal from '@/views/LoginView.vue'

vi.mock('@/services/authService', () => ({
  loginUser: vi.fn(),
  signUpUser: vi.fn(),
}))
vi.mock('@/services/toastService', () => ({
  toastService: { success: vi.fn(), error: vi.fn() },
}))
vi.mock('@/stores/authStore.ts', () => ({
  useAuthStore: () => ({ login: vi.fn() }),
}))
vi.mock('@/services/api.ts', () => ({
  default: { post: vi.fn() },
}))
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() }),
}))

describe('LoginView.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders modal when modelValue is true', () => {
    const wrapper = mount(LoginModal, {
      props: { modelValue: true },
      global: { stubs: { teleport: true } },
    })
    expect(wrapper.html()).toContain('Welcome back')
    expect(wrapper.find('button').text()).toContain('Continue with Google')
    expect(wrapper.find('input[type="email"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
  })

  it('does not render modal when modelValue is false', () => {
    const wrapper = mount(LoginModal, {
      props: { modelValue: false },
      global: { stubs: { teleport: true } },
    })
    expect(wrapper.html()).not.toContain('Welcome back')
  })

  it('emits update:modelValue=false when backdrop is clicked', async () => {
    const wrapper = mount(LoginModal, {
      props: { modelValue: true },
      global: { stubs: { teleport: true } },
    })
    const backdrop = wrapper.find('.fixed.inset-0')
    expect(backdrop.exists()).toBe(true)
    await backdrop.trigger('click.self')
    expect(wrapper.emitted()['update:modelValue']).toBeTruthy()
    expect(wrapper.emitted()['update:modelValue'][0]).toEqual([false])
  })
})
