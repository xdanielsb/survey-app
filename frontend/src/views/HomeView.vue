<template>
  <div class="container">
    <h1>Available Surveys</h1>
    <ul v-if="surveys.length" class="survey-list">
      <li v-for="survey in surveys" :key="survey.id" class="survey-item">
        <span class="title">{{ survey.title }}</span>
        <div class="actions">
          <router-link :to="`/surveys/${survey.id}`" class="btn primary">Answer</router-link>
          <router-link :to="`/surveys/${survey.id}/results`" class="btn secondary">Results</router-link>
        </div>
      </li>
    </ul>
    <p v-else class="loading">Loading surveys...</p>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import { fetchSurveys } from '@/services/surveyService';
import type { Survey } from '@/types/survey';

const surveys = ref<Survey[]>([]);

onMounted(async () => {
  surveys.value = await fetchSurveys();
});
</script>

<style scoped>
.container {
  max-width: 700px;
  margin: 3rem auto;
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h1 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #333;
}

.survey-list {
  list-style: none;
  padding: 0;
}

.survey-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  padding: 1rem 1.5rem;
  margin-bottom: 1rem;
  border-radius: 8px;
  border: 1px solid #ddd;
  transition: box-shadow 0.2s;
}

.survey-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.title {
  font-weight: 500;
  font-size: 1.1rem;
  color: #222;
}

.actions {
  display: flex;
  gap: 0.5rem;
}
</style>
