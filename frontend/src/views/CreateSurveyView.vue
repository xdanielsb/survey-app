<template>
  <div v-if="surveyCredits === null">Loading your survey credits...</div>

  <div v-else-if="surveyCredits > 0">
    <!-- Existing form and create button -->
    <div class="form-group">
      <label for="survey-title">Survey Title</label>
      <input
        id="survey-title"
        v-model="surveyTitle"
        type="text"
        class="text-input"
        placeholder="Enter the survey title"
      />
      <p v-if="errors.title" class="error-text">{{ errors.title }}</p>
    </div>

    <div class="form-group">
      <label>Questions</label>
      <div v-for="(question, index) in questions" :key="index" class="question-entry">
        <input
          v-model="question.text"
          type="text"
          class="text-input"
          placeholder="Enter question"
        />
        <button class="remove-btn" @click="removeQuestion(index)">Remove</button>
        <p v-if="errors.questions && errors.questions[index]" class="error-text">
          {{ errors.questions[index] }}
        </p>
      </div>
      <button class="add-btn" @click="addQuestion">+ Add Question</button>
    </div>

    <button class="submit-btn" @click="submitSurvey">Create Survey</button>
  </div>

  <div v-else>
    <p class="text-red-700 mb-4">
      ⚠️ You have 0 survey credits. Please buy one to create a survey.
    </p>
    <button class="submit-btn" @click="buyCredit">Buy a Survey Credit</button>
  </div>
</template>

<script lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { createSurvey } from '@/services/surveyService'
import { logger } from '@/plugins/logger'
import { toastService } from '@/services/toastService'
import { typedSurveySchema } from '@/validation/surveySchema.ts'
import { ValidationError } from 'yup'
import type { SurveyForm } from '@/validation/surveySchema.ts'
import { getUserCredits } from '@/services/creditService.ts'
import { buySurveyCredit } from '@/services/stripeService.ts'

export default {
  name: 'CreateSurveyView',
  setup() {
    const surveyTitle = ref('')
    const questions = ref([{ text: '' }])
    const errors = reactive<{ title?: string; questions: (string | undefined)[] }>({
      title: undefined,
      questions: [],
    })
    const surveyCredits = ref<number | null>(null)

    const addQuestion = () => {
      questions.value.push({ text: '' })
      errors.questions.push(undefined)
    }

    const removeQuestion = (index: number) => {
      questions.value.splice(index, 1)
      errors.questions.splice(index, 1)
    }

    const fetchCredits = async () => {
      try {
        const { credits } = await getUserCredits()
        surveyCredits.value = credits
      } catch (err) {
        toastService.error('Could not fetch your credits')
        logger.error(JSON.stringify(err))
      }
    }

    const buyCredit = () => {
      buySurveyCredit()
    }

    onMounted(() => {
      fetchCredits()
    })

    const validateSurvey = async (): Promise<boolean> => {
      const input: SurveyForm = {
        title: surveyTitle.value,
        questions: questions.value,
      }

      try {
        await typedSurveySchema.validate(input, { abortEarly: false })
        errors.title = undefined
        errors.questions = []
        return true
      } catch (err) {
        if (err instanceof ValidationError) {
          errors.title = undefined
          errors.questions = []

          err.inner.forEach((issue) => {
            if (issue.path === 'title') {
              errors.title = issue.message
            } else if (issue.path?.startsWith('questions')) {
              const match = issue.path.match(/questions\[(\d+)\]\.text/)
              if (match) {
                const index = Number(match[1])
                errors.questions[index] = issue.message
              }
            }
          })
        }
        return false
      }
    }

    const submitSurvey = async () => {
      const isValid = await validateSurvey()
      if (!isValid) {
        toastService.error('Please fix the errors before submitting.')
        return
      }

      const payload = {
        title: surveyTitle.value,
        questions: questions.value.map((q, index) => ({
          questionText: q.text,
          position: index,
        })),
      }

      try {
        await createSurvey(payload)
        toastService.success('Survey has been created successfully!')
        surveyTitle.value = ''
        questions.value = [{ text: '' }]
        errors.title = undefined
        errors.questions = []
      } catch (err) {
        toastService.error('Error creating survey')
        logger.error(JSON.stringify(err))
      }
    }

    return {
      surveyTitle,
      questions,
      errors,
      surveyCredits,
      addQuestion,
      removeQuestion,
      submitSurvey,
      buyCredit,
    }
  },
}
</script>

<style scoped>
.create-container {
  max-width: 700px;
  margin: 3rem auto;
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #333;
}

.form-group {
  margin-bottom: 2rem;
}

.text-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
  background-color: white;
}

.question-entry {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  align-items: center;
}

.add-btn,
.remove-btn,
.submit-btn {
  font-size: 0.95rem;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.add-btn {
  background-color: #eef1f5;
  color: #333;
  margin-top: 0.5rem;
}

.remove-btn {
  background-color: #f8d7da;
  color: #721c24;
}

.submit-btn {
  background-color: #007aff;
  color: white;
  width: 100%;
  margin-top: 2rem;
}

.add-btn:hover {
  background-color: #dde4ec;
}

.remove-btn:hover {
  background-color: #f5c6cb;
}

.submit-btn:hover {
  background-color: #0063cc;
}

.error-text {
  color: #d93025;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}
</style>
