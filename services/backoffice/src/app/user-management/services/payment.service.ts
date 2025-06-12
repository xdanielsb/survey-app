import { Injectable, inject } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
import { environment } from '../../../environments/environment'
import { Payment } from '../models/payment'

@Injectable({ providedIn: 'root' })
export class PaymentsService {
  private http = inject(HttpClient)
  private baseUrl = environment.backendUrl

  getPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.baseUrl}/payments`)
  }

}
