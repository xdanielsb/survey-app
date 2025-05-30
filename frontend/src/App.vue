<script setup lang="ts" strict>
import { computed, onMounted, ref, defineAsyncComponent } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { logger } from '@/plugins/logger'

/* ── modal loaded on demand ─────────────── */
const LoginView = defineAsyncComponent(() => import('@/views/LoginView.vue'))

/* ── env / auth ─────────────────────────── */
const mode = import.meta.env.MODE as 'development' | 'test' | 'production'
const router = useRouter()
const authStore = useAuthStore()
const userEmail = computed(() => (authStore.isAuthenticated ? authStore.email : null))
const showLogin = ref(false)

/* env pill classes */
const envClass = computed(() =>
  mode === 'development'
    ? 'bg-[color:var(--color-primary-600)] text-white'
    : mode === 'test'
      ? 'bg-yellow-500 text-white'
      : 'bg-red-500 text-white',
)

/* logout */
function logout() {
  authStore.logout()
  router.push('/')
}

/* wow-burst once */
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
  <!-- ███ flex wrapper fills viewport -->
  <div class="min-h-screen flex flex-col relative">
    <!-- ███ decorative backgrounds (-z-20) -->
    <div
      aria-hidden
      class="fixed inset-0 -z-20 bg-[radial-gradient(ellipse_at_top,rgba(59,130,246,0.08)_0%,transparent_65%)]"
    ></div>

    <div
      aria-hidden
      class="fixed inset-0 -z-20 pointer-events-none opacity-[0.04]"
      style="
        background-image: url('data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2210%22 height=%2210%22 viewBox=%220 0 10 10%22%3E%3Cline x1=%220%22 y1=%2210%22 x2=%2210%22 y2=%220%22 stroke=%22%23aab4cf%22 stroke-opacity=%220.6%22 stroke-width=%221%22/%3E%3C/svg%3E');
        background-size: 10px 10px;
      "
    ></div>

    <div
      aria-hidden
      class="fixed -z-20 right-0 top-0 w-[520px] h-[520px] opacity-[0.12] rotate-12 bg-[color:var(--color-primary-400)] blur-3xl"
      style="clip-path: polygon(28% 0, 100% 0, 100% 100%, 72% 100%, 58% 74%, 46% 52%, 34% 29%)"
    ></div>

    <!-- ███ frosted navbar -->
    <header
      class="sticky top-0 z-40 flex items-center justify-between gap-4 backdrop-blur-sm bg-white/75 border-b border-[color:var(--color-neutral-200)] px-6 py-2 shadow-sm"
    >
      <span
        :class="envClass"
        class="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-semibold shadow-[var(--shadow-card)]"
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

    <!-- ███ routed pages -->
    <main class="flex-1 max-w-6xl mx-auto px-6 py-10">
      <Transition name="page" mode="out-in" appear>
        <RouterView />
      </Transition>
    </main>

    <!-- ███ login modal -->
    <LoginView v-model="showLogin" />
  </div>
</template>

<style>
/* smooth page lift */
.page-enter-from,
.page-leave-to {
  opacity: 0;
  transform: translateY(12px) scale(0.98);
}
.page-enter-active,
.page-leave-active {
  transition: all 340ms var(--ease-snappy);
}
.page-enter-to,
.page-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* halo burst */
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
