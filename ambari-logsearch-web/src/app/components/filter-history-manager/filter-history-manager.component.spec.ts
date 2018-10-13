import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterHistoryManagerComponent } from './filter-history-manager.component';

describe('FilterHistoryManagerComponent', () => {
  let component: FilterHistoryManagerComponent;
  let fixture: ComponentFixture<FilterHistoryManagerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FilterHistoryManagerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterHistoryManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
