import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SurveyView from '@/views/SurveyView.vue'
import SurveyResultsView from '@/views/SurveyResultsView.vue'
import CreateSurveyView from '@/views/CreateSurveyView.vue'
import LoginView from '@/views/LoginView.vue'
import { auth } from '@/firebase'

export const routes = [
  { path: '/', component: HomeView },
  { path: '/surveys/:id', component: SurveyView },
  { path: '/surveys/:id/results', component: SurveyResultsView },
  { path: '/create-survey', component: CreateSurveyView, meta: { requiresAuth: true } },
  { path: '/login', component: LoginView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth
  const user = auth?.currentUser

  if (requiresAuth && !user) {
    return next('/login')
  }

  next()
})

export default router
