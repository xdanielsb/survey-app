import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const dark = ref(false)

  // initialize from localStorage
  if (typeof window !== 'undefined') {
    dark.value = localStorage.getItem('theme') === 'dark'
  }

  // update DOM and storage when dark value changes
  watch(
    dark,
    (isDark) => {
      const html = document.documentElement
      if (isDark) {
        html.classList.add('dark')
        localStorage.setItem('theme', 'dark')
      } else {
        html.classList.remove('dark')
        localStorage.setItem('theme', 'light')
      }
    },
    { immediate: true },
  )

  function toggle() {
    dark.value = !dark.value
  }

  return { dark, toggle }
})
