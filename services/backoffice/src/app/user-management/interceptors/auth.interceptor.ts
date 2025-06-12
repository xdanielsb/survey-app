import { HttpInterceptorFn } from '@angular/common/http'
import { inject } from '@angular/core'
import { environment } from '../../../environments/environment'
import { KeycloakService } from '../services/keycloak.service'

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!environment.production) {
    const keycloak = inject(KeycloakService)
    const token = keycloak.getToken()
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    }
  }
  return next(req)
}
