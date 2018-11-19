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
import org.apache.ambari.logfeeder.output.cloud.upload.UploadClient;
import org.apache.ambari.logfeeder.util.LogFeederUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Periodically checks a folder (contains archived logs) and if it finds any .log or .gz files, it will try to upload them to cloud storage by an upload client (cloud specific)
 */
public class CloudStorageUploader extends Thread {

  private static final Logger logger = LogManager.getLogger(CloudStorageUploader.class);

  private final UploadClient uploadClient;
  private final LogFeederProps logFeederProps;
  private final String clusterName;
  private final String hostName;
  private final String uploaderType;

  public CloudStorageUploader(String name, UploadClient uploadClient, LogFeederProps logFeederProps) {
    super(name);
    this.uploadClient = uploadClient;
    this.logFeederProps = logFeederProps;
    this.uploaderType = logFeederProps.getCloudStorageDestination().getText();
    this.clusterName = logFeederProps.getClusterName();
    this.hostName = LogFeederUtil.hostName;
  }

  @Override
  public void run() {
    logger.info("Start '{}' uploader", uploaderType);
    boolean stop = false;
    do {
      try {
        try {
          doUpload();
        } catch (Exception e) {
          logger.error("An error occurred during Uploader operation - " + uploaderType, e);
        }
        Thread.sleep(1000 * logFeederProps.getCloudStorageUploaderIntervalSeconds());
      } catch (InterruptedException ie) {
        logger.info("Uploader ({}) thread interrupted", uploaderType);
        stop = true;
      }
    } while (!stop && !Thread.currentThread().isInterrupted());
  }

  /**
   * Finds .log and .gz files and upload them to cloud storage by an uploader client
   */
  void doUpload() {
    try {
      final File archiveLogDir = Paths.get(logFeederProps.getRolloverConfig().getRolloverArchiveBaseDir(),
        uploaderType, clusterName, hostName, "archived").toFile();
      if (archiveLogDir.exists()) {
        String[] extensions = {"log", "json", "gz"};
        Collection<File> filesToUpload = FileUtils.listFiles(archiveLogDir, extensions, true);
        if (filesToUpload.isEmpty()) {
          logger.debug("Not found any files to upload.");
        } else {
          for (File file : filesToUpload) {
            String basePath = logFeederProps.getCloudBasePath();
            String outputPath = String.format("%s/%s/%s/%s/%s", basePath, clusterName, hostName, file.getParentFile().getName(), file.getName())
              .replaceAll("//", "/");
            logger.info("Upload will start: input: {}, output: {}", file.getAbsolutePath(), outputPath);
            uploadClient.upload(file.getAbsolutePath(), outputPath);
          }
        }
      } else {
        logger.debug("Directory {} does not exist.", archiveLogDir);
      }
    } catch (Exception e) {
      logger.error("Exception during cloud upload", e);
    }
  }

}
