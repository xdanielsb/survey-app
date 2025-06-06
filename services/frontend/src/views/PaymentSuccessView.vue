<template>
  <div class="success-container">
    <div class="card">
      <h1>Thank You!</h1>
      <p>Your payment was successful.</p>

      <div v-if="loading" class="loading">Verifying payment...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else class="success">
        <p><strong>Credits Added:</strong> {{ creditsGranted }}</p>
        <router-link to="/" clasHomegs="home-link">Back to Dashboard</router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/services/api.ts'
import { logger } from '@/plugins/logger'
import { useAuthStore } from '@/stores/authStore.ts'

export default {
  name: 'PaymentSuccessView',
  setup() {
    const route = useRoute()
    const loading = ref(true)
    const error = ref('')
    const creditsGranted = ref(0)
    const authStore = useAuthStore()

    onMounted(async () => {
      const sessionId = route.query.session_id
      if (!sessionId || typeof sessionId !== 'string') {
        error.value = 'Missing or invalid session ID.'
        loading.value = false
        return
      }

      try {
        const response = await api.get(`/payments/verify?session_id=${sessionId}`)
        creditsGranted.value = response.data.creditsGranted
        authStore.updatePremium(!!creditsGranted.value)
      } catch (err) {
        logger.error('Payment verification failed:', err)
        error.value = 'We could not verify your payment. Please contact support.'
      } finally {
        loading.value = false
      }
    })

    return { loading, error, creditsGranted }
  },
}
</script>

<style scoped>
.success-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: #fafafa;
}

.card {
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  background: white;
  text-align: center;
  max-width: 400px;
}

h1 {
  font-size: 1.75rem;
  margin-bottom: 1rem;
}

p {
  font-size: 1rem;
  margin: 0.5rem 0;
}

.success {
  color: #007a33;
  margin-top: 1rem;
}

.error {
  color: #d93025;
  margin-top: 1rem;
}

.loading {
  font-style: italic;
  color: #555;
  margin-top: 1rem;
}

.home-link {
  display: inline-block;
  margin-top: 1.5rem;
  color: white;
  background-color: #007aff;
  padding: 0.5rem 1.2rem;
  border-radius: 6px;
  text-decoration: none;
  transition: background 0.2s;
}

.home-link:hover {
  background-color: #005bb5;
}
</style>
