import { keycloak } from '@/keycloak.ts'
import { useAuthStore } from '@/stores/authStore'
import { jwtDecode } from 'jwt-decode'
import type { UserGmail } from '@/types/user.ts'
const authStore = useAuthStore()

export async function getGoogleLoginUrl(): Promise<string> {
  if (keycloak.token) {
    const token = keycloak.token!
    const decoded: UserGmail = jwtDecode(token)
    const email = decoded.email || decoded.preferred_username
    const roles = decoded.realm_access?.roles || []

    authStore.login(email, token, roles, false)
  }
  return await keycloak.createLoginUrl({
    idpHint: 'google',
    redirectUri: window.location.origin,
  })
}

export async function getSignUpUrl(): Promise<string> {
  return await keycloak.createRegisterUrl({
    redirectUri: window.location.origin,
  })
}
