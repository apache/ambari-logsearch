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
package org.apache.ambari.logfeeder.conf.output;

import org.apache.ambari.logfeeder.common.LogFeederConstants;
import org.apache.ambari.logsearch.config.api.LogSearchPropertyDescription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HdfsOutputConfig implements OutputConfig {

  @LogSearchPropertyDescription(
    name = LogFeederConstants.HDFS_HOST,
    description = "HDFS Name Node host.",
    examples = {"mynamenodehost"},
    sources = {LogFeederConstants.LOGFEEDER_PROPERTIES_FILE}
  )
  @Value("${"+ LogFeederConstants.HDFS_HOST + ":}")
  private String hdfsHost;

  @LogSearchPropertyDescription(
    name = LogFeederConstants.HDFS_PORT,
    description = "HDFS Name Node port",
    examples = {"9000"},
    sources = {LogFeederConstants.LOGFEEDER_PROPERTIES_FILE}
  )
  @Value("${"+ LogFeederConstants.HDFS_PORT + ":}")
  private Integer hdfsPort;

  @LogSearchPropertyDescription(
    name = LogFeederConstants.HDFS_FILE_PERMISSIONS,
    description = "Default permissions for created files on HDFS",
    examples = {"600"},
    defaultValue = "640",
    sources = {LogFeederConstants.LOGFEEDER_PROPERTIES_FILE}
  )
  @Value("${"+ LogFeederConstants.HDFS_FILE_PERMISSIONS + ":640}")
  private String hdfsFilePermissions;

  @LogSearchPropertyDescription(
    name = LogFeederConstants.HDFS_USER,
    description = "Overrides HADOOP_USER_NAME variable at runtime",
    examples = {"hdfs"},
    sources = {LogFeederConstants.LOGFEEDER_PROPERTIES_FILE}
  )
  @Value("${"+ LogFeederConstants.HDFS_USER + ":}")
  private String hdfsUser;

  @LogSearchPropertyDescription(
    name = LogFeederConstants.HDFS_OUTPUT_BASE_DIR,
    description = "HDFS base directory for uploading files",
    examples = {"/my/path/on/hdfs"},
    defaultValue = "/user/hdfs",
    sources = {LogFeederConstants.LOGFEEDER_PROPERTIES_FILE}
  )
  @Value("${"+ LogFeederConstants.HDFS_OUTPUT_BASE_DIR + ":/user/hdfs}")
  private String hdfsOutputDir;

  @LogSearchPropertyDescription(
    name = LogFeederConstants.HDFS_KERBEROS,
    description = "Enable kerberos support for HDFS",
    examples = {"true"},
    defaultValue = "false",
    sources = {LogFeederConstants.LOGFEEDER_PROPERTIES_FILE}
  )
  @Value("${"+ LogFeederConstants.HDFS_KERBEROS + ":false}")
  private boolean secure;

  public String getHdfsHost() {
    return hdfsHost;
  }

  public void setHdfsHost(String hdfsHost) {
    this.hdfsHost = hdfsHost;
  }

  public Integer getHdfsPort() {
    return hdfsPort;
  }

  public void setHdfsPort(Integer hdfsPort) {
    this.hdfsPort = hdfsPort;
  }

  public String getHdfsFilePermissions() {
    return hdfsFilePermissions;
  }

  public void setHdfsFilePermissions(String hdfsFilePermissions) {
    this.hdfsFilePermissions = hdfsFilePermissions;
  }

  public String getHdfsUser() {
    return hdfsUser;
  }

  public void setHdfsUser(String hdfsUser) {
    this.hdfsUser = hdfsUser;
  }

  public String getHdfsOutputDir() {
    return hdfsOutputDir;
  }

  public void setHdfsOutputDir(String hdfsOutputDir) {
    this.hdfsOutputDir = hdfsOutputDir;
  }

  public boolean isSecure() {
    return secure;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  @Override
  public String getOutputBasePath() {
    return this.hdfsOutputDir;
  }
}
