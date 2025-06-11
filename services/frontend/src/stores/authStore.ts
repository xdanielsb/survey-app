import { defineStore } from 'pinia'
import Cookies from 'js-cookie'

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
      Cookies.set('token', token, { secure: true, sameSite: 'strict', expires: 1 / 24 })
      Cookies.set('email', email, { secure: true, sameSite: 'strict', expires: 1 / 24 })
      Cookies.set('roles', JSON.stringify(roles), {
        secure: true,
        sameSite: 'strict',
        expires: 1 / 24,
      })
      Cookies.set('isPremium', String(isPremium), {
        secure: true,
        sameSite: 'strict',
        expires: 1 / 24,
      })
    },

    updatePremium(premium: boolean) {
      this.isPremium = true
      Cookies.set('isPremium', String(premium), {
        secure: true,
        sameSite: 'strict',
        expires: 1 / 24,
      })
    },

    logout() {
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
