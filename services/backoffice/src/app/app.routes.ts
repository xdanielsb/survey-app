import { Routes } from '@angular/router'
import { UsersComponent } from './user-management/containers/users/users.component'
import { PaymentsComponent } from './user-management/containers/payments/payments.component'

export const routes: Routes = [
  { path: '', redirectTo: 'users', pathMatch: 'full' },
  { path: 'users', component: UsersComponent },
  { path: 'payments', component: PaymentsComponent },
]
