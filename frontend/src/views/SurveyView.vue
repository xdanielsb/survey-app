<template>
  <div class="survey-container" v-if="survey">
    <h2>{{ survey.title }}</h2>
    <form @submit.prevent="submitResponse">
      <SurveyQuestionInput
        v-for="question in survey.questions"
        :key="question.id"
        v-model="answers[question.id]"
        :question="question"
        :likertOptions="likertOptions"
      />

      <div class="actions">
        <button type="submit" class="btn primary">Submit</button>
        <RouterLink to="/" class="btn secondary">Return to Home</RouterLink>
      </div>
    </form>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { fetchSurveyById, submitSurveyResponse } from '@/services/surveyService'
import SurveyQuestionInput from '@/components/SurveyQuestionInput.vue'
import type { SurveyResponseDTO } from '@/types/survey-response-dto'
import type { Survey } from '@/types/survey'

import { toastService } from '@/services/toastService'

const route = useRoute()
const router = useRouter()
const survey = ref<Survey | null>(null)
const answers = ref<Record<number, string>>({})

const likertOptions = ['Totally disagree', 'Disagree', 'Neutral', 'Agree', 'Fully agree']

onMounted(async () => {
  const id = Number(route.params.id)
  survey.value = await fetchSurveyById(id)
})

const submitResponse = async () => {
  if (!survey.value) return

  const response: SurveyResponseDTO = {
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

<style scoped>
.survey-container {
  margin: auto;
  padding: 1rem;
}
</style>
