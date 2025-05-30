<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { logger } from '@/plugins/logger'

/* ──────────────────────────────────
   stores & env
────────────────────────────────── */
const mode = import.meta.env.MODE
const router = useRouter()
const authStore = useAuthStore()

/* user email (reactive) */
const userEmail = computed(() => (authStore.isAuthenticated ? authStore.email : null))

/* env-pill colour */
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

/* logout */
const logout = () => {
  authStore.logout()
  router.push('/login')
}

/* mount log */
onMounted(() => logger.info('App mounted successfully.'))
</script>

<template>
  <!-- Frosted top bar -->
  <header
    class="sticky top-0 z-40 flex items-center justify-between gap-4 backdrop-blur-sm bg-white/75 border-b border-[color:var(--color-neutral-200)] px-6 py-2 shadow-sm"
  >
    <!-- Env pill -->
    <span
      class="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold shadow-[var(--shadow-card)]"
      :class="envClass"
    >
      Env: {{ mode }}
    </span>

    <!-- Auth controls -->
    <div class="flex items-center gap-4 text-sm">
      <span v-if="userEmail" class="text-[color:var(--color-neutral-700)]">
        {{ userEmail }}
      </span>

      <router-link
        v-if="!userEmail"
        to="/login"
        class="px-3 py-1.5 rounded-[var(--radius-sm)] bg-[color:var(--color-primary-600)] text-white hover:bg-[color:var(--color-primary-700)] transition"
      >
        Login
      </router-link>

      <button
        v-else
        @click="logout"
        class="px-3 py-1.5 rounded-[var(--radius-sm)] bg-red-100 text-red-700 hover:bg-red-200 transition"
      >
        Logout
      </button>
    </div>
  </header>

  <!-- Routed pages -->
  <main class="max-w-6xl mx-auto px-6 py-10">
    <RouterView />
  </main>
</template>
