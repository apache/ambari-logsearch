/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ambari.logfeeder.output.cloud;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rolling.AbstractTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.concurrent.TimeUnit;

/**
 * Rolls a file over based on time. - it works with a specific interval, it does not use the file date pattern from log4j2 configuration
 */
@Plugin(name = "CustomTimeBasedTriggeringPolicy", category = Core.CATEGORY_NAME, printObject = true)
public final class CustomTimeBasedTriggeringPolicy extends AbstractTriggeringPolicy {

  private final long intervalMin;

  private RollingFileManager manager;
  private long nextRolloverMillis;

  private CustomTimeBasedTriggeringPolicy(final long intervalMin) {
    this.intervalMin = intervalMin;
  }

  public long getIntervalMin() {
    return this.intervalMin;
  }

  @Override
  public void initialize(RollingFileManager manager) {
    this.manager = manager;
    long fileTime = this.manager.getFileTime();
    long actualDate = System.currentTimeMillis();
    long diff = actualDate - fileTime;
    long intervalMillis = TimeUnit.MINUTES.toMillis(this.intervalMin);
    if (diff > intervalMillis) {
      this.nextRolloverMillis = actualDate;
    } else {
      long remainingMillis = intervalMillis - diff;
      this.nextRolloverMillis = actualDate + remainingMillis;
    }
  }

  @Override
  public boolean isTriggeringEvent(LogEvent event) {
    if (this.manager.getFileSize() == 0L) {
      return false;
    } else {
      long nowMillis = event.getTimeMillis();
      if (nowMillis >= this.nextRolloverMillis) {
        this.nextRolloverMillis = nowMillis + TimeUnit.MINUTES.toMillis(this.intervalMin);
        return true;
      } else {
        return false;
      }
    }
  }

  @PluginFactory
  public static CustomTimeBasedTriggeringPolicy createPolicy(@PluginAttribute("intervalMins") final String intervalMins) {
    return new CustomTimeBasedTriggeringPolicy(Long.parseLong(intervalMins));
  }

}
