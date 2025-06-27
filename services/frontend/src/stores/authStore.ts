import { defineStore } from 'pinia'
import Cookies from 'js-cookie'
import { keycloak } from '@/keycloak.ts'
import type { UserGmail } from '@/types/user.ts'
import { jwtDecode } from 'jwt-decode'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: Cookies.get('token') || null,
    roles: JSON.parse(Cookies.get('roles') || '[]') as string[],
    email: Cookies.get('email') || null,
    isAuthenticated: !!Cookies.get('token') && !!Cookies.get('email'),
    isPremium: Cookies.get('isPremium') === 'true',
  }),

  actions: {
    login(email: string, token: string, roles: string[], isPremium: boolean) {
      this.token = token
      this.roles = roles
      this.email = email
      this.isAuthenticated = true
      this.isPremium = isPremium
      const decoded: UserGmail = jwtDecode(token)
      const expiresAt = decoded.exp || null
      const cookieOptions = {
        secure: true,
        sameSite: 'strict' as const,
        ...(expiresAt ? { expires: new Date(expiresAt * 1000) } : {}),
      }
      Cookies.set('token', token, cookieOptions)
      Cookies.set('email', email, cookieOptions)
      Cookies.set('roles', JSON.stringify(roles), cookieOptions)
      Cookies.set('isPremium', String(isPremium), cookieOptions)
    },

    updatePremium(premium: boolean) {
      this.isPremium = premium
      Cookies.set('isPremium', String(premium), {
        secure: true,
        sameSite: 'strict',
      })
    },

    logout() {
      if (keycloak.authenticated) {
        try {
          keycloak.logout({ redirectUri: window.location.origin })
        } catch (e) {
          console.error('Keycloak logout failed:', e)
        }
      }
      this.token = null
      this.email = null
      this.roles = []
      this.isAuthenticated = false
      this.isPremium = false
      Cookies.remove('token')
      Cookies.remove('roles')
      Cookies.remove('email')
      Cookies.remove('isPremium')
    },
  },
})
