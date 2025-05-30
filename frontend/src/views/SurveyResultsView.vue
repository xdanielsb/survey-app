<template>
  <div class="relative">
    <header
      v-if="results"
      class="sticky top-0 z-20 backdrop-blur-sm bg-white/70 border-b border-[color:var(--color-neutral-200)] py-3 px-6 flex items-center justify-between"
    >
      <h1 class="text-lg font-display font-semibold text-[color:var(--color-neutral-900)] truncate">
        {{ results.surveyTitle }}
      </h1>

      <span
        class="px-3 py-1 rounded-full bg-[color:var(--color-neutral-100)] text-[color:var(--color-neutral-700)] text-sm font-medium"
      >
        Questions {{ questionCount }}
      </span>
    </header>

    <main class="max-w-6xl mx-auto px-6 py-12 space-y-10">
      <template v-if="!results">
        <h2 class="text-xl font-display font-semibold text-[color:var(--color-neutral-600)] mb-6">
          Loading results…
        </h2>

        <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <div
            v-for="n in 6"
            :key="n"
            class="animate-pulse h-[300px] rounded-[var(--radius-lg)] bg-[color:var(--color-neutral-100)]"
          />
        </div>
      </template>

      <template v-else>
        <h2
          class="hidden md:block text-3xl font-display font-semibold text-[color:var(--color-neutral-900)]"
        >
          {{ results.surveyTitle }}
        </h2>

        <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <SurveyResultItem
            v-for="q in results.questionResults"
            :key="q.questionId"
            :question="q"
          />
        </div>
      </template>
    </main>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import type { SurveyResults } from '@/types/survey-results'
import { fetchSurveyResults } from '@/services/surveyService'
import SurveyResultItem from '@/components/SurveyResultItem.vue'

const route = useRoute()
const results = ref<SurveyResults | null>(null)

const questionCount = computed(() => results.value?.questionResults.length ?? 0)

onMounted(async () => {
  const id = Number(route.params.id)
  results.value = await fetchSurveyResults(id)
})
</script>
