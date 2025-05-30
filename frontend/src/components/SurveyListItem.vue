<template>
  <li
    class="flex items-center justify-between bg-white border border-[color:var(--color-neutral-200)] rounded-[var(--radius-md)] px-6 py-4 mb-4 shadow-[var(--shadow-soft)] hover:shadow-[var(--shadow-card)] transition-shadow duration-300 ease-[var(--ease-snappy)]"
  >
    <span
      class="truncate text-base font-medium text-[color:var(--color-neutral-900)] max-w-[200px]"
    >
      {{ survey.title }}
    </span>

    <div class="flex gap-2">
      <router-link
        :to="`/surveys/${survey.id}`"
        class="inline-flex items-center px-3 py-1.5 text-sm font-medium rounded-[var(--radius-sm)] text-white bg-[color:var(--color-primary-600)] hover:bg-[color:var(--color-primary-700)] transition"
      >
        Answer
      </router-link>

      <router-link
        :to="`/surveys/${survey.id}/results`"
        class="inline-flex items-center px-3 py-1.5 text-sm font-medium rounded-[var(--radius-sm)] bg-[color:var(--color-neutral-200)] text-[color:var(--color-neutral-700)] hover:bg-[color:var(--color-neutral-300)] transition"
      >
        Results
      </router-link>

      <button
        v-role="['ADMIN']"
        @click="handleDelete"
        class="inline-flex items-center px-3 py-1.5 text-sm font-medium rounded-[var(--radius-sm)] bg-[color:var(--color-danger)] text-white hover:bg-red-700 transition"
      >
        Delete
      </button>
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
