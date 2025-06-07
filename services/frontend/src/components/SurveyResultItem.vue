<template>
  <div
    class="group relative overflow-hidden flex flex-col md:flex-row items-center gap-6 p-6 rounded-[var(--radius-lg)] bg-[color:var(--color-neutral-100)] border border-[color:var(--color-neutral-200)] shadow-[var(--shadow-card)] transition-transform duration-300 ease-[var(--ease-snappy)] hover:shadow-lg hover:-translate-y-1"
  >
    <div class="relative w-44 h-44 flex-shrink-0">
      <div ref="chartRef" class="absolute inset-0" />

      <!-- Center badge -->
      <div
        class="absolute inset-0 flex items-center justify-center text-center pointer-events-none"
      >
        <span
          class="inline-block px-3 py-1 rounded-full bg-[color:var(--color-neutral-100)] text-xs font-semibold text-[color:var(--color-neutral-700)] shadow-sm"
        >
          Avg {{ avgLabel }}
        </span>
      </div>
    </div>

    <!-- Question + Legend -->
    <div class="flex-1">
      <h3 class="text-lg font-display font-semibold text-[color:var(--color-neutral-900)] mb-4">
        {{ question.questionText }}
      </h3>

      <!-- Legend -->
      <ul class="grid gap-2 sm:grid-cols-2 md:grid-cols-1">
        <li
          v-for="entry in legendData"
          :key="entry.name"
          class="flex items-center gap-2 text-sm text-[color:var(--color-neutral-700)]"
        >
          <span class="block w-3 h-3 rounded-full" :style="{ backgroundColor: entry.color }" />
          <span class="truncate">{{ entry.name }}</span>
          <span class="ml-auto font-medium text-[color:var(--color-neutral-500)]">
            {{ entry.value }}%
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'
import { ref, onMounted, computed } from 'vue'
import type { QuestionResult } from '@/types/question-result'

/* ---------- Props ---------- */
const props = defineProps<{ question: QuestionResult }>()

/* ---------- Chart & Legend Data ---------- */
const chartRef = ref<HTMLDivElement | null>(null)

const rawData = computed(() =>
  Object.entries(props.question.distribution).map(([label, value]) => ({
    name: formatOption(label),
    value,
  })),
)

// nicer colors (extend or match Maverik palette)
const palette = [
  'var(--color-primary-600)',
  'var(--color-primary-400)',
  'var(--color-primary-200)',
  '#e0e7ff',
  'var(--color-primary-800)',
]

const legendData = computed(() =>
  rawData.value.map((d, i) => ({
    ...d,
    color: palette[i % palette.length],
  })),
)

/* ---------- Average label ---------- */
const avgLabel = computed(() => props.question.averageScore.toFixed(2))

/* ---------- Chart Init ---------- */
onMounted(() => {
  if (!chartRef.value) return

  const chart = echarts.init(chartRef.value, undefined, { renderer: 'svg' })

  chart.setOption({
    tooltip: { trigger: 'item' },
    animationDuration: 800,
    series: [
      {
        type: 'pie',
        radius: ['50%', '80%'],
        startAngle: 90,
        label: { show: false },
        labelLine: { show: false },
        data: legendData.value.map((d) => ({
          value: d.value,
          name: d.name,
          itemStyle: { color: d.color },
        })),
      },
    ],
  })

  // Resize with container
  window.addEventListener('resize', () => chart.resize())
})

/* ---------- Helpers ---------- */
function formatOption(text: string) {
  return text.replace(/([A-Z])/g, ' $1').replace(/^./, (s) => s.toUpperCase())
}
</script>
