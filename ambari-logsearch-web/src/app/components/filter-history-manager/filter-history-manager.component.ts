import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs/Subject';

@Component({
  selector: 'filter-history-manager',
  templateUrl: './filter-history-manager.component.html',
  styleUrls: ['./filter-history-manager.component.less']
})
export class FilterHistoryManagerComponent implements OnInit, OnDestroy {

  destroyed$ = new Subject();

  constructor() { }

  ngOnInit() {
  }

  ngOnDestroy() {
    this.destroyed$.next(true);
  }

}
