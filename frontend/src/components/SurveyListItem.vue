<template>
  <li class="survey-item">
    <span class="title">{{ survey.title }}</span>
    <div class="actions">
      <router-link :to="`/surveys/${survey.id}`" class="btn primary">Answer</router-link>
      <router-link :to="`/surveys/${survey.id}/results`" class="btn secondary">Results</router-link>
    </div>
  </li>
</template>

<script lang="ts" setup>
import type { Survey } from '@/types/survey'

defineProps<{
  survey: Survey
}>()
</script>

<style scoped>
.survey-item {
  display: flex;
  align-items: center;
  gap: 1rem;
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
</style>
