<template>
  <teleport to="body">
    <Transition name="fade-scale">
      <div
        v-if="modelValue"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4"
        @click.self="$emit('update:modelValue', false)"
      >
        <div
          class="p-6 rounded-[var(--radius-lg)] bg-[color:var(--color-neutral-100)] shadow-[var(--shadow-soft)] space-y-4 text-center"
        >
          <p class="text-lg text-[color:var(--color-neutral-900)]">{{ message }}</p>
          <div class="flex justify-center gap-4">
            <button
              @click="onConfirm"
              class="px-4 py-2 rounded-[var(--radius-sm)] bg-[color:var(--color-danger-600)] text-white hover:bg-[color:var(--color-danger-700)] transition"
            >
              Confirm
            </button>
            <button
              @click="$emit('update:modelValue', false)"
              class="px-4 py-2 rounded-[var(--radius-sm)] border border-[color:var(--color-neutral-300)] bg-[color:var(--color-neutral-100)] hover:bg-[color:var(--color-neutral-200)] transition"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </teleport>
</template>

<script setup lang="ts">
defineProps<{ modelValue: boolean; message: string }>()
const emit = defineEmits<{ (e: 'update:modelValue', value: boolean): void; (e: 'confirm'): void }>()

function onConfirm() {
  emit('confirm')
  emit('update:modelValue', false)
}
</script>

<style scoped>
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all 0.25s var(--ease-snappy);
}
</style>
