import { toastService } from '@/services/toastService'
import api from '@/services/api'
import { useAuthStore } from '@/stores/authStore'
import type { CreateUserInput } from '@/types/create-user-input.ts'
import { createUser } from '@/services/surveyService.ts'
import { getGoogleLoginUrl, getSignUpUrl } from '@/services/keycloakService.ts'

const authStore = useAuthStore()

export async function loginUserAndPassword(email: string, password: string) {
  try {
    const response = await api.post('/auth/login', { email, password })
    const { token, roles, premium } = response.data
    const authStore = useAuthStore()
    authStore.login(email, token, roles, premium)
    return { success: true }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    const backendError = err?.code || err?.message
    toastService.error('Backend error during login:' + backendError)
    return {
      success: false,
    }
  }
}

export async function loginWithGoogle() {
  const url = await getGoogleLoginUrl()
  window.location.href = url
  if (authStore.isAuthenticated) {
    const userPayload: CreateUserInput = {
      email: authStore.email || '',
    }
    const userDB = await createUser(userPayload)
    authStore.login(userDB.email, authStore.token || '', userDB.roles, userDB.premium)
    return { success: true, message: `Welcome, friend'}!` }
  }
  return { success: false, message: `Could not login` }
}

export async function signUpUser(): Promise<void> {
  const url = await getSignUpUrl()
  window.location.href = url
}
