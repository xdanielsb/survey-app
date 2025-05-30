<template>
  <div
    class="mb-6 p-6 rounded-[var(--radius-lg)] bg-white border border-[color:var(--color-neutral-200)] shadow-[var(--shadow-soft)] transition hover:shadow-[var(--shadow-card)]"
  >
    <h3 class="text-lg font-semibold text-[color:var(--color-neutral-800)] mb-4">
      {{ question.questionText }}
    </h3>

    <div ref="chartRef" class="w-full h-[250px]" />

    <p class="mt-4 text-sm text-[color:var(--color-neutral-600)]">
      <strong class="text-[color:var(--color-neutral-800)]">Average Score:</strong>
      {{ question.averageScore.toFixed(2) }}
    </p>
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
      textStyle: {
        fontFamily: 'var(--font-display)',
        color: 'var(--color-neutral-800)',
        fontWeight: 500,
      },
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
            shadowColor: 'rgba(0, 0, 0, 0.1)',
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
