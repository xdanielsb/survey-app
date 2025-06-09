import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { UserService, User } from '../user.service';

@Component({
  selector: 'app-user-table',
  standalone: true,
  imports: [CommonModule, MatTableModule],
  templateUrl: './user-table.component.html',
})
export class UserTableComponent implements OnInit {
  displayedColumns = ['id', 'email', 'premium', 'surveyCredits', 'roles'];
  dataSource = new MatTableDataSource<User>();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUsers().subscribe((users) => {
      this.dataSource.data = users;
    });
  }
}
