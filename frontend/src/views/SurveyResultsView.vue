<template>
  <div>
    <h2 v-if="results">{{ results.surveyTitle }}</h2>
    <p v-else>Loading results...</p>

    <div class="grid">
      <SurveyResultItem
        v-for="question in results?.questionResults"
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

<style scoped>
.container {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 1rem;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
}
</style>
