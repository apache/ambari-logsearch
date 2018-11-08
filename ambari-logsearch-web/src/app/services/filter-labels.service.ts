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
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { LogsType } from '@app/classes/string';

import * as serviceLogsFieldSelectors from '@app/store/selectors/service-logs-fields.selectors';
import * as auditLogsFieldSelectors from '@app/store/selectors/audit-logs-fields.selectors';
import { Store } from '@ngrx/store';
import { AppStore } from '@app/classes/models/store';

@Injectable()
export class FilterLabelsService {

  constructor(
    private store: Store<AppStore>
  ) { }

  getFieldLabel(fieldName: string, logsType: LogsType, componentName?: string): Observable<string> {
    if (logsType === 'serviceLogs') {
      return this.store.select(serviceLogsFieldSelectors.createServiceLogsFieldLabelSelectorByFieldName(fieldName));
    } else if (logsType === 'auditLogs') {
      return this.store.select(auditLogsFieldSelectors.createAuditLogsFieldLabelSelectorByComponentNameAndFieldName(
        fieldName,
        componentName
      ));
    }
    throw new Error(`Logs Type not supported ${logsType}.`);
  }

  getValueLabel(value: string, fieldName: string, logsType: LogsType): Observable<string> {
    return Observable.of('');
  }

  getActionLabel(actionType: string): Observable<string> {
    return Observable.of('');
  }

  getQueryTypeLabel(isExclude: boolean): Observable<string> {
    return Observable.of('');
  }

}
