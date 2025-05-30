<template>
  <div class="max-w-6xl mx-auto px-6 py-10">
    <h2
      v-if="results"
      class="text-3xl font-display font-semibold text-[color:var(--color-neutral-900)] mb-8"
    >
      {{ results.surveyTitle }}
    </h2>

    <p v-else class="text-[color:var(--color-neutral-600)] text-lg">Loading results...</p>

    <div v-if="results" class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
      <SurveyResultItem
        v-for="question in results.questionResults"
        :key="question.questionId"
        :question="question"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import type { SurveyResults } from '@/types/survey-results'
import { fetchSurveyResults } from '@/services/surveyService'
import SurveyResultItem from '@/components/SurveyResultItem.vue'

const route = useRoute()
const results = ref<SurveyResults | null>(null)

onMounted(async () => {
  const id = Number(route.params.id)
  results.value = await fetchSurveyResults(id)
})
</script>
