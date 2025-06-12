import { Injectable } from '@angular/core'
import Keycloak from 'keycloak-js'
import { environment } from '../../../environments/environment'
import Cookies from 'js-cookie'

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private keycloak!: Keycloak

  init(): Promise<void> {
    if (!environment.keycloakUrl) {
      throw new Error('KEYCLOAK_URL is not defined in environment variables')
    }
    console.log(environment.keycloakUrl)
    this.keycloak = new Keycloak({
      url: environment.keycloakUrl,
      realm: environment.realm,
      clientId: 'backoffice',
    })
    return this.keycloak.init({ onLoad: 'login-required' }).then(() => {
      const token = this.keycloak?.token
      if (token) {
        Cookies.set('token', token, {
          secure: true,
          sameSite: 'strict',
        })
      }
    })
  }

  logout(): void {
    this.keycloak?.logout()
  }

  login(): void {
    this.keycloak?.login()
  }

  getToken(): string | undefined {
    return this.keycloak?.token
  }

  isLoggedIn(): boolean {
    return !!this.keycloak?.authenticated
  }

  getUsername(): string | undefined {
    return this.keycloak?.tokenParsed?.['preferred_username'] as string | undefined
  }
}
