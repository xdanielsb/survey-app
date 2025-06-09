<script setup lang="ts" strict>
import { ref, reactive, onMounted, computed } from 'vue'
import { createSurvey } from '@/services/surveyService'
import { getUserCredits } from '@/services/creditService'
import { buySurveyCredit } from '@/services/stripeService'
import { toastService } from '@/services/toastService'
import { typedSurveySchema } from '@/validation/surveySchema'
import type { SurveyForm } from '@/validation/surveySchema'
import { ValidationError } from 'yup'
import { logger } from '@/plugins/logger'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()
const surveyTitle = ref('')
const questions = ref([{ text: '' }])
const surveyCredits = ref<number | null>(null)

const errors = reactive<{ title?: string; questions: (string | undefined)[] }>({
  title: undefined,
  questions: [],
})

/* computed array solely for rendering errors */
const questionErrorList = computed(() =>
  errors.questions.map((msg, idx) => ({ msg, idx })).filter(({ msg }) => Boolean(msg)),
)

const addQuestion = () => {
  questions.value.push({ text: '' })
  errors.questions.push(undefined)
}
const removeQuestion = (i: number) => {
  questions.value.splice(i, 1)
  errors.questions.splice(i, 1)
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    surveyCredits.value = null
    return
  }
  try {
    surveyCredits.value = (await getUserCredits()).credits
  } catch (err) {
    logger.error(JSON.stringify(err))
  }
})

/* validation */
async function validateSurvey(): Promise<boolean> {
  const input: SurveyForm = { title: surveyTitle.value, questions: questions.value }
  try {
    await typedSurveySchema.validate(input, { abortEarly: false })
    errors.title = undefined
    errors.questions = []
    return true
  } catch (err) {
    if (err instanceof ValidationError) {
      errors.title = undefined
      errors.questions = []
      err.inner.forEach(({ path, message }) => {
        if (path === 'title') errors.title = message
        else if (path?.startsWith('questions')) {
          const idx = Number(path.match(/questions\[(\d+)]/)?.[1] ?? -1)
          if (idx >= 0) errors.questions[idx] = message
        }
      })
    }
    return false
  }
}

/* submit */
async function submitSurvey() {
  if (!(await validateSurvey())) {
    toastService.error('Please fix the errors before submitting.')
    return
  }
  try {
    const survey = await createSurvey({
      title: surveyTitle.value,
      questions: questions.value.map((q, i) => ({
        questionText: q.text,
        position: i,
      })),
    })
    toastService.success('Survey created successfully!')
    surveyTitle.value = ''
    questions.value = [{ text: '' }]
    router.push(`/surveys/${survey.id}/results`)
  } catch (err) {
    toastService.error('Error creating survey')
    logger.error(JSON.stringify(err))
  }
}

const buyCredit = () => buySurveyCredit()
</script>

<template>
  <div v-if="surveyCredits === null" class="text-center text-[color:var(--color-neutral-600)]">
    Fetching your survey credits…
  </div>

  <div
    v-else-if="surveyCredits > 0"
    class="mx-auto max-w-3xl p-8 space-y-8 bg-[color:var(--color-neutral-100)] rounded-[var(--radius-lg)] shadow-[var(--shadow-soft)] ring-1 ring-[color:var(--color-neutral-200)]"
  >
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-display font-semibold">Create Survey</h1>
      <span
        class="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-medium bg-[color:var(--color-neutral-100)] text-[color:var(--color-neutral-700)]"
      >
        {{ surveyCredits }} credit{{ surveyCredits === 1 ? '' : 's' }}
      </span>
    </div>

    <div>
      <label for="survey-title" class="block mb-1 text-sm font-medium">Survey Title</label>
      <input
        id="survey-title"
        v-model="surveyTitle"
        type="text"
        required
        class="w-full rounded-[var(--radius-sm)] border border-[color:var(--color-neutral-300)] bg-[color:var(--color-neutral-50)] px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[color:var(--color-primary-500)]"
      />
      <p v-if="errors.title" class="mt-1 text-xs text-red-600">{{ errors.title }}</p>
    </div>

    <div>
      <label class="block mb-2 text-sm font-medium">Questions</label>

      <TransitionGroup name="flip" tag="div" class="space-y-3">
        <div v-for="(q, i) in questions" :key="i" class="flex items-center gap-3">
          <input
            v-model="q.text"
            type="text"
            required
            class="flex-1 rounded-[var(--radius-sm)] border border-[color:var(--color-neutral-300)] bg-[color:var(--color-neutral-50)] px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[color:var(--color-primary-500)]"
            placeholder="Enter question"
          />
          <button
            @click="removeQuestion(i)"
            type="button"
            class="px-3 py-1.5 rounded-[var(--radius-sm)] bg-red-100 text-red-700 hover:bg-red-200 text-xs transition"
          >
            Remove
          </button>
        </div>
      </TransitionGroup>

      <!-- ✅ error list without v-if inside v-for -->
      <template v-for="err in questionErrorList" :key="'err' + err.idx">
        <p class="mt-1 text-xs text-red-600">{{ err.msg }}</p>
      </template>

      <button
        @click="addQuestion"
        type="button"
        class="mt-4 inline-flex items-center gap-1 px-4 py-2 rounded-[var(--radius-sm)] bg-[color:var(--color-neutral-100)] text-[color:var(--color-neutral-700)] hover:bg-[color:var(--color-neutral-200)] text-sm transition"
      >
        + Add Question
      </button>
    </div>

    <!-- Submit -->
    <button
      @click="submitSurvey"
      type="button"
      class="w-full py-3 rounded-[var(--radius-sm)] bg-[color:var(--color-primary-600)] text-white hover:bg-[color:var(--color-primary-700)] font-medium transition"
    >
      Create Survey
    </button>
  </div>

  <!-- No credit state -->
  <div v-else class="mx-auto max-w-md text-center space-y-6">
    <p class="text-[color:var(--color-neutral-700)]">
      ⚠️ You have no survey credits.<br />
      Buy one to create a survey.
    </p>
    <button
      @click="buyCredit"
      type="button"
      class="w-full py-3 rounded-[var(--radius-sm)] bg-[color:var(--color-primary-600)] text-white hover:bg-[color:var(--color-primary-700)] font-medium transition"
    >
      Buy a Survey Credit
    </button>
  </div>
</template>

<style>
.flip-enter-from,
.flip-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
.flip-enter-active,
.flip-leave-active {
  transition: all 250ms var(--ease-snappy);
}
</style>
