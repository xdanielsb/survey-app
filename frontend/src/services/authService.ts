import { auth } from '@/firebase'
import { signInWithEmailAndPassword, createUserWithEmailAndPassword } from 'firebase/auth'
import { toastService } from '@/services/toastService'
import api from '@/services/api'

export async function loginUser(email: string, password: string): Promise<string | null> {
  if (!auth) {
    toastService.error('Firebase not initialized')
    return null
  }
  try {
    const userCred = await signInWithEmailAndPassword(auth, email, password)
    return await userCred.user.getIdToken()
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    const firebaseError = err?.code || err?.message
    toastService.error('Firebase sign-up failed:' + firebaseError)
    return null
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
