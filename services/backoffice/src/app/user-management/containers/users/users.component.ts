import { Component, OnInit } from '@angular/core'
import { NgFor } from '@angular/common'
import { MatTableModule, MatTableDataSource } from '@angular/material/table'
import { User } from '../../models/user'
import { UserService } from '../../services/users.service'

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [MatTableModule, NgFor],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  displayedColumns = ['id', 'email', 'surveyCredits', 'premium']
  dataSource = new MatTableDataSource<User>([])

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUsers().subscribe((users) => (this.dataSource.data = users))
  }
}
