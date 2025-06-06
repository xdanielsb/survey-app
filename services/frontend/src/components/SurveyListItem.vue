<template>
  <li
    class="group flex flex-col justify-between gap-4 rounded-[var(--radius-md)] border border-[color:var(--color-neutral-200)] bg-white p-6 shadow-[var(--shadow-soft)] transition-transform duration-300 ease-[var(--ease-snappy)] hover:-translate-y-[3px] hover:shadow-[var(--shadow-card)]"
  >
    <p
      class="text-lg font-medium text-[color:var(--color-neutral-900)] line-clamp-2 flex items-center gap-2"
    >
      {{ survey.title }}
      <span
        class="px-2 py-0.5 rounded-full text-xs font-semibold bg-[color:var(--color-primary-100)] text-[color:var(--color-primary-800)]"
        >{{ survey.responseCount }}</span
      >
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

      <a
        :href="shareUrl"
        target="_blank"
        rel="noopener"
        class="action-pill neutral"
        title="Share on X"
      >
        <ShareIcon class="w-4 h-4" />
        <span class="hidden sm:inline">Share</span>
      </a>

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

import {
  ArrowRightOnRectangleIcon,
  ChartBarIcon,
  TrashIcon,
  ShareIcon,
} from '@heroicons/vue/24/solid'

const props = defineProps<{ survey: Survey }>()
const emit = defineEmits<{ (e: 'deleted', id: number): void }>()

const shareUrl =
  'https://x.com/share?url=' +
  encodeURIComponent(`${window.location.origin}/surveys/${props.survey.id}`)

async function handleDelete() {
  if (!confirm('Delete this survey?')) return
  await deleteSurvey(props.survey.id)
  emit('deleted', props.survey.id)
}
</script>
