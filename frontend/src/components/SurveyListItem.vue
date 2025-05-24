<template>
  <li class="survey-item">
    <span class="title truncate">{{ survey.title }}</span>

    <div class="actions">
      <router-link :to="`/surveys/${survey.id}`" class="btn primary">Answer</router-link>
      <router-link :to="`/surveys/${survey.id}/results`" class="btn secondary">Results</router-link>

      <button v-role="['ADMIN']" @click="handleDelete" class="btn danger">Delete</button>
    </div>
  </li>
</template>

<script setup lang="ts">
import type { Survey } from '@/types/survey'
import { deleteSurvey } from '@/services/surveyService'

const props = defineProps<{ survey: Survey }>()
const emit = defineEmits<{ (e: 'deleted', id: number): void }>()

const handleDelete = async () => {
  if (!confirm('Are you sure you want to delete this survey?')) return

  try {
    await deleteSurvey(props.survey.id)
    emit('deleted', props.survey.id)
  } catch (error) {
    console.error('Failed to delete survey:', error)
    alert('Could not delete survey')
  }
}
</script>

<style scoped>
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

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn {
  padding: 0.4rem 0.75rem;
  border: none;
  border-radius: 6px;
  text-decoration: none;
  cursor: pointer;
  font-size: 0.9rem;
}

.primary {
  background-color: #007aff;
  color: white;
}

.secondary {
  background-color: #ddd;
  color: black;
}

.danger {
  background-color: #e63946;
  color: white;
}

.danger:hover {
  background-color: #d62828;
}

.truncate {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}
</style>
