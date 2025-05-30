<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { loginUser } from '@/services/authService'
import { toastService } from '@/services/toastService'

/* -------- props / v-model -------- */
const show = defineModel<boolean>({ required: true })

/* -------- form state -------- */
const email = ref('')
const password = ref('')
const errorMsg = ref('')
const router = useRouter()

/* close on success */
watch(show, (v) => !v && (email.value = password.value = errorMsg.value = ''))

async function handlePasswordLogin() {
  const { success } = await loginUser(email.value, password.value)
  if (success) {
    router.push('/')
    show.value = false
  } else {
    errorMsg.value = 'Invalid credentials'
  }
}

function googleSoon() {
  toastService.info('Google login — coming soon ✨')
}
</script>

<template>
  <teleport to="body">
    <Transition name="fade-scale" appear>
      <div
        v-if="show"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4"
        @click.self="show = false"
      >
        <div
          class="relative flex w-full max-w-3xl overflow-hidden rounded-[var(--radius-lg)] bg-white shadow-[var(--shadow-soft)]"
        >
          <div class="w-full lg:w-1/2 p-10 space-y-6">
            <h1 class="text-2xl font-display font-semibold text-center">Welcome back</h1>

            <button
              @click="googleSoon"
              class="w-full inline-flex items-center justify-center gap-3 rounded-[var(--radius-sm)] border border-[color:var(--color-neutral-300)] py-2.5 text-sm font-medium hover:bg-[color:var(--color-neutral-100)] transition"
            >
              <img
                src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                class="w-5 h-5"
              />
              Continue with Google
            </button>

            <div class="flex items-center gap-4">
              <span class="flex-1 h-px bg-[color:var(--color-neutral-200)]" />
              <span class="text-xs text-[color:var(--color-neutral-500)] uppercase">or</span>
              <span class="flex-1 h-px bg-[color:var(--color-neutral-200)]" />
            </div>

            <form @submit.prevent="handlePasswordLogin" class="space-y-4">
              <input
                v-model="email"
                type="email"
                required
                placeholder="Email"
                class="w-full rounded-[var(--radius-sm)] border border-[color:var(--color-neutral-300)] bg-[color:var(--color-neutral-50)] px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[color:var(--color-primary-500)]"
              />
              <input
                v-model="password"
                type="password"
                required
                placeholder="Password"
                class="w-full rounded-[var(--radius-sm)] border border-[color:var(--color-neutral-300)] bg-[color:var(--color-neutral-50)] px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[color:var(--color-primary-500)]"
              />
              <button
                type="submit"
                class="w-full rounded-[var(--radius-sm)] bg-[color:var(--color-primary-600)] py-2.5 text-sm font-semibold text-white hover:bg-[color:var(--color-primary-700)] transition"
              >
                Login
              </button>
              <p v-if="errorMsg" class="text-xs text-red-600 text-center">{{ errorMsg }}</p>
            </form>
          </div>

          <div
            class="hidden lg:block lg:w-1/2 bg-cover bg-center"
            style="
              background-image: url('https://images.pexels.com/photos/590022/pexels-photo-590022.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1');
            "
          />
        </div>
      </div>
    </Transition>
  </teleport>
</template>

<style>
/* Modal animation */
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all 0.25s var(--ease-snappy);
}
</style>
