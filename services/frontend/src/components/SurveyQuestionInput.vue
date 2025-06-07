<template>
  <div
    class="mb-8 p-6 rounded-[var(--radius-lg)] bg-[color:var(--color-neutral-100)] border border-[color:var(--color-neutral-200)] shadow-[var(--shadow-soft)] hover:shadow-[var(--shadow-card)] transition-shadow duration-300 ease-[var(--ease-snappy)]"
  >
    <label class="block mb-4 text-[1.1rem] font-semibold text-[color:var(--color-neutral-800)]">
      {{ question.questionText }}
    </label>

    <div class="flex flex-wrap gap-2">
      <button
        v-for="option in likertOptions"
        :key="option"
        type="button"
        @click="modelValue = option"
        :class="[
          'px-4 py-2 rounded-[var(--radius-sm)] border text-sm transition-all duration-200',
          modelValue === option
            ? 'bg-[color:var(--color-primary-600)] text-white border-[color:var(--color-primary-600)] font-semibold'
            : 'bg-[color:var(--color-neutral-50)] text-[color:var(--color-neutral-800)] border-[color:var(--color-neutral-300)] hover:bg-[color:var(--color-neutral-100)] hover:border-[color:var(--color-neutral-400)]',
        ]"
      >
        {{ option }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Question } from '@/types/question'

defineProps<{
  question: Question
  likertOptions: string[]
}>()

const modelValue = defineModel<string>()
</script>
