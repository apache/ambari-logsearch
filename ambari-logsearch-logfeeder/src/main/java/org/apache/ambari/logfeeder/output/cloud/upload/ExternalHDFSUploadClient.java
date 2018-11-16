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
package org.apache.ambari.logfeeder.output.cloud.upload;

import org.apache.ambari.logfeeder.conf.LogFeederProps;
import org.apache.ambari.logfeeder.conf.output.ExternalHdfsOutputConfig;
import org.apache.ambari.logfeeder.util.LogFeederHDFSUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HDFS (on-prem) specific uploader client that can work with an external HDFS.
 */
public class ExternalHDFSUploadClient implements UploadClient {

  private static final Logger logger = LogManager.getLogger(ExternalHDFSUploadClient.class);

  private final ExternalHdfsOutputConfig hdfsOutputConfig;
  private final FsPermission fsPermission;
  private FileSystem fs;

  public ExternalHDFSUploadClient(ExternalHdfsOutputConfig hdfsOutputConfig) {
    this.hdfsOutputConfig = hdfsOutputConfig;
    this.fsPermission = new FsPermission(hdfsOutputConfig.getHdfsFilePermissions());
  }

  @Override
  public void init(LogFeederProps logFeederProps) {
    logger.info("Initialize external HDFS client ...");
    if (StringUtils.isNotBlank(logFeederProps.getLogfeederHdfsUser())) {
      logger.info("Using HADOOP_USER_NAME: {}", logFeederProps.getLogfeederHdfsUser());
      System.setProperty("HADOOP_USER_NAME", logFeederProps.getLogfeederHdfsUser());
    }
    this.fs = LogFeederHDFSUtil.buildFileSystem(
      hdfsOutputConfig.getHdfsHost(),
      String.valueOf(hdfsOutputConfig.getHdfsPort()));
    if (logFeederProps.getHdfsOutputConfig().isSecure()) {
      logger.info("Kerberos is enabled for external HDFS.");
      Configuration conf = fs.getConf();
      conf.set("hadoop.security.authentication", "kerberos");
    }
  }

  @Override
  public void upload(String source, String target) throws Exception {
    LogFeederHDFSUtil.copyFromLocal(source, target, fs, true, true, fsPermission);
  }

  @Override
  public void close() {
    LogFeederHDFSUtil.closeFileSystem(fs);
  }
}
