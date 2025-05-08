import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SurveyView from '@/views/SurveyView.vue'
import SurveyResultsView from '@/views/SurveyResultsView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/surveys/:id', component: SurveyView },
  { path: '/surveys/:id/results', component: SurveyResultsView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
