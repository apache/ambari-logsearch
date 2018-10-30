import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Observable } from 'rxjs/Observable';
import { Store } from '@ngrx/store';
import { AppStore } from '@app/classes/models/store';
import {
  selectActiveFilterHistoryChangesUndoItems,
  selectActiveFilterHistoryChangesRedoItems,
  selectActiveFilterHistoryChanges,
  selectActiveFilterHistoryChangeIndex
} from '@app/store/selectors/filter-history.selectors';
import { FilterUrlParamChange } from '@app/classes/models/filter-url-param-change.interface';
import { Router } from '@angular/router';

@Component({
  selector: 'filter-history-manager',
  templateUrl: './filter-history-manager.component.html',
  styleUrls: ['./filter-history-manager.component.less']
})
export class FilterHistoryManagerComponent implements OnInit, OnDestroy {

  activeHistoryChangeIndex$: Observable<number> = this.store.select(selectActiveFilterHistoryChangeIndex);

  activeHistoryItems$: Observable<FilterUrlParamChange[]> = this.store.select(selectActiveFilterHistoryChanges);
  hasActiveHistoryItems$: Observable<boolean> = this.activeHistoryItems$
    .map(items => items && items.length > 0).startWith(false);

  activeUndoHistoryItems$: Observable<FilterUrlParamChange[]> = this.store.select(selectActiveFilterHistoryChangesUndoItems);
  hasActiveUndoHistoryItems$: Observable<boolean> = this.activeUndoHistoryItems$
    .map(items => items && items.length > 0).startWith(false);

  activeRedoHistoryItems$: Observable<FilterUrlParamChange[]> = this.store.select(selectActiveFilterHistoryChangesRedoItems);
  hasActiveRedoHistoryItems$: Observable<boolean> = this.activeRedoHistoryItems$
    .map(items => items && items.length > 0).startWith(false);

  destroyed$ = new Subject();

  constructor(
    private store: Store<AppStore>,
    private router: Router
  ) { }

  ngOnInit() {}

  ngOnDestroy() {
    this.destroyed$.next(true);
  }

  navigateToFilterUrlParamChangeItem = (item: FilterUrlParamChange) => {
    this.router.navigateByUrl(item.currentPath);
  }

  undo(item?: FilterUrlParamChange): void {
    ( item ?
      Observable.of(item)
      : this.activeUndoHistoryItems$.map((changes: FilterUrlParamChange[]) => changes[changes.length - 1])
    ).first().subscribe(this.navigateToFilterUrlParamChangeItem);
  }

  redo(item?) {
    ( item ?
      Observable.of(item)
      : this.activeRedoHistoryItems$.map((changes: FilterUrlParamChange[]) => changes[0])
    ).first().subscribe(this.navigateToFilterUrlParamChangeItem);
  }

}
