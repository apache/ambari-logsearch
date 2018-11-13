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

import java.io.Closeable;

/**
 * Client that is responsible to upload files to cloud storage implementation.
 */
public interface UploadClient extends Closeable {

  /**
   * Initialize the client
   * @param logFeederProps global properties holder
   */
  void init(LogFeederProps logFeederProps);

  /**
   * Upload source file to cloud storage location
   * @param source file that will be uploaded
   * @param target file key/output on cloud storage
   * @throws Exception error during upload
   */
  void upload(String source, String target) throws Exception;
}
