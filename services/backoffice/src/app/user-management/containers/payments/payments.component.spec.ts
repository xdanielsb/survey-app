import { TestBed } from '@angular/core/testing'
import { PaymentsComponent } from './payments.component'

describe('PaymentsComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentsComponent],
    }).compileComponents()
  })

  it('should create', () => {
    const fixture = TestBed.createComponent(PaymentsComponent)
    const component = fixture.componentInstance
    expect(component).toBeTruthy()
  })
})
