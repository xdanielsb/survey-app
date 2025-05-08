<template>
  <div class="container">
    <h2 v-if="results">{{ results.title }}</h2>
    <p v-else>Loading results...</p>

    <div
      v-for="question in results?.questions"
      :key="question.id"
      class="question-block"
    >
      <h3>{{ question.text }}</h3>
      <ul>
        <li v-for="(count, option) in question.answers" :key="option">
          {{ option }}: {{ count }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import type { SurveyResults } from '@/types/survey-results.ts'
import { fetchSurveyResults } from '@/services/surveyService.ts'

const route = useRoute();
const results = ref<SurveyResults | null>(null);

onMounted(async () => {
  const id = route.params.id as string;
  results.value = await fetchSurveyResults(Number(id));
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

h2 {
  text-align: center;
  margin-bottom: 2rem;
}

h3 {
  margin-bottom: 0.5rem;
}
</style>
