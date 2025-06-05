import { auth } from '@/firebase'
import { createUserWithEmailAndPassword } from 'firebase/auth'
import { toastService } from '@/services/toastService'
import api from '@/services/api'
import { useAuthStore } from '@/stores/authStore'

export async function loginUser(email: string, password: string) {
  try {
    const response = await api.post('/auth/login', { email, password })
    const { token, roles, premium } = response.data
    console.log(response.data)
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

export async function signUpUser(email: string, password: string): Promise<string | null> {
  if (!auth) {
    toastService.error('Firebase not initialized')
    return null
  }

  try {
    const userCred = await createUserWithEmailAndPassword(auth, email, password)
    const token = await userCred.user.getIdToken()

    const userPayload = {
      uid: userCred.user.uid,
      email: userCred.user.email,
    }

    await api.post('/users/create', userPayload)
    const authStore = useAuthStore()
    authStore.login(email, token, [], false)
    return token
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    const firebaseError = err?.code || err?.message
    const backendError = err?.response?.data || null

    if (backendError) {
      toastService.error('Backend error during user registration:' + backendError)
    } else {
      toastService.error('Firebase sign-up failed:' + firebaseError)
    }

    return null
  }
}
