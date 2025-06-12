import { TestBed } from '@angular/core/testing'
import { UsersComponent } from './users.component'
import { HttpClientTestingModule } from '@angular/common/http/testing'

describe('UsersComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsersComponent, HttpClientTestingModule],
    }).compileComponents()
  })

  it('should create', () => {
    const fixture = TestBed.createComponent(UsersComponent)
    const component = fixture.componentInstance
    expect(component).toBeTruthy()
  })
})
