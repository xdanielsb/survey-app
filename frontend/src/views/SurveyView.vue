<template>
  <div>
    <h2>{{ survey?.title }}</h2>

    <form v-if="survey" @submit.prevent="submitResponse">
      <div v-for="question in survey.questions" :key="question.id">
        <p>{{ question.questionText }}</p>
        <select v-model="answers[question.id]" required>
          <option disabled value="">Select</option>
          <option v-for="option in likertOptions" :key="option" :value="option">
            {{ option }}
          </option>
        </select>
      </div>

      <button type="submit">Submit</button>
    </form>

    <p v-else>Loading survey...</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router';
import type { Survey } from '@/types/survey'
import { fetchSurveyById, submitSurveyResponse } from '@/services/surveyService.ts'

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
  try {
    const id = route.params.id as string
    survey.value = await fetchSurveyById(id)
    if (!survey.value) {
      return;
    }
    for (const q of survey.value.questions) {
      answers.value[q.id] = ''
    }
  } catch (error) {
    console.error('Failed to load survey:', error)
  }
})

const submitResponse = async () => {
  const id = route.params.id as string
  const response = {
    answers: Object.entries(answers.value).map(([qId, answer]) => ({
      questionId: Number(qId),
      answer
    }))
  }

  try {
    await submitSurveyResponse(id, response)
    alert('Survey submitted successfully!')
  } catch (err) {
    console.error('Submission failed:', err)
    alert('Failed to submit survey.')
  }
}
</script>
