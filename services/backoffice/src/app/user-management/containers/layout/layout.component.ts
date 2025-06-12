import { Component } from '@angular/core'
import { RouterModule, RouterOutlet } from '@angular/router'
import { NgIf } from '@angular/common'
import { MatSidenavModule } from '@angular/material/sidenav'
import { MatToolbarModule } from '@angular/material/toolbar'
import { MatButtonModule } from '@angular/material/button'
import { MatIconModule } from '@angular/material/icon'
import { MatListModule } from '@angular/material/list'
import { KeycloakService } from '../../services/keycloak.service'

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
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent {
  dark = false
  constructor(private keycloak: KeycloakService) {
    this.dark = localStorage.getItem('theme') === 'dark'
    this.updateThemeClass()
  }

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

  toggleTheme() {
    this.dark = !this.dark
    localStorage.setItem('theme', this.dark ? 'dark' : 'light')
    this.updateThemeClass()
  }

  private updateThemeClass() {
    const html = document.documentElement
    if (this.dark) {
      html.classList.add('dark')
    } else {
      html.classList.remove('dark')
    }
  }
}
