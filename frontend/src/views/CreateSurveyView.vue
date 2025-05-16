<template>
  <div class="create-container">
    <h1 class="title">Create New Survey</h1>

    <div class="form-group">
      <label for="survey-title">Survey Title</label>
      <input
        id="survey-title"
        v-model="surveyTitle"
        type="text"
        class="text-input"
        placeholder="Enter the survey title"
      />
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
      </div>
      <button class="add-btn" @click="addQuestion">+ Add Question</button>
    </div>

    <button class="submit-btn" @click="submitSurvey">Create Survey</button>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue'
import { createSurvey } from '@/services/surveyService'

export default {
  name: 'CreateSurveyView',
  setup() {
    const surveyTitle = ref('')
    const questions = ref([{ text: '' }])

    const addQuestion = () => questions.value.push({ text: '' })
    const removeQuestion = (index: number) => questions.value.splice(index, 1)

    const submitSurvey = async () => {
      const payload = {
        title: surveyTitle.value,
        questions: questions.value.map((q, index) => ({
          questionText: q.text,
          position: index,
        })),
      }

      try {
        await createSurvey(payload)
        alert('Survey created!')
        surveyTitle.value = ''
        questions.value = [{ text: '' }]
      } catch (err) {
        alert('Error creating survey')
        console.error(err)
      }
    }

    return {
      surveyTitle,
      questions,
      addQuestion,
      removeQuestion,
      submitSurvey,
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
</style>
