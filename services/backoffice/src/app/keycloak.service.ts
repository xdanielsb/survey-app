import { Injectable } from '@angular/core';
import Keycloak, { KeycloakInstance } from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private keycloak!: KeycloakInstance;

  init(): Promise<void> {
    this.keycloak = new Keycloak({
      url: 'http://localhost:8080',
      realm: 'survey',
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
