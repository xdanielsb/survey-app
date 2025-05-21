<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { onMounted } from 'vue'
import { logger } from './plugins/logger'

const mode = import.meta.env.MODE
const router = useRouter()
const authStore = useAuthStore()

// Computed user email from store
const userEmail = computed(() => (authStore.isAuthenticated ? authStore.email : null))

// Logout: clears store, localStorage, and redirects
const logout = () => {
  authStore.logout()
  router.push('/login')
}

// Logger
onMounted(() => {
  logger.info('App mounted successfully.')
})

defineExpose({ logger })
</script>

<template>
  <main class="container mx-auto p-6">
    <div class="env-indicator" :class="mode">
      Environment: <strong>{{ mode }}</strong>
    </div>

    <div class="auth-bar">
      <span v-if="userEmail"> {{ userEmail }}</span>
      <router-link v-if="!userEmail" to="/login" class="auth-btn">Login</router-link>
      <button v-else @click="logout" class="auth-btn logout">Logout</button>
    </div>

    <RouterView />
  </main>
</template>

<style scoped>
.env-indicator {
  position: fixed;
  top: 1rem;
  right: 1rem;
  padding: 0.4rem 0.8rem;
  font-size: 0.75rem;
  font-weight: bold;
  border-radius: 6px;
  z-index: 1000;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.1);
}
.env-indicator.development {
  background-color: #007aff;
  color: white;
}
.env-indicator.production {
  background-color: #e53935;
  color: white;
}
.env-indicator.test {
  background-color: #ff9800;
  color: white;
}

.auth-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 1rem;
  margin: 0.5rem 0 1rem;
}

.auth-btn {
  background-color: #eee;
  color: #333;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  font-size: 0.9rem;
  text-decoration: none;
  border: none;
  cursor: pointer;
}

.auth-btn.logout {
  background-color: #ffdddd;
  color: #b00020;
}

.auth-btn:hover {
  background-color: #ddd;
}

.auth-btn.logout:hover {
  background-color: #ffcccc;
}
</style>
