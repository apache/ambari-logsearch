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

import org.apache.ambari.logfeeder.conf.LogFeederProps;
import org.apache.ambari.logfeeder.plugin.common.MetricData;
import org.apache.ambari.logfeeder.plugin.input.Input;
import org.apache.ambari.logfeeder.plugin.input.InputMarker;
import org.apache.ambari.logfeeder.plugin.manager.OutputManager;
import org.apache.ambari.logfeeder.plugin.output.Output;
import org.apache.ambari.logfeeder.util.LogFeederUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handle output operations for sending cloud inputs to a cloud storage destination
 */
public class CloudStorageOutputManager extends OutputManager {

  private static final Logger logger = LogManager.getLogger(CloudStorageOutputManager.class);

  @Inject
  private LogFeederProps logFeederProps;

  private CloudStorageOutput storageOutput = null;

  private List<Output> outputList = new ArrayList<>();

  @Override
  public void write(Map<String, Object> jsonObj, InputMarker marker) {
    write(LogFeederUtil.getGson().toJson(jsonObj), marker);
  }

  @Override
  public void write(String line, InputMarker marker) {
    try {
      storageOutput.write(line, marker);
    } catch (Exception e) {
      logger.error("Error during cloud output write.", e);
    }
  }

  @Override
  public void copyFile(File file, InputMarker marker) {
  }

  @Override
  public void add(Output output) {
    this.outputList.add(output);
  }

  @Override
  public List<Output> getOutputs() {
    return this.outputList;
  }

  @Override
  public void init() throws Exception {
    logger.info("Called init with cloud storage output manager.");
    storageOutput = new CloudStorageOutput(logFeederProps);
    storageOutput.init(logFeederProps);
    add(storageOutput);
  }

  @Override
  public void close() {
    logger.info("Close called for cloud outputs.");
    storageOutput.stopUploader();
    storageOutput.setDrain(true);
    storageOutput.close();
  }

  @Override
  public void logStats() {
  }

  @Override
  public void addMetricsContainers(List<MetricData> metricsList) {
  }

  @Override
  public void release(Input input) {
    storageOutput.removeWorker(input);
  }
}
