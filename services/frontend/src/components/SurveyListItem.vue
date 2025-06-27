<template>
  <li
    class="group flex flex-col justify-between gap-4 rounded-[var(--radius-md)] border border-[color:var(--color-neutral-200)] bg-[color:var(--color-neutral-100)] p-6 shadow-[var(--shadow-soft)] transition-transform duration-300 ease-[var(--ease-snappy)] hover:-translate-y-[3px] hover:shadow-[var(--shadow-card)]"
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
        <ArrowRightOnRectangleIcon
          class="w-4 h-4 transition-colors group-hover:text-[color:var(--color-primary-600)]"
        />
        <span class="hidden sm:inline">Answer</span>
      </router-link>

      <router-link :to="`/surveys/${survey.id}/results`" class="action-pill neutral">
        <ChartBarIcon
          class="w-4 h-4 transition-colors group-hover:text-[color:var(--color-primary-500)]"
        />
        <span class="hidden sm:inline">Results</span>
      </router-link>

      <button type="button" @click="share" class="action-pill neutral" title="Share">
        <ShareIcon
          class="w-4 h-4 transition-colors group-hover:text-[color:var(--color-primary-500)]"
        />
        <span class="hidden sm:inline">Share</span>
      </button>
      <button
        v-role="['ADMIN']"
        @click="handleDelete"
        class="action-pill danger cursor-pointer"
        title="Delete survey"
      >
        <TrashIcon
          class="w-4 h-4 transition-colors group-hover:text-[color:var(--color-danger-600)]"
        />
        <span class="hidden sm:inline">Delete</span>
      </button>
    </div>
    <ConfirmDialog
      v-model="confirmDeleteOpen"
      message="Delete this survey?"
      @confirm="confirmDelete"
    />
  </li>
</template>

<script setup lang="ts">
import type { Survey } from '@/types/survey'
import { deleteSurvey } from '@/services/surveyService'

import { toastService } from '@/services/toastService'
import { ref } from 'vue'
import {
  ArrowRightOnRectangleIcon,
  ChartBarIcon,
  TrashIcon,
  ShareIcon,
} from '@heroicons/vue/24/solid'
import ConfirmDialog from '@/views/ConfirmDialog.vue'

const props = defineProps<{ survey: Survey }>()
const emit = defineEmits<{ (e: 'deleted', id: number): void }>()

const shareUrl =
  'https://x.com/share?url=' +
  encodeURIComponent(`${window.location.origin}/surveys/${props.survey.id}`)

const confirmDeleteOpen = ref(false)

function share() {
  const url = `${window.location.origin}/surveys/${props.survey.id}`
  if (navigator.share) {
    navigator.share({ title: props.survey.title, url }).catch(() => {
      window.open(shareUrl, '_blank', 'noopener')
    })
  } else {
    window.open(shareUrl, '_blank', 'noopener')
  }
}

function handleDelete() {
  confirmDeleteOpen.value = true
}

async function confirmDelete() {
  try {
    await deleteSurvey(props.survey.id)
    toastService.success('Survey deleted')
    emit('deleted', props.survey.id)
  } catch {
    toastService.error('Error deleting survey')
  }
}
</script>
