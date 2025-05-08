<template>
  <div>
    <h2>Results for: {{ results?.title }}</h2>
    <div v-for="result in results?.questions" :key="result.id">
      <h3>{{ result.text }}</h3>
      <ul>
        <li v-for="(count, option) in result.answers" :key="option">
          {{ option }}: {{ count }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const results = ref<any>(null)

onMounted(async () => {
  const id = route.params.id
  const res = await axios.get(`/surveys/api/${id}/results`)
  results.value = res.data
})
</script>
