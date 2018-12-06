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
import {
  Component,
  Input,
  Output,
  AfterViewInit,
  EventEmitter,
  forwardRef,
  OnChanges,
  SimpleChanges
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

import * as moment from 'moment-timezone';
import * as $ from 'jquery';

@Component({
  selector: 'time-zone-map-input',
  templateUrl: './time-zone-map-input.component.html',
  styleUrls: ['./time-zone-map-input.component.less'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TimeZoneMapInputComponent),
      multi: true
    }
  ]
})
export class TimeZoneMapInputComponent implements AfterViewInit, ControlValueAccessor, OnChanges {

  @Input()
  value = moment.tz.guess();
  
  @Input()
  mapElementId = 'timeZoneMap';
  
  @Input()
  mapOptions = {
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

  @Output()
  onChange = new EventEmitter();

  controlValueAccessorOnchange;
  
  constructor() { }

  ngAfterViewInit() {
    this.initMap();
  }

  ngOnChanges(change: SimpleChanges) {
    if (change.value) {
      this.setTimeZoneSelection(this.value);
    }
  }

  getMapElement() {
    return $(`#${this.mapElementId}`);
  }

  getSelectElement() {
    return this.getMapElement().find('select');
  }

  initMap() {
    const mapElement: any = this.getMapElement();
    let timeZoneSelect;
    let lastValue;
    mapElement.WorldMapGenerator(this.mapOptions);
    mapElement.on('click', (event) => {
      const currentValue = timeZoneSelect && timeZoneSelect.val();
      if (currentValue !== lastValue) {
        this.onChange.emit(currentValue);
        if (this.controlValueAccessorOnchange) {
          this.controlValueAccessorOnchange(currentValue);
        }
      }
    });
    timeZoneSelect = this.getSelectElement();
    timeZoneSelect.removeClass('btn btn-default')
      .addClass('form-control')
      .val(this.value || moment.tz.guess());
  }

  setTimeZoneSelection(timeZone) {
    this.getSelectElement().val(timeZone);
    this.getMapElement().find('[data-selected=true]').attr('data-selected', 'false');
    this.getMapElement().find(`[data-timezone="${timeZone}"]`).attr('data-selected', 'true');
  }

  writeValue(value: any): void {
    this.value = value;
    this.setTimeZoneSelection(this.value);
  }

  registerOnChange(fn: any): void {
    this.controlValueAccessorOnchange = fn;
  }

  registerOnTouched(fn: any): void {

  }

}
