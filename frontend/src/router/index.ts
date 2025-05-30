// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { routes } from './routes'
import { useAuthStore } from '@/stores/authStore'
import { useUiStore } from '@/stores/uiStore'
import { toastService } from '@/services/toastService'

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  const ui = useUiStore()

  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    const msg =
      typeof to.meta.authMessage === 'string'
        ? to.meta.authMessage
        : 'You need to log in to continue.'

    toastService.info(msg)
    ui.showLogin() // opens the modal
    next(false) // cancel navigation
  } else {
    next()
  }
})

export default router
