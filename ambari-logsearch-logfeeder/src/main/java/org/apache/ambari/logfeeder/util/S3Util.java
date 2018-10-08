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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.apache.ambari.logfeeder.common.LogFeederConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility to connect to s3
 */
public class S3Util {
  private static final Logger logger = LogManager.getLogger(S3Util.class);

  private S3Util() {
    throw new UnsupportedOperationException();
  }
  
  public static MinioClient getS3Client(String endpoint, String accessKey, String secretKey) throws InvalidPortException, InvalidEndpointException {
    return new MinioClient(endpoint, accessKey, secretKey);
  }

  public static String getBucketName(String s3Path) {
    String bucketName = null;
    // s3path
    if (s3Path != null) {
      String[] s3PathParts = s3Path.replace(LogFeederConstants.S3_PATH_START_WITH, "").split(LogFeederConstants.S3_PATH_SEPARATOR);
      bucketName = s3PathParts[0];
    }
    return bucketName;
  }

  public static String getS3Key(String s3Path) {
    StringBuilder s3Key = new StringBuilder();
    if (s3Path != null) {
      String[] s3PathParts = s3Path.replace(LogFeederConstants.S3_PATH_START_WITH, "").split(LogFeederConstants.S3_PATH_SEPARATOR);
      ArrayList<String> s3PathList = new ArrayList<>(Arrays.asList(s3PathParts));
      s3PathList.remove(0);// remove bucketName
      for (int index = 0; index < s3PathList.size(); index++) {
        if (index > 0) {
          s3Key.append(LogFeederConstants.S3_PATH_SEPARATOR);
        }
        s3Key.append(s3PathList.get(index));
      }
    }
    return s3Key.toString();
  }

  /**
   * Get the buffer reader to read s3 file as a stream
   * @param s3Path s3 specific path
   * @param s3Endpoint url of an s3 server
   * @param accessKey s3 access key - pass by an input shipper configuration
   * @param secretKey s3 secret key - pass by an input shipper configuration
   * @return buffered reader object which can be used to read s3 file object
   * @throws Exception error that happens during reading s3 file
   */
  public static BufferedReader getReader(String s3Path, String s3Endpoint, String accessKey, String secretKey) throws Exception {
    // TODO error handling
    // Compression support
    // read header and decide the compression(auto detection)
    // For now hard-code GZIP compression
    String s3Bucket = getBucketName(s3Path);
    String s3Key = getS3Key(s3Path);
    GZIPInputStream objectInputStream = null;
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;
    try {
      MinioClient s3Client = getS3Client(s3Endpoint, accessKey, secretKey);
      s3Client.statObject(s3Bucket, s3Key);
      objectInputStream = new GZIPInputStream(s3Client.getObject(s3Bucket, s3Key));
      inputStreamReader = new InputStreamReader(objectInputStream);
      bufferedReader = new BufferedReader(inputStreamReader);
      return bufferedReader;
    } catch (Exception e) {
      logger.error("Error in creating stream reader for s3 file :" + s3Path, e.getCause());
      throw e;
    } finally {
      try {
        if (inputStreamReader != null) {
          inputStreamReader.close();
        }
        if (bufferedReader != null) {
          bufferedReader.close();
        }
        if (objectInputStream != null) {
          objectInputStream.close();
        }
      } catch (Exception e) {
        // do nothing
      }
    }
  }

  public static void writeFileIntoS3File(String filename, String bucketName, String s3Key, String endpoint, String accessKey, String secretKey) {
    try {
      MinioClient s3Client = getS3Client(endpoint, accessKey, secretKey);
      s3Client.putObject(bucketName, s3Key, filename);
    } catch (Exception e) {
      logger.error("Could not write file to s3", e);
    }
  }

  public static void writeDataIntoS3File(String data, String bucketName, String s3Key, String endpoint, String accessKey, String secretKey) {
    try (ByteArrayInputStream bai = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8))) {
      MinioClient s3Client = getS3Client(endpoint, accessKey, secretKey);
      s3Client.putObject(bucketName, s3Key, bai, bai.available(), "application/octet-stream");
    } catch (Exception e) {
      logger.error("Could not write data to s3", e);
    }
  }
}
