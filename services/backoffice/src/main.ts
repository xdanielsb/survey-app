import { bootstrapApplication } from '@angular/platform-browser'
import { appConfig } from './app/app.config'
import { LayoutComponent } from './app/user-management/containers/layout/layout.component'

bootstrapApplication(LayoutComponent, appConfig).catch((err) => console.error(err))
