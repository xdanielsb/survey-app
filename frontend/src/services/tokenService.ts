import { auth } from '@/firebase'
import { toastService } from '@/services/toastService'

export async function getAuthToken(): Promise<string | null> {
  if (!auth || !auth.currentUser) return null
  const user = auth.currentUser
  try {
    return await user.getIdToken(true) // always refresh
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (err: any) {
    toastService.error('Failed to get Firebase token' + err)
    return null
  }
}
