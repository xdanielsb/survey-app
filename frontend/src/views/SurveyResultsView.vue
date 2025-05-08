<template>
  <div class="container">
    <h2 v-if="results">{{ results.surveyTitle }}</h2>
    <p v-else>Loading results...</p>

    <div
      v-for="question in results?.questionResults"
      :key="question.questionId"
      class="question-block"
    >
      <h3>{{ question.questionText }}</h3>
      <ul>
        <li v-for="(count, option) in question.distribution" :key="option">
          {{ option }}: {{ count }}
        </li>
      </ul>
      <p><strong>Average Score:</strong> {{ question.averageScore.toFixed(2) }}</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import type { SurveyResults } from '@/types/survey-results.ts';
import { fetchSurveyResults } from '@/services/surveyService.ts';

const route = useRoute();
const results = ref<SurveyResults | null>(null);

onMounted(async () => {
  const id = Number(route.params.id);
  results.value = await fetchSurveyResults(id);
});
</script>

<style scoped>
.container {
  max-width: 700px;
  margin: 2rem auto;
  padding: 1rem;
  font-family: 'Segoe UI', sans-serif;
}

.question-block {
  background-color: #fdfdfd;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

</style>
