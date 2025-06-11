import { Injectable } from '@angular/core';
import Keycloak, { KeycloakInstance } from 'keycloak-js';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private keycloak!: KeycloakInstance;

  init(): Promise<void> {
    if (!environment.keycloakUrl) {
      throw new Error('KEYCLOAK_URL is not defined in environment variables');
    }
    console.log(environment.keycloakUrl);
    this.keycloak = new Keycloak({
      url: environment.keycloakUrl,
      realm: environment.realm,
      clientId: 'backoffice'
    });
    return this.keycloak.init({ onLoad: 'login-required' }).then(() => undefined);
  }

  logout(): void {
    this.keycloak?.logout();
  }

  getToken(): string | undefined {
    return this.keycloak?.token;
  }
}
