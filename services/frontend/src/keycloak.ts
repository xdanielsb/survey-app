import Keycloak from 'keycloak-js'
import type { KeycloakConfig } from 'keycloak-js'
import { jwtDecode } from 'jwt-decode'
import { useAuthStore } from '@/stores/authStore'
import type { UserGmail } from '@/types/user.ts'

const config: KeycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: 'frontend',
}

for (const [k, v] of Object.entries(config)) {
  if (typeof v === 'undefined' || v === null || v === '') {
    console.warn(`[Keycloak] Missing env var for ${k}`)
  }
}

export async function initializeKeycloak() {
  await keycloak.init({
    onLoad: 'check-sso', // or 'login-required'
    pkceMethod: 'S256', // use PKCE for public clients
    flow: 'standard', // standard flow is most secure
    // silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
    checkLoginIframe: false,
  })
}

export function syncAuthFromKeycloak() {
  if (keycloak.authenticated && keycloak.token) {
    const authStore = useAuthStore()
    const decoded: UserGmail = jwtDecode(keycloak.token)
    const email = decoded.email || decoded.preferred_username
    const roles = decoded.realm_access?.roles || []
    const premium = decoded.premium ?? false
    authStore.login(email, keycloak.token, roles, authStore.isPremium)
  }
}

export const keycloak = new Keycloak(config)
