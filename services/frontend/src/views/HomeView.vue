<template>
  <div class="relative">
    <main class="mx-auto px-6 py-16 space-y-10">
      <section
        class="rounded-[var(--radius-md)] bg-[color:var(--color-primary-50)] p-6 shadow-[var(--shadow-soft)]"
      >
        <p class="text-lg font-medium text-[color:var(--color-neutral-900)] mb-2">
          "The people who are crazy enough to think they can change the world are the ones who do."
          – Steve Jobs
        </p>
        <p class="text-[color:var(--color-neutral-700)]">
          Buy a survey to become a lifelong premium user and unlock AI analytics for every survey
          you create.
        </p>
      </section>
      <section class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-6">
        <div>
          <h1
            class="text-3xl font-display font-semibold text-[color:var(--color-neutral-900)] mb-2"
          >
            Available Surveys
          </h1>

          <span
            v-if="credits !== null"
            class="inline-flex items-center gap-1 text-sm font-medium px-3 py-1 rounded-full bg-[color:var(--color-neutral-100)] text-[color:var(--color-neutral-700)]"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4 text-[color:var(--color-primary-600)]"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 8v4l3 3"
              />
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 19C6.48 19 2 14.52 2 9S6.48-1 12-1s10 4.48 10 10-4.48 10-10 10z"
              />
            </svg>
            {{ credits }} survey credit{{ credits === 1 ? '' : 's' }}
          </span>
        </div>

        <!-- Search / filter -->
        <div class="flex items-center gap-3 w-full sm:w-auto">
          <input
            v-model="search"
            type="text"
            placeholder="Search…"
            class="flex-1 sm:w-56 px-4 py-2 text-sm rounded-[var(--radius-sm)] border border-[color:var(--color-neutral-300)] focus:outline-none focus:ring-2 focus:ring-[color:var(--color-primary-500)] bg-[color:var(--color-neutral-50)]"
          />

          <router-link
            to="/create-survey"
            class="relative inline-flex items-center gap-2 px-5 py-2.5 rounded-full text-sm font-semibold text-white bg-[color:var(--color-primary-600)] shadow-[var(--shadow-soft)] hover:bg-[color:var(--color-primary-700)] focus:outline-none focus:ring-2 focus:ring-[color:var(--color-primary-500)] transition"
          >
            + Create Survey
            <span
              class="absolute inset-0 rounded-full animate-ping bg-[color:var(--color-primary-600)] opacity-10"
            />
          </router-link>
        </div>
      </section>

      <section>
        <ul v-if="surveys" class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <SurveyListItem v-for="survey in filteredSurveys" :key="survey.id" :survey="survey" />
        </ul>

        <div v-else class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <div
            v-for="n in 6"
            :key="n"
            class="h-[180px] rounded-[var(--radius-lg)] bg-[color:var(--color-neutral-100)] animate-pulse"
          />
        </div>
      </section>

      <section v-if="surveys && totalPages > 1" class="flex justify-center items-center gap-6 pt-6">
        <button
          @click="page--"
          :disabled="page === 0"
          class="text-[color:var(--color-primary-600)] hover:text-[color:var(--color-primary-800)] disabled:text-[color:var(--color-neutral-400)] text-sm font-medium transition"
        >
          ← Previous
        </button>

        <span class="text-[color:var(--color-neutral-600)] text-sm font-medium">
          Page {{ page + 1 }} of {{ totalPages }}
        </span>

        <button
          @click="page++"
          :disabled="page + 1 >= totalPages"
          class="text-[color:var(--color-primary-600)] hover:text-[color:var(--color-primary-800)] disabled:text-[color:var(--color-neutral-400)] text-sm font-medium transition"
        >
          Next →
        </button>
      </section>
    </main>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, watchEffect, computed, onMounted } from 'vue'
import { fetchSurveys } from '@/services/surveyService'
import { getUserCredits } from '@/services/creditService'
import { onBeforeRouteUpdate } from 'vue-router'
import type { Survey } from '@/types/survey'
import SurveyListItem from '@/components/SurveyListItem.vue'
import { logger } from '@/plugins/logger'
import { useAuthStore } from '@/stores/authStore'

/* ---------- reactive state ---------- */
const surveys = ref<Survey[] | null>(null)
const page = ref(0)
const totalPages = ref(0)
const search = ref('')
const credits = ref<number | null>(null)

const authStore = useAuthStore()

async function loadCredits() {
  try {
    const { credits: c } = await getUserCredits()
    credits.value = c
  } catch {
    logger.error('Failed to load user credits')
  }
}

watchEffect(async () => {
  const { content, totalPages: tp } = await fetchSurveys(page.value, 9, search.value)
  surveys.value = content
  totalPages.value = tp
})

watch(search, () => {
  page.value = 0
})

onMounted(async () => {
  loadCredits()
})

onBeforeRouteUpdate((to, from, next) => {
  loadCredits()
  next()
})

watch(
  () => authStore.isAuthenticated,
  (isAuthNow) => {
    if (isAuthNow) {
      loadCredits()
    } else {
      credits.value = null
    }
  },
)

const filteredSurveys = computed(() => surveys.value ?? [])
</script>
