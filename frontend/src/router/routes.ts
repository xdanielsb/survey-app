import HomeView from '@/views/HomeView.vue'
import SurveyView from '@/views/SurveyView.vue'
import SurveyResultsView from '@/views/SurveyResultsView.vue'
import CreateSurveyView from '@/views/CreateSurveyView.vue'
import PaymentSuccessView from '@/views/PaymentSuccessView.vue'

export const routes = [
  { path: '/', component: HomeView },
  { path: '/surveys/:id', component: SurveyView },
  { path: '/surveys/:id/results', component: SurveyResultsView },
  {
    path: '/create-survey',
    component: CreateSurveyView,
    meta: {
      requiresAuth: true,
      authMessage: 'Log in to create a survey.',
    },
  },
  {
    path: '/payment-success',
    component: PaymentSuccessView,
    meta: {
      requiresAuth: true,
      authMessage: 'Please log in to view your payment receipt.',
    },
  },
  {
    path: '/login',
    component: () => import('@/views/LoginView.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
  },
]
