<template>
  <div class="question-card">
    <label class="question-label">{{ question.questionText }}</label>
    <div class="options">
      <button
        v-for="option in likertOptions"
        :key="option"
        type="button"
        :class="['option', { selected: modelValue === option }]"
        @click="modelValue = option"
      >
        {{ option }}
      </button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { Question } from '@/types/question'

defineProps<{
  question: Question
  likertOptions: string[]
}>()

const modelValue = defineModel<string>()
</script>

<style scoped>
.question-card {
  margin-bottom: 2rem;
  padding: 1.5rem;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease;
}

.question-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.question-label {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  display: block;
  color: #333;
}

.options {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.option {
  padding: 0.6rem 1.2rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: #f9f9f9;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.95rem;
  color: #333;
}

.option:hover {
  background-color: #eef1f5;
  border-color: #bbb;
}

.option.selected {
  background-color: #007aff;
  color: white;
  border-color: #007aff;
  font-weight: 600;
}
</style>
