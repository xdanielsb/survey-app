import { Component, OnInit } from '@angular/core'
import { RouterOutlet } from '@angular/router'
import { NgFor } from '@angular/common'
import { User } from '../../models/user'
import { UserService } from '../../services/users.service'

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NgFor],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {
  title = 'backoffice'
  users: User[] = []

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUsers().subscribe((users) => (this.users = users))
  }
}
