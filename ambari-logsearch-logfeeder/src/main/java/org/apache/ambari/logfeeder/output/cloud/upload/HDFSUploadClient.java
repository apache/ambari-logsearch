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
import org.apache.ambari.logfeeder.util.LogFeederHDFSUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * HDFS client that uses core-site.xml file from the classpath to load the configuration.
 * Can connect to S3 / GCS / WASB / ADLS if the core-site.xml is configured to use one of those cloud storages
 */
public class HDFSUploadClient implements UploadClient {

  private static final String FS_DEFAULT_FS = "fs.defaultFS";

  private static final Logger logger = LogManager.getLogger(HDFSUploadClient.class);

  private FileSystem fs;

  @Override
  public void init(LogFeederProps logFeederProps) {
    logger.info("Initialize HDFS client (cloud mode), using core-site.xml from the classpath.");
    Configuration configuration = new Configuration();
    if (StringUtils.isNotBlank(logFeederProps.getCustomFs())) {
      configuration.set(FS_DEFAULT_FS, logFeederProps.getCustomFs());
    }
    if (StringUtils.isNotBlank(logFeederProps.getLogfeederHdfsUser()) && isHadoopFileSystem(configuration)) {
      logger.info("Using HADOOP_USER_NAME: {}", logFeederProps.getLogfeederHdfsUser());
      System.setProperty("HADOOP_USER_NAME", logFeederProps.getLogfeederHdfsUser());
    }
    this.fs = LogFeederHDFSUtil.buildFileSystem(configuration);
  }

  @Override
  public void upload(String source, String target) throws Exception {
    LogFeederHDFSUtil.copyFromLocal(source, target, fs, true, true, null);
  }

  @Override
  public void close() throws IOException {
    LogFeederHDFSUtil.closeFileSystem(fs);
  }

  private boolean isHadoopFileSystem(Configuration conf) {
    return conf.get(FS_DEFAULT_FS).contains("hdfs://");
  }

}
