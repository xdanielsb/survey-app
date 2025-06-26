<template>
  <div class="p-4 border rounded-[var(--radius-lg)] bg-[color:var(--color-neutral-100)]">
    <div class="space-y-2 mb-4 max-h-60 overflow-y-auto">
      <div
        v-for="(msg, i) in messages"
        :key="i"
        :class="msg.role === 'user' ? 'text-right' : 'text-left'"
      >
        <span
          class="inline-block px-3 py-1 rounded-md"
          :class="
            msg.role === 'user'
              ? 'bg-[color:var(--color-primary-200)]'
              : 'bg-[color:var(--color-neutral-200)]'
          "
        >
          {{ msg.content }}
        </span>
      </div>
    </div>
    <div v-if="loading" class="text-left">
      <span
        class="inline-flex items-center px-3 py-1 rounded-md bg-[color:var(--color-neutral-200)]"
      >
        <ArrowPathIcon class="w-4 h-4 mr-2 animate-spin" />
        Thinking...
      </span>
      <p class="text-xs mt-1 text-[color:var(--color-neutral-600)]">
        Lots of people are chatting at once. If nothing appears, stay hungry, stay foolish and try
        again shortly.
      </p>
    </div>
    <form @submit.prevent="handleSend" class="flex gap-2">
      <input
        v-model="input"
        type="text"
        class="flex-1 px-3 py-1 border rounded-[var(--radius-sm)]"
        placeholder="Ask a question about this survey..."
      />
      <button
        type="submit"
        class="px-4 py-1 rounded-[var(--radius-sm)] bg-[color:var(--color-primary-600)] text-white"
      >
        Send
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { askSurveyQuestion } from '@/services/surveyService'
import type { LlmResponse } from '@/types/llm'
import { ArrowPathIcon } from '@heroicons/vue/24/solid'

const props = defineProps<{ surveyId: number }>()

const input = ref('')
const messages = ref<{ role: 'user' | 'assistant'; content: string }[]>([])
const loading = ref(false)

const handleSend = async () => {
  const question = input.value.trim()
  if (!question) return
  messages.value.push({ role: 'user', content: question })
  input.value = ''
  loading.value = true
  try {
    const response: LlmResponse = await askSurveyQuestion(props.surveyId, question)
    messages.value.push({ role: 'assistant', content: response.answer })
    // eslint-disable-next-line @typescript-eslint/no-explicit-any,@typescript-eslint/no-unused-vars
  } catch (err: any) {
    messages.value.push({
      role: 'assistant',
      content: 'Sorry, the server is busy. Please try again soon.',
    })
  } finally {
    loading.value = false
  }
}
</script>
