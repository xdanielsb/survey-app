import '@/assets/styles/global.css'
import ToastPlugin from 'vue-toast-notification'
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import { vRole } from '@/directives/has-role.ts'
import App from './App.vue'
import router from './router'
import * as Sentry from '@sentry/vue'

import 'vue-toast-notification/dist/theme-default.css'

const app = createApp(App)

Sentry.init({
  app,
  dsn: import.meta.env.SENTRY_AUTH_DSN,
  sendDefaultPii: true,
})

app.use(router)
app.use(ToastPlugin)
app.use(createPinia())

app.directive('role', vRole)

app.mount('#app')
