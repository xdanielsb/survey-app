import axios from 'axios'
import { toastService } from '@/services/toastService'
import { useAuthStore } from '@/stores/authStore'
import router from '@/router'
import { logger } from '@/plugins/logger'

const baseURL = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080'

export const api = axios.create({
  baseURL,
  timeout: 10_000,
})

api.interceptors.request.use(
  (config) => {
    const { token } = useAuthStore()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const onResponseError = async (error: any) => {
  if (error.response?.status === 403) {
    const authStore = useAuthStore()
    toastService.warning('[Auth] Session expired, please login again.')
    authStore.logout()
    router.push('/')
  }

  if (error.response) {
    logger.error(`HTTP ${error.response.status}:`, error.response.data)
  } else {
    logger.error('Axios network/server error:', error)
  }

  return Promise.reject(error)
}

api.interceptors.response.use((res) => res, onResponseError)

export default api
