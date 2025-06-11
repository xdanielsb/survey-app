import type { DirectiveBinding } from 'vue'
import { useAuthStore } from '@/stores/authStore'

export const vRole = {
  mounted(el: HTMLElement, binding: DirectiveBinding<string | string[]>) {
    const authStore = useAuthStore()
    const roles = Array.isArray(binding.value) ? binding.value : [binding.value]
    const hasRole = roles.some((role) =>
      authStore.roles.some((userRole) => userRole.toLowerCase() === String(role).toLowerCase()),
    )

    if (!hasRole) {
      const tooltipText = `Requires role: ${roles.join(', ')}`

      // Wrap element in a span for tooltip
      const wrapper = document.createElement('span')
      wrapper.style.position = 'relative'
      wrapper.style.display = 'inline-block'
      wrapper.setAttribute('title', tooltipText)

      // Style the element as disabled
      el.setAttribute('aria-disabled', 'true')
      el.style.pointerEvents = 'none'
      el.style.opacity = '0.6'
      el.style.cursor = 'not-allowed'

      // Move element into wrapper
      el.parentNode?.insertBefore(wrapper, el)
      wrapper.appendChild(el)
    }
  },
}
