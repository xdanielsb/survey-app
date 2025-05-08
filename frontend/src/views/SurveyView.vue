<template>
  <div class="survey-container" v-if="survey">
    <h2>{{ survey.title }}</h2>
    <form @submit.prevent="submitResponse">
      <div
        class="question"
        v-for="question in survey.questions"
        :key="question.id"
      >
        <label :for="'q-' + question.id">{{ question.questionText }}</label>
        <select
          v-model="answers[question.id]"
          :id="'q-' + question.id"
          required
        >
          <option disabled value="">Select</option>
          <option v-for="option in likertOptions" :key="option" :value="option">
            {{ option }}
          </option>
        </select>
      </div>
      <button type="submit">Submit</button>
    </form>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { fetchSurveyById, submitSurveyResponse } from '@/services/surveyService'
import type { SurveyResponseDTO } from '@/types/survey-response-dto.ts'
import type { Survey } from '@/types/survey.ts'

const route = useRoute()
const survey = ref<Survey | null>(null)
const answers = ref<Record<number, string>>({})

const likertOptions = [
  'Totally disagree',
  'Disagree',
  'Neutral',
  'Agree',
  'Fully agree'
]

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
      answer
    }))
  }

  await submitSurveyResponse(survey.value.id, response)
  alert('Survey submitted!')
}
</script>

<style scoped>
.survey-container {
  max-width: 600px;
  margin: auto;
  padding: 1rem;
}

.question {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

select {
  width: 100%;
  padding: 0.5rem;
  font-size: 1rem;
}

button {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  cursor: pointer;
}
</style>
