import { Component } from '@angular/core'
import { RouterModule, RouterOutlet } from '@angular/router'
import { NgIf } from '@angular/common'
import { MatSidenavModule } from '@angular/material/sidenav'
import { MatToolbarModule } from '@angular/material/toolbar'
import { MatButtonModule } from '@angular/material/button'
import { MatIconModule } from '@angular/material/icon'
import { MatListModule } from '@angular/material/list'
import { KeycloakService } from '../../services/keycloak.service'
import { UsersComponent } from '../users/users.component'

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterModule,
    RouterOutlet,
    NgIf,
    MatSidenavModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    UsersComponent,
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent {
  constructor(private keycloak: KeycloakService) {}

  login() {
    this.keycloak.login()
  }

  logout() {
    this.keycloak.logout()
  }

  get username(): string | undefined {
    return this.keycloak.getUsername()
  }

  get isLoggedIn(): boolean {
    return this.keycloak.isLoggedIn()
  }
}
