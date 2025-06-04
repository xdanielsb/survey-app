import { defineStore } from 'pinia'
import Cookies from 'js-cookie'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: Cookies.get('token') || null,
    roles: JSON.parse(Cookies.get('roles') || '[]'),
    email: Cookies.get('email') || null,
    isAuthenticated: !!Cookies.get('token') && !!Cookies.get('email'),
  }),

  actions: {
    login(email: string, token: string, roles: string[]) {
      this.token = token
      this.roles = roles
      this.email = email
      this.isAuthenticated = true
      Cookies.set('token', token, { secure: true, sameSite: 'strict' })
      Cookies.set('email', email, { secure: true, sameSite: 'strict' })
      Cookies.set('roles', JSON.stringify(roles), {
        secure: true,
        sameSite: 'strict',
      })
    },

    logout() {
      this.token = null
      this.email = null
      this.roles = []
      this.isAuthenticated = false
      Cookies.remove('token')
      Cookies.remove('roles')
      Cookies.remove('email')
    },
  },
})
