<template>
  <div
    v-if="survey"
    class="max-w-3xl mx-auto my-10 px-6 py-8 bg-white border border-[color:var(--color-neutral-200)] rounded-[var(--radius-lg)] shadow-[var(--shadow-soft)]"
  >
    <h2 class="text-2xl font-display font-semibold text-[color:var(--color-neutral-900)] mb-6">
      {{ survey.title }}
    </h2>

    <!-- Error message -->
    <p v-if="validationError" class="text-red-600 mb-4 text-sm font-medium">
      {{ validationError }}
    </p>

    <form @submit.prevent="submitResponse" class="space-y-6">
      <SurveyQuestionInput
        v-for="question in survey.questions"
        :key="question.id"
        v-model="answers[question.id]"
        :question="question"
        :likertOptions="likertOptions"
      />

      <div class="flex flex-wrap gap-4 justify-end mt-8">
        <button
          type="submit"
          class="px-5 py-2 text-sm font-medium rounded-[var(--radius-sm)] text-white bg-[color:var(--color-primary-600)] hover:bg-[color:var(--color-primary-700)] transition"
        >
          Submit
        </button>

        <RouterLink
          to="/"
          class="px-5 py-2 text-sm font-medium rounded-[var(--radius-sm)] bg-[color:var(--color-neutral-200)] text-[color:var(--color-neutral-800)] hover:bg-[color:var(--color-neutral-300)] transition"
        >
          Return to Home
        </RouterLink>
      </div>
    </form>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { fetchSurveyById, submitSurveyResponse } from '@/services/surveyService'
import SurveyQuestionInput from '@/components/SurveyQuestionInput.vue'
import type { SurveyResponse } from '@/types/survey-response'
import type { Survey } from '@/types/survey'
import { toastService } from '@/services/toastService'

const route = useRoute()
const router = useRouter()
const survey = ref<Survey | null>(null)
const answers = ref<Record<number, string>>({})
const validationError = ref<string | null>(null)

const likertOptions = ['Totally disagree', 'Disagree', 'Neutral', 'Agree', 'Fully agree']

onMounted(async () => {
  const id = Number(route.params.id)
  survey.value = await fetchSurveyById(id)
})

const submitResponse = async () => {
  if (!survey.value) return

  // Validate all questions are answered
  const unanswered = survey.value.questions.filter((q) => !answers.value[q.id])
  if (unanswered.length > 0) {
    validationError.value = 'Please answer all questions before submitting.'
    return
  }

  validationError.value = null

  const response: SurveyResponse = {
    surveyId: survey.value.id,
    answers: Object.entries(answers.value).map(([qId, answer]) => ({
      questionId: Number(qId),
      answer,
    })),
  }

  await submitSurveyResponse(survey.value.id, response)
  toastService.success('Survey has been submitted')
  router.push('/')
}
</script>
