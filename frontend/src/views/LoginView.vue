<template>
  <div class="login-container">
    <h1>{{ isSignUp ? 'Sign Up' : 'Sign In' }}</h1>

    <form @submit.prevent="isSignUp ? handleSignUp() : handleLogin()">
      <input v-model="email" type="email" placeholder="Email" required />
      <input v-model="password" type="password" placeholder="Password" required />
      <button type="submit">{{ isSignUp ? 'Create Account' : 'Login' }}</button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>

    <p class="toggle-text">
      <span v-if="isSignUp">Already have an account?</span>
      <span v-else>Don't have an account?</span>
      <a @click="toggleMode">{{ isSignUp ? 'Login here' : 'Sign up here' }}</a>
    </p>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { loginUser, signUpUser } from '@/services/authService.ts'

const email = ref('')
const password = ref('')
const error = ref('')
const isSignUp = ref(false)
const router = useRouter()

const toggleMode = () => {
  error.value = ''
  isSignUp.value = !isSignUp.value
}

const handleLogin = async () => {
  try {
    const token = await loginUser(email.value, password.value)
    if (token) {
      localStorage.setItem('token', token)
      router.push('/')
    }
  } catch {
    error.value = 'Login failed'
  }
}

const handleSignUp = async () => {
  const token = await signUpUser(email.value, password.value)
  localStorage.setItem('token', token || '')
  if (token) {
    router.push('/')
  }
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 6rem auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.05);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto;
  text-align: center;
}

h1 {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #111;
}

form {
  display: flex;
  flex-direction: column;
}

input {
  display: block;
  padding: 0.75rem 1rem;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 1rem;
  background: #f9f9f9;
}

button {
  width: 100%;
  padding: 0.75rem;
  background-color: #007aff;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

button:hover {
  background-color: #0063cc;
}

.error {
  color: #b00020;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.toggle-text {
  margin-top: 1rem;
  font-size: 0.9rem;
}

.toggle-text a {
  color: #007aff;
  margin-left: 0.25rem;
  cursor: pointer;
  text-decoration: underline;
}
</style>
