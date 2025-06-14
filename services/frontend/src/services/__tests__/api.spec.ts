// api.test.ts
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { onResponseError } from '../api'

const mockStore = { logout: vi.fn(), token: 'abc' } // <-- ONE instance

vi.mock('@/stores/authStore', () => ({
  useAuthStore: () => mockStore, // <-- always the same
}))
vi.mock('@/services/toastService', () => ({
  toastService: { warning: vi.fn() },
}))
vi.mock('@/router', () => ({
  default: { push: vi.fn() },
}))
vi.mock('@/plugins/logger', () => ({
  logger: { error: vi.fn() },
}))

const toast = (await import('@/services/toastService')).toastService
const router = (await import('@/router')).default
const logger = (await import('@/plugins/logger')).logger

describe('api response interceptor', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    vi.useFakeTimers()
  })

  it('handles 401 by logging out, showing toast, and redirecting', async () => {
    await onResponseError({ response: { status: 401 } }).catch(() => null)

    expect(toast.warning).toHaveBeenCalledWith('[Auth] Session expired, please login again.')

    // Simulate the 3-second delay
    vi.advanceTimersByTime(3000)

    expect(mockStore.logout).toHaveBeenCalled()
    expect(router.push).toHaveBeenCalledWith('/')
  })

  it('logs server HTTP errors', async () => {
    await onResponseError({
      response: { status: 500, data: { msg: 'Boom' } },
    }).catch(() => null)

    expect(logger.error).toHaveBeenCalledWith('HTTP 500:', { msg: 'Boom' })
  })
})
