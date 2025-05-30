<template>
  <li
    class="group flex flex-col justify-between gap-4 rounded-[var(--radius-md)] border border-[color:var(--color-neutral-200)] bg-white p-6 shadow-[var(--shadow-soft)] transition-transform duration-300 ease-[var(--ease-snappy)] hover:-translate-y-[3px] hover:shadow-[var(--shadow-card)]"
  >
    <p class="text-lg font-medium text-[color:var(--color-neutral-900)] line-clamp-2">
      {{ survey.title }}
    </p>

    <div class="flex justify-end gap-3">
      <router-link :to="`/surveys/${survey.id}`" class="action-pill primary">
        <ArrowRightOnRectangleIcon class="w-4 h-4" />
        <span class="hidden sm:inline">Answer</span>
      </router-link>

      <router-link :to="`/surveys/${survey.id}/results`" class="action-pill neutral">
        <ChartBarIcon class="w-4 h-4" />
        <span class="hidden sm:inline">Results</span>
      </router-link>

      <button
        v-role="['ADMIN']"
        @click="handleDelete"
        class="action-pill danger"
        title="Delete survey"
      >
        <TrashIcon class="w-4 h-4" />
      </button>
    </div>
  </li>
</template>

<script setup lang="ts">
import type { Survey } from '@/types/survey'
import { deleteSurvey } from '@/services/surveyService'

import { ArrowRightOnRectangleIcon, ChartBarIcon, TrashIcon } from '@heroicons/vue/24/solid'

const props = defineProps<{ survey: Survey }>()
const emit = defineEmits<{ (e: 'deleted', id: number): void }>()

async function handleDelete() {
  if (!confirm('Delete this survey?')) return
  await deleteSurvey(props.survey.id)
  emit('deleted', props.survey.id)
}
</script>
