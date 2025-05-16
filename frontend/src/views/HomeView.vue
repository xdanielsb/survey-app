<template>
  <div class="container">
    <div class="header">
      <h1>Available Surveys</h1>
      <router-link to="/create-survey" class="create-btn">+ Create Survey</router-link>
    </div>

    <ul v-if="surveys.length" class="survey-list">
      <SurveyListItem v-for="survey in surveys" :key="survey.id" :survey="survey" />
    </ul>
    <p v-else class="loading">Loading surveys...</p>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { fetchSurveys } from '@/services/surveyService'
import type { Survey } from '@/types/survey'
import SurveyListItem from '@/components/SurveyListItem.vue'

const surveys = ref<Survey[]>([])

onMounted(async () => {
  surveys.value = await fetchSurveys()
})
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

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.create-btn {
  background-color: #007aff;
  color: white;
  padding: 0.5rem 1rem;
  font-size: 0.95rem;
  border-radius: 8px;
  text-decoration: none;
  transition: background 0.2s ease;
}

.create-btn:hover {
  background-color: #0063cc;
}

.survey-list {
  list-style: none;
  padding: 0;
}
</style>
