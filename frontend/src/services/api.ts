import axios from 'axios'
import { toastService } from '@/services/toastService'
import { useAuthStore } from '@/stores/authStore'
import router from '@/router'
import { logger } from '@/plugins/logger'

const baseURL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL,
  timeout: 10000,
})

// Request interceptor: inject JWT token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      toastService.warning('[Auth] Session expired, please login again.')
      authStore.logout()
      router.push('/login')
    }

    if (error.response) {
      logger.error(`HTTP ${error.response.status}:`, error.response.data)
    } else {
      logger.error('Axios network/server error:', error)
    }
    return Promise.reject(error)
  },
)

export default api
