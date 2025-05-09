<template>
  <div class="question-block">
    <h3>{{ question.questionText }}</h3>
    <div ref="chartRef" class="chart" />

    <p class="avg-score"><strong>Average Score:</strong> {{ question.averageScore.toFixed(2) }}</p>
  </div>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts'
import { onMounted, ref } from 'vue'
import type { QuestionResult } from '@/types/question-result'

const props = defineProps<{
  question: QuestionResult
}>()

const chartRef = ref<HTMLDivElement | null>(null)

onMounted(() => {
  if (!chartRef.value) return

  const chart = echarts.init(chartRef.value)
  const data = Object.entries(props.question.distribution).map(([label, value]) => ({
    name: formatOption(label),
    value,
  }))

  chart.setOption({
    title: {
      text: 'Responses',
      left: 'center',
    },
    tooltip: {
      trigger: 'item',
    },
    legend: {
      bottom: 0,
    },
    series: [
      {
        name: 'Answers',
        type: 'pie',
        radius: '50%',
        data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
          },
        },
      },
    ],
  })
})

function formatOption(option: string) {
  return option.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase())
}
</script>

<style scoped>
.question-block {
  background-color: #fdfdfd;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.avg-score {
  margin-top: 1rem;
  font-size: 0.95rem;
}

.chart {
  width: 100%;
  height: 250px;
}
</style>
