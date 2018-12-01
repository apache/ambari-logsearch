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

import { Component } from '@angular/core';
import * as $ from 'jquery';
import * as moment from 'moment-timezone';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { Store } from '@ngrx/store';
import { AppStore } from '@app/classes/models/store';
import { selectTimeZone } from '@app/store/selectors/user-settings.selectors';
import { SetUserSettingsAction } from '@app/store/actions/user-settings.actions';

import '@vendor/js/WorldMapGenerator.min';
import {ServerSettingsService} from '@app/services/server-settings.service';

@Component({
  selector: 'timezone-picker',
  templateUrl: './timezone-picker.component.html',
  styleUrls: ['./timezone-picker.component.less']
})
export class TimeZonePickerComponent {

  timeZone$: Observable<string> = this.store.select(selectTimeZone).startWith(moment.tz.guess());

  destroyed$: Subject<boolean> = new Subject();

  constructor(
    private store: Store<AppStore>,
    private settingsService: ServerSettingsService
  ) {
  }

  readonly mapElementId = 'timezone-map';

  private readonly mapOptions = {
    quickLink: [
      {
        PST: 'PST',
        MST: 'MST',
        CST: 'CST',
        EST: 'EST',
        GMT: 'GMT',
        LONDON: 'Europe/London',
        IST: 'IST'
      }
    ]
  };

  private mapElement: any;

  private timeZoneSelect: JQuery;

  isTimeZonePickerDisplayed: boolean = false;

  setTimeZonePickerDisplay(isDisplayed: boolean): void {
    this.isTimeZonePickerDisplayed = isDisplayed;
  }

  initMap(): void {
    this.mapElement = $(`#${this.mapElementId}`);
    this.mapElement.WorldMapGenerator(this.mapOptions);
    this.timeZoneSelect = this.mapElement.find('select');
    this.timeZoneSelect.removeClass('btn btn-default').addClass('form-control');
    this.timeZone$.take(1).subscribe((timeZoneSetting) => {
      this.timeZoneSelect.val(timeZoneSetting);
    });
  }

  setTimeZone(): void {
    const timeZone = this.timeZoneSelect.val();
    this.store.dispatch(new SetUserSettingsAction({
      timeZone
    }) );
    this.setTimeZonePickerDisplay(false);
  }

}
