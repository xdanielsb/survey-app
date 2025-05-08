<template>
  <div>
    <h2>{{ survey?.title }}</h2>
    <form @submit.prevent="submitResponse">
      <div v-for="question in survey?.questions" :key="question.id">
        <p>{{ question.text }}</p>
        <select v-model="answers[question.id]" required>
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
import axios from 'axios'

const route = useRoute()
const survey = ref<any>(null)
const answers = ref<Record<number, string>>({})

const likertOptions = [
  'Totally disagree',
  'Disagree',
  'Neutral',
  'Agree',
  'Fully agree'
]

onMounted(async () => {
  const id = route.params.id
  const res = await axios.get(`/surveys/${id}`)
  survey.value = res.data
})

const submitResponse = async () => {
  const id = route.params.id
  const response = {
    answers: Object.entries(answers.value).map(([qId, answer]) => ({
      questionId: Number(qId),
      answer
    }))
  }
  await axios.post(`/surveys/${id}/responses`, response)
  alert('Submitted!')
}
</script>
