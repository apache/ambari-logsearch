import { TestBed, inject } from '@angular/core/testing';

import { FilterLabelsService } from './filter-labels.service';

describe('FilterLabelsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FilterLabelsService]
    });
  });

  it('should be created', inject([FilterLabelsService], (service: FilterLabelsService) => {
    expect(service).toBeTruthy();
  }));
});
