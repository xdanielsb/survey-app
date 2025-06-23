<template>
  <div class="flex items-center justify-center min-h-[80vh] bg-[color:var(--color-neutral-50)]">
    <div
      class="max-w-sm text-center p-8 rounded-[12px] shadow-[0_4px_20px_rgba(0,0,0,0.05)] bg-[color:var(--color-neutral-100)]"
    >
      <h1 class="text-2xl mb-4">Thank You!</h1>
      <p>Your payment was successful.</p>

      <div v-if="loading" class="italic text-[color:var(--color-neutral-600)] mt-4">
        Verifying payment...
      </div>
      <div v-else-if="error" class="text-[color:var(--color-danger)] mt-4">
        {{ error }}
      </div>
      <div v-else class="text-[color:var(--color-success)] mt-4">
        <p><strong>Credits Added:</strong> {{ creditsGranted }}</p>
        <router-link
          to="/"
          class="inline-block mt-6 px-4 py-2.5 rounded-md text-white bg-[color:var(--color-primary-600)] hover:bg-[color:var(--color-primary-700)] transition"
          >Back to Dashboard</router-link
        >
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
        if (response.data.paid && !authStore.isPremium) {
          authStore.updatePremium(true)
        }
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
