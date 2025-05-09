<script setup lang="ts">
import { RouterView } from 'vue-router'

const mode = import.meta.env.MODE
</script>

<template>
  <main class="container mx-auto p-6">
    <div class="env-indicator">
      Environment: <strong>{{ mode }}</strong>
    </div>
    <RouterView />
  </main>
</template>

<style scoped>
.env-indicator {
  background-color: #eee;
  color: #333;
  font-size: 0.875rem;
  padding: 0.25rem 1rem;
  text-align: right;
  border-bottom: 1px solid #ccc;
}
</style>
