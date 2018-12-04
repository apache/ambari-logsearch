import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeZoneMapInputComponent } from './time-zone-map-input.component';

describe('TimeZoneMapInputComponent', () => {
  let component: TimeZoneMapInputComponent;
  let fixture: ComponentFixture<TimeZoneMapInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeZoneMapInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeZoneMapInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
