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

package org.apache.ambari.logsearch.config.api;

import java.util.List;

import org.apache.ambari.logsearch.config.api.model.inputconfig.InputConfig;

/**
 * Monitors input configuration changes.
 */
public interface InputConfigMonitor {
  /**
   * @return A list of json strings for all the global config jsons.
   */
  List<String> getGlobalConfigJsons();
  
  /**
   * Notification of a new input configuration.
   * 
   * @param serviceName The name of the service for which the input configuration was created.
   * @param inputConfig The input configuration.
   * @throws Exception error during loading the input configurations
   */
  void loadInputConfigs(String serviceName, InputConfig inputConfig) throws Exception;
  
  /**
   * Notification of the removal of an input configuration.
   * 
   * @param serviceName The name of the service of which's input configuration was removed.
   */
  void removeInputs(String serviceName);
}
