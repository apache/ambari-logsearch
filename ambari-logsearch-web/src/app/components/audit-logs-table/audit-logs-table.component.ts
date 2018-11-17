/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Component, Input} from '@angular/core';
import {ListItem} from '@app/classes/list-item';
import {LogsTableComponent} from '@app/classes/components/logs-table/logs-table-component';
import {LogsContainerService} from '@app/services/logs-container.service';
import { Store } from '@ngrx/store';
import { AppStore } from '@app/classes/models/store';
import { Observable } from 'rxjs/Observable';
import { createAuditLogsFieldComponentFieldsSelectorByComponentName } from '@app/store/selectors/audit-logs-fields.selectors';
import { LogField } from '@app/classes/object';

@Component({
  selector: 'audit-logs-table',
  templateUrl: './audit-logs-table.component.html',
  styleUrls: ['./audit-logs-table.component.less']
})
export class AuditLogsTableComponent extends LogsTableComponent {

  @Input()
  commonFieldNames: string[];

  readonly customProcessedColumns: string[] = ['evtTime'];

  readonly timeFormat = 'YYYY-MM-DD HH:mm:ss';

  private readonly logsType = 'auditLogs';

  get displayedCommonColumns(): string[] {
    return this.commonFieldNames.reduce(
      (fieldNames: string[], fieldName: string): string[] => {
        return this.isColumnDisplayed(fieldName) ? [...fieldNames, fieldName] : fieldNames;
      },
      []
    );
  }

  get displayedNotCommonColumns(): string[] {
    return this.displayedColumns.filter((column: ListItem) => this.commonFieldNames.indexOf(column.value) === -1)
      .map((column: ListItem) => column.value);
  }

  constructor(
    private logsContainer: LogsContainerService,
    private store: Store<AppStore>
  ) {
    super();
  }

  get filters(): any {
    return this.logsContainer.filters;
  }

  get timeZone(): string {
    return this.logsContainer.timeZone;
  }

  getColumnByName(name: string): ListItem | undefined {
    return this.columns.find((column: ListItem): boolean => column.value === name);
  }

  updateSelectedColumns(columns: string[]): void {
    this.logsContainer.updateSelectedColumns(columns, this.logsType);
  }

  isCommonField(fieldName: string): boolean {
    return this.commonFieldNames.indexOf(fieldName) > -1;
  }

  fieldIsAvailableForComponent(field: string, componentName: string): Observable<boolean> {
    return this.store.select(createAuditLogsFieldComponentFieldsSelectorByComponentName(componentName))
      .map((logFields: LogField[]) => logFields.some((logField: LogField) => logField.name === field));
  }

}
