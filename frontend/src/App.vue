<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import { auth } from '@/firebase'
import { onAuthStateChanged, signOut } from 'firebase/auth'
import firebase from 'firebase/auth'

const mode = import.meta.env.MODE
const router = useRouter()
const user = ref<firebase.User | null>(null)

onMounted(() => {
  if (!auth) {
    console.error('Firebase not initialized. Auth is unavailable.')
    return
  }

  onAuthStateChanged(auth, (u) => {
    user.value = u
  })
})

const logout = async () => {
  if (!auth) return
  await signOut(auth)
  user.value = null
  router.push('/login')
}
</script>

<template>
  <main class="container mx-auto p-6">
    <div class="env-indicator">
      Environment: <strong>{{ mode }}</strong>
    </div>

    <div class="auth-bar">
      <span v-if="user">👋 {{ user.email }}</span>
      <router-link v-if="!user" to="/login" class="auth-btn">Login</router-link>
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
