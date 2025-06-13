import { bootstrapApplication } from '@angular/platform-browser'
import { appConfig } from './app/app.config'
import { LayoutComponent } from './app/user-management/containers/layout/layout.component'
import * as Sentry from '@sentry/angular'
import { environment } from './environments/environment'

Sentry.init({
  dsn: environment.sentryDsn,
  environment: environment.production ? 'production' : 'development',
  sendDefaultPii: true,
})
bootstrapApplication(LayoutComponent, appConfig).catch((err) => console.error(err))
