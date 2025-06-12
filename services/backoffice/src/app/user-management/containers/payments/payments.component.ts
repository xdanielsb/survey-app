import { Component, OnInit } from '@angular/core'
import { NgFor } from '@angular/common'
import { MatTableModule, MatTableDataSource } from '@angular/material/table'
import { Payment } from '../../models/payment'
import { PaymentsService } from '../../services/payment.service'

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [MatTableModule, NgFor],
  templateUrl: './payments.component.html',
  styleUrl: './payments.component.scss',
})
export class PaymentsComponent implements OnInit {
  displayedColumns = ['id', 'email', 'amountCents', 'creditsGranted', 'status']
  dataSource = new MatTableDataSource<Payment>([])

  constructor(private paymentsService: PaymentsService) {}

  ngOnInit(): void {
    this.paymentsService.getPayments().subscribe((payments) => (this.dataSource.data = payments))
  }
}
