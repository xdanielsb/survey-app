import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import LoginView from '@/views/LoginView.vue'

const push = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
}))

const loginUserAndPassword = vi.fn()
const loginWithGoogle = vi.fn()
const signUpUser = vi.fn()

vi.mock('@/services/authService', () => ({
  loginUserAndPassword: (...a: unknown[]) => loginUserAndPassword(...a),
  loginWithGoogle: (...a: unknown[]) => loginWithGoogle(...a),
  signUpUser: (...a: unknown[]) => signUpUser(...a),
}))

const success = vi.fn()
const error = vi.fn()

vi.mock('@/services/toastService', () => ({
  toastService: {
    success: (...a: unknown[]) => success(...a),
    error: (...a: unknown[]) => error(...a),
  },
}))

vi.mock('@/stores/authStore.ts', () => ({
  useAuthStore: () => ({ isPremium: true }),
}))

describe('LoginView.vue actions', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('submits credentials and closes on success', async () => {
    loginUserAndPassword.mockResolvedValue({ success: true })
    const wrapper = mount(LoginView, {
      props: { modelValue: true },
      global: { stubs: { teleport: true } },
    })

    await wrapper.find('input[type="email"]').setValue('user@example.com')
    await wrapper.find('input[type="password"]').setValue('pass')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(loginUserAndPassword).toHaveBeenCalledWith('user@example.com', 'pass')
    expect(push).toHaveBeenCalledWith('/')
    expect(wrapper.emitted()['update:modelValue']).toEqual([[false]])
  })

  it('shows error when login fails', async () => {
    loginUserAndPassword.mockResolvedValue({ success: false })
    const wrapper = mount(LoginView, {
      props: { modelValue: true },
      global: { stubs: { teleport: true } },
    })

    await wrapper.find('input[type="email"]').setValue('bad@user.com')
    await wrapper.find('input[type="password"]').setValue('wrong')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    expect(wrapper.text()).toContain('Invalid credentials')
  })

  it('handles successful Google login for premium user', async () => {
    loginWithGoogle.mockResolvedValue({ success: true })
    const wrapper = mount(LoginView, {
      props: { modelValue: true },
      global: { stubs: { teleport: true } },
    })
    const googleBtn = wrapper.findAll('button').find((b) => b.text().includes('Google'))
    await googleBtn!.trigger('click')
    await flushPromises()

    expect(loginWithGoogle).toHaveBeenCalled()
    expect(success).toHaveBeenCalledWith('Welcome back, premium user! 🎉')
  })
})
