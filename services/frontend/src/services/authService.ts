import { auth, googleProvider } from '@/firebase'
import { createUserWithEmailAndPassword, signInWithPopup } from 'firebase/auth'
import { toastService } from '@/services/toastService'
import api from '@/services/api'
import { useAuthStore } from '@/stores/authStore'
import type { CreateUserInput } from '@/types/create-user-input.ts'
import { createUser } from '@/services/surveyService.ts'

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
  try {
    if (!auth) {
      return { success: false, message: 'Firebase not initialized' }
    }
    if (!googleProvider) {
      return { success: false, error: 'Google provider not initialized' }
    }
    const { user } = await signInWithPopup(auth, googleProvider)
    const idToken = await user.getIdToken(true)
    const authStore = useAuthStore()
    if (!user.email) {
      return { success: false, error: 'No email found in user data' }
    }
    const userPayload: CreateUserInput = {
      uid: user.uid,
      email: user.email,
    }
    const userDB = await createUser(userPayload)
    authStore.login(userDB.email, idToken, userDB.roles, userDB.premium)
    return { success: true, user: `Welcome, ${user.displayName ?? 'friend'}!` }
  } catch (err) {
    return { success: false, error: err }
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

    const userPayload: CreateUserInput = {
      uid: userCred.user.uid,
      email: userCred.user.email,
    }
    const user = await createUser(userPayload)
    const authStore = useAuthStore()
    authStore.login(email, token, user.roles, user.premium)
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
