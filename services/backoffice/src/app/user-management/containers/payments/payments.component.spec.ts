import { TestBed } from '@angular/core/testing'
import { PaymentsComponent } from './payments.component'
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('PaymentsComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentsComponent, HttpClientTestingModule],
    }).compileComponents()
  })

  it('should create', () => {
    const fixture = TestBed.createComponent(PaymentsComponent)
    const component = fixture.componentInstance
    expect(component).toBeTruthy()
  })
})
