<script setup lang="ts" strict>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { loginUserAndPassword, loginWithGoogle, signUpUser } from '@/services/authService'
import { toastService } from '@/services/toastService'
import { useAuthStore } from '@/stores/authStore.ts'

/* v-model from parent */
const show = defineModel<boolean>({ required: true })

/* form state */
const email = ref('')
const password = ref('')
const errorMsg = ref('')
const isSignUp = ref(false)
const router = useRouter()
const authStore = useAuthStore()

/* reset when modal closes */
watch(show, (v) => {
  if (!v) {
    email.value = ''
    password.value = ''
    errorMsg.value = ''
    isSignUp.value = false
  }
})

async function handleSubmit() {
  errorMsg.value = ''

  /* Sign Up */
  if (isSignUp.value) {
    try {
      const token = signUpUser(email.value, password.value)
      if (token) {
        toastService.success('Account created!')
        router.push('/')
        show.value = false
      } else {
        errorMsg.value = 'Could not create account'
      }
    } catch {
      errorMsg.value = 'Sign-up failed'
    }
    return
  }

  /* Sign In */
  const { success } = await loginUserAndPassword(email.value, password.value)
  if (success) {
    router.push('/')
    show.value = false
  } else {
    errorMsg.value = 'Invalid credentials'
  }
}

function toggleMode() {
  errorMsg.value = ''
  isSignUp.value = !isSignUp.value
}

async function googleLogin() {
  const { success, message } = await loginWithGoogle()
  if (success) {
    if (authStore.isPremium) {
      toastService.success('Welcome back, premium user! 🎉')
    } else {
      toastService.success(message || 'Hello')
    }
    show.value = false
    router.push('/')
  } else {
    toastService.error(message || 'Google sign-in failed')
  }
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
          class="relative flex w-full max-w-3xl overflow-hidden rounded-[var(--radius-lg)] bg-[color:var(--color-neutral-100)] shadow-[var(--shadow-soft)]"
        >
          <div class="w-full lg:w-1/2 p-10 space-y-6">
            <h1 class="text-2xl font-display font-semibold text-center">
              {{ isSignUp ? 'Create Account' : 'Welcome back' }}
            </h1>

            <button
              @click="googleLogin"
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

            <form @submit.prevent="handleSubmit" class="space-y-4">
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
                {{ isSignUp ? 'Create Account' : 'Login' }}
              </button>

              <p v-if="errorMsg" class="text-xs text-red-600 text-center">{{ errorMsg }}</p>
            </form>

            <p class="text-center text-sm">
              <span v-if="isSignUp">Already have an account?</span>
              <span v-else>Don't have an account?</span>
              <button
                @click="toggleMode"
                type="button"
                class="ml-1 text-[color:var(--color-primary-600)] hover:underline"
              >
                {{ isSignUp ? 'Sign in' : 'Sign up' }}
              </button>
            </p>
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
