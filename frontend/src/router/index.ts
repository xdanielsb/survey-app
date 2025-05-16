import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SurveyView from '@/views/SurveyView.vue'
import SurveyResultsView from '@/views/SurveyResultsView.vue'
import CreateSurveyView from '@/views/CreateSurveyView.vue'

export const routes = [
  { path: '/', component: HomeView },
  { path: '/surveys/:id', component: SurveyView },
  { path: '/surveys/:id/results', component: SurveyResultsView },
  { path: '/create-survey', component: CreateSurveyView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
