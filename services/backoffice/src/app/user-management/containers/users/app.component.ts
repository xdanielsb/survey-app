import { Component, OnInit } from '@angular/core'
import { RouterOutlet } from '@angular/router'
import { NgFor } from '@angular/common'
import { MatToolbarModule } from '@angular/material/toolbar'
import { MatButtonModule } from '@angular/material/button'
import { MatIconModule } from '@angular/material/icon'
import { MatMenuModule } from '@angular/material/menu'
import { User } from '../../models/user'
import { UserService } from '../../services/users.service'
import { KeycloakService } from '../../services/keycloak.service'

@Component({
  selector: 'app-root',
  imports: [
    NgFor,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  title = 'backoffice'
  users: User[] = []

  constructor(
    private userService: UserService,
    private keycloak: KeycloakService,
  ) {}

  ngOnInit(): void {
    this.userService.getUsers().subscribe((users) => (this.users = users))
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
}
