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
package org.apache.ambari.logfeeder.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogFeederHDFSUtil {
  private static final Logger logger = LogManager.getLogger(LogFeederHDFSUtil.class);

  private LogFeederHDFSUtil() {
    throw new UnsupportedOperationException();
  }
  
  public static boolean copyFromLocal(String sourceFilepath, String destFilePath, FileSystem fileSystem, boolean overwrite,
                                      boolean delSrc, FsPermission fsPermission) {
    Path src = new Path(sourceFilepath);
    Path dst = new Path(destFilePath);
    boolean isCopied = false;
    try {
      logger.info("copying localfile := " + sourceFilepath + " to hdfsPath := " + destFilePath);
      fileSystem.copyFromLocalFile(delSrc, overwrite, src, dst);
      isCopied = true;
      if (fsPermission != null) {
        fileSystem.setPermission(dst, fsPermission);
      }
    } catch (Exception e) {
      logger.error("Error copying local file :" + sourceFilepath + " to hdfs location : " + destFilePath, e);
    }
    return isCopied;
  }

  public static FileSystem buildFileSystem(String hdfsHost, String hdfsPort) {
    return buildFileSystem(hdfsHost, hdfsPort, "hdfs");
  }

  public static FileSystem buildFileSystem(String hdfsHost, String hdfsPort, String scheme) {
    Configuration configuration = buildHdfsConfiguration(hdfsHost, hdfsPort, scheme);
    return buildFileSystem(configuration);
  }

  public static FileSystem buildFileSystem(Configuration configuration) {
    try {
      return FileSystem.get(configuration);
    } catch (Exception e) {
      logger.error("Exception during buildFileSystem call:", e);
    }
    return null;
  }

  public static Configuration buildHdfsConfiguration(String hdfsHost, String hdfsPort, String scheme) {
    return buildHdfsConfiguration(String.format("%s:%s", hdfsHost, hdfsPort), scheme);
  }

  public static Configuration buildHdfsConfiguration(String address, String scheme) {
    String url = String.format("%s://%s/", scheme, address);
    Configuration configuration = new Configuration();
    configuration.set("fs.defaultFS", url);
    return configuration;
  }

  public static void closeFileSystem(FileSystem fileSystem) {
    if (fileSystem != null) {
      try {
        fileSystem.close();
      } catch (IOException e) {
        logger.error(e.getLocalizedMessage(), e.getCause());
      }
    }
  }
}