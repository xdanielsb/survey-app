<template>
  <div class="container">
    <h1>Available Surveys</h1>
    <ul v-if="surveys.length">
      <li v-for="survey in surveys" :key="survey.id">
        <router-link :to="`/survey/${survey.id}`">
          {{ survey.title }}
        </router-link>
      </li>
    </ul>
    <p v-else>Loading surveys...</p>
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
  max-width: 600px;
  margin: auto;
  padding: 1rem;
}
</style>
