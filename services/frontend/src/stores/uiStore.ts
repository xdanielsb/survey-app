import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUiStore = defineStore('ui', () => {
  const loginModalOpen = ref(false)
  const showLogin = () => (loginModalOpen.value = true)
  const hideLogin = () => (loginModalOpen.value = false)
  return { loginModalOpen, showLogin, hideLogin }
})
