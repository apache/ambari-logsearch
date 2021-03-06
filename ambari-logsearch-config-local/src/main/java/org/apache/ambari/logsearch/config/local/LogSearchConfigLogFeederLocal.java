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
package org.apache.ambari.logsearch.config.local;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.ambari.logsearch.config.api.InputConfigMonitor;
import org.apache.ambari.logsearch.config.api.LogLevelFilterMonitor;
import org.apache.ambari.logsearch.config.api.LogSearchConfigLogFeeder;
import org.apache.ambari.logsearch.config.json.JsonHelper;
import org.apache.ambari.logsearch.config.json.model.inputconfig.impl.InputConfigGson;
import org.apache.ambari.logsearch.config.json.model.inputconfig.impl.InputConfigImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Handle input.config-*.json files from local sourse (filesystem)
 * After the first file check in the configuration folder, it starts to watch the specified about changes (create/update/delete files)
 */
public class LogSearchConfigLogFeederLocal extends LogSearchConfigLocal implements LogSearchConfigLogFeeder {

  private static final Logger logger = LogManager.getLogger(LogSearchConfigLogFeederLocal.class);

  private String configDir;

  @Override
  public void init(Map<String, String> properties, String clusterName) throws Exception {
    super.init(properties);
    setConfigDir(properties.getOrDefault("logfeeder.config.dir", "/usr/lib/ambari-logsearch-logfeeder/conf"));
    boolean localConfig = Boolean.valueOf(properties.getOrDefault("logfeeder.config.filter.local", "false"));
    if (localConfig) {
      setLogLevelFilterManager(new LogLevelFilterManagerLocal(getConfigDir(), gson));
    }
  }

  @Override
  public boolean inputConfigExists(String serviceName) throws Exception {
    Path filePath = Paths.get(getConfigDir(), String.format("input.config-%s.json", serviceName.toLowerCase()));
    return inputFileContentsMap.containsKey(filePath.toAbsolutePath().toString());
  }

  @Override
  public void monitorInputConfigChanges(final InputConfigMonitor inputConfigMonitor, final LogLevelFilterMonitor logLevelFilterMonitor, String clusterName) throws Exception {
    final JsonParser parser = new JsonParser();
    final JsonArray globalConfigNode = new JsonArray();
    for (String globalConfigJsonString : inputConfigMonitor.getGlobalConfigJsons()) {
      JsonElement globalConfigJson = parser.parse(globalConfigJsonString);
      globalConfigNode.add(globalConfigJson.getAsJsonObject().get("global"));
      Path filePath = Paths.get(configDir, "global.config.json");
      String strData = InputConfigGson.gson.toJson(globalConfigJson);
      byte[] data = strData.getBytes(StandardCharsets.UTF_8);
      Files.write(filePath, data);
    }

    File[] inputConfigFiles = new File(configDir).listFiles(inputConfigFileFilter);
    if (inputConfigFiles != null) {
      for (File inputConfigFile : inputConfigFiles) {
        tryLoadingInputConfig(inputConfigMonitor, parser, globalConfigNode, inputConfigFile);
      }
    }
    final FileSystem fs = FileSystems.getDefault();
    final WatchService ws = fs.newWatchService();
    Path configPath = Paths.get(configDir);
    LogSearchConfigLocalUpdater updater = new LogSearchConfigLocalUpdater(configPath, ws, inputConfigMonitor, inputFileContentsMap,
      parser, globalConfigNode, serviceNamePattern);
    executorService.submit(updater);
  }

  private void tryLoadingInputConfig(InputConfigMonitor inputConfigMonitor, JsonParser parser, JsonArray globalConfigNode, File inputConfigFile) throws Exception {
    // note: that will try to solve a intermittent issue when the input config json is a null string (during file generation), that process will re-try to process the files a few times
    int tries = 0;
    while(true) {
      tries++;
      Matcher m = serviceNamePattern.matcher(inputConfigFile.getName());
      m.find();
      String inputConfig = new String(Files.readAllBytes(inputConfigFile.toPath()));
      String serviceName = m.group(1);
      JsonElement inputConfigJson = null;
      logger.info("Trying to load '{}' service input config from input file '{}'", serviceName, inputConfigFile.getAbsolutePath());
      try {
        inputConfigJson = JsonHelper.mergeGlobalConfigWithInputConfig(parser, inputConfig, globalConfigNode);
      } catch (Exception e) {
        final String errorMessage;
        if (tries < 3) {
          errorMessage = String.format("Cannot parse input config: '%s', will retry in a few seconds again (tries: %s)", inputConfig, String.valueOf(tries));
          logger.error(errorMessage, e);
          try {
            Thread.sleep(2000);
          } catch (Exception ex) {
            // skip
          }
          continue;
        } else {
          errorMessage = String.format("Cannot parse input config: %s, after %s tries. Will skip to processing it", inputConfig, String.valueOf(tries));
          logger.error(errorMessage, e);
          break;
        }
      }
      inputConfigMonitor.loadInputConfigs(serviceName, InputConfigGson.gson.fromJson(inputConfigJson, InputConfigImpl.class));
      break;
    }
  }

  @Override
  public void close() throws IOException {
  }

  @Override
  public String getConfigDir() {
    return this.configDir;
  }

  @Override
  public void setConfigDir(String configDir) {
    this.configDir = configDir;
  }
}
