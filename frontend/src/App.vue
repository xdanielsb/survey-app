<script setup lang="ts" strict>
import { computed, onMounted, ref } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { logger } from '@/plugins/logger'
import { defineAsyncComponent } from 'vue'

const LoginView = defineAsyncComponent(() => import('@/views/LoginView.vue'))

const mode = import.meta.env.MODE as 'development' | 'test' | 'production'
const router = useRouter()
const authStore = useAuthStore()

const userEmail = computed(() => (authStore.isAuthenticated ? authStore.email : null))

const envClass = computed(() => {
  switch (mode) {
    case 'development':
      return 'bg-[color:var(--color-primary-600)] text-white'
    case 'test':
      return 'bg-yellow-500 text-white'
    case 'production':
    default:
      return 'bg-red-500 text-white'
  }
})

const showLogin = ref(false)

/* logout */
function logout() {
  authStore.logout()
  router.push('/')
}

/* first-mount halo (optional) */
onMounted(() => {
  logger.info('App shell mounted.')
  const halo = document.createElement('span')
  halo.className =
    'pointer-events-none fixed inset-0 z-50 animate-burst ' +
    'bg-[radial-gradient(circle_at_center,rgba(59,130,246,.25)_0%,rgba(59,130,246,0)_60%)]'
  document.body.appendChild(halo)
  halo.addEventListener('animationend', () => halo.remove())
})
</script>

<template>
  <header
    class="sticky top-0 z-40 flex items-center justify-between gap-4 backdrop-blur-sm bg-white/75 border-b border-[color:var(--color-neutral-200)] px-6 py-2 shadow-sm"
  >
    <span
      class="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold shadow-[var(--shadow-card)]"
      :class="envClass"
    >
      Env: {{ mode }}
    </span>

    <div class="flex items-center gap-4 text-sm">
      <span v-if="userEmail" class="text-[color:var(--color-neutral-700)] truncate max-w-[14rem]">
        {{ userEmail }}
      </span>

      <button
        v-if="!userEmail"
        @click="showLogin = true"
        class="px-3 py-1.5 rounded-[var(--radius-sm)] bg-[color:var(--color-primary-600)] text-white hover:bg-[color:var(--color-primary-700)] transition"
      >
        Login
      </button>

      <button
        v-else
        @click="logout"
        class="px-3 py-1.5 rounded-[var(--radius-sm)] bg-red-100 text-red-700 hover:bg-red-200 transition"
      >
        Logout
      </button>
    </div>
  </header>

  <main class="max-w-6xl mx-auto px-6 py-10">
    <Transition name="page" mode="out-in" appear>
      <RouterView />
    </Transition>
  </main>

  <LoginView v-model="showLogin" />
</template>

<style>
/* Page transition */
.page-enter-from,
.page-leave-to {
  opacity: 0;
  transform: translateY(12px) scale(0.98);
}
.page-enter-active,
.page-leave-active {
  transition: all 340ms var(--ease-snappy);
}
.page-leave-from,
.page-enter-to {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* Burst halo */
@keyframes burst {
  from {
    opacity: 0.9;
    transform: scale(0.4);
  }
  to {
    opacity: 0;
    transform: scale(2);
  }
}
.animate-burst {
  animation: burst 700ms var(--ease-snappy) forwards;
}
</style>
