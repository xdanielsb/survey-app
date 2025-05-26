import { loadStripe } from '@stripe/stripe-js'
import api from '@/services/api.ts'

const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY)

export async function buySurveyCredit() {
  const stripe = await stripePromise
  const res = await api.post('/payments/session')
  const sessionId = res.data.sessionId
  stripe?.redirectToCheckout({ sessionId: sessionId })
}
