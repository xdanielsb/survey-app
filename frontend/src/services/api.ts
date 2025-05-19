import axios from 'axios'
import { getAuthToken } from './tokenService'
import { toastService } from '@/services/toastService'

const baseURL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL,
  timeout: 10000,
})

api.interceptors.request.use(
  async (config) => {
    const token = await getAuthToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log(config.headers)
    }
    return config
  },
  (error) => Promise.reject(error),
)

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      toastService.warning('[Auth] Token expired or unauthorized')
      // TODO: redirect to login, or dispatch logout
    }
    return Promise.reject(err)
  },
)

export default api
