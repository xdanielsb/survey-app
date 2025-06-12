import { ApplicationConfig, APP_INITIALIZER, provideZoneChangeDetection } from '@angular/core'
import { provideRouter } from '@angular/router'

import { KeycloakService } from './user-management/services/keycloak.service'

export function initKeycloak(keycloak: KeycloakService) {
  return () => keycloak.init()
}

import { routes } from './app.routes'
import { provideHttpClient, withInterceptors } from '@angular/common/http'
import { authInterceptor } from './user-management/interceptors/auth.interceptor'

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    {
      provide: APP_INITIALIZER,
      useFactory: initKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
  ],
}
