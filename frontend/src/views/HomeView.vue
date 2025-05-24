<template>
  <div class="container">
    <div class="header">
      <h1>Available Surveys</h1>
      <router-link v-role="['MANAGER', 'ADMIN']" to="/create-survey" class="create-btn">
        + Create Survey
      </router-link>
    </div>

    <ul v-if="surveys.length" class="survey-list">
      <SurveyListItem v-for="survey in surveys" :key="survey.id" :survey="survey" />
    </ul>
    <p v-else class="loading">Loading surveys...</p>

    <div class="pagination">
      <button @click="page--" :disabled="page === 0">Previous</button>
      <span>Page {{ page + 1 }} of {{ totalPages }}</span>
      <button @click="page++" :disabled="page + 1 >= totalPages">Next</button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watchEffect } from 'vue'
import { fetchSurveys } from '@/services/surveyService'
import type { Survey } from '@/types/survey'
import SurveyListItem from '@/components/SurveyListItem.vue'

const surveys = ref<Survey[]>([])
const page = ref(0)
const totalPages = ref(0)

watchEffect(async () => {
  const result = await fetchSurveys(page.value, 5)
  surveys.value = result.content
  totalPages.value = result.totalPages
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  gap: 1rem;
}

.pagination button {
  padding: 0.5rem 1rem;
  border: none;
  background: #007aff;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.pagination button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
