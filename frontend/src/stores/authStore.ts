import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    roles: JSON.parse(localStorage.getItem('roles') || '[]'),
    email: localStorage.getItem('email') || null,
    isAuthenticated: !!localStorage.getItem('token'),
  }),

  actions: {
    login(email: string, token: string, roles: string[]) {
      this.token = token
      this.roles = roles
      this.email = email
      this.isAuthenticated = true
      localStorage.setItem('token', token)
      localStorage.setItem('email', email)
      localStorage.setItem('roles', JSON.stringify(roles))
    },

    logout() {
      this.token = null
      this.email = null
      this.roles = []
      this.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('roles')
      localStorage.removeItem('email')
    },
  },
})
