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
package org.apache.ambari.logfeeder.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;

/**
 * Factory for creating specific Solr clients based on provided configurations (simple / LB or cloud Solr client)
 */
public class LogFeederSolrClientFactory {

  private static final Logger logger = LogManager.getLogger(LogFeederSolrClientFactory.class);

  /**
   * Creates a new Solr client. If solr urls are provided create a LB client (Use simple Http client if only 1 provided),
   * otherwise create a cloud client. That means at least providing zookeeper connection string or Solr urls are required.
   * @param zkConnectionString zookeeper connection string, e.g.: localhost1:2181,localhost2:2181/solr
   * @param solrUrls list of solr urls
   * @param collection name of the Solr collection
   * @return created client
   */
  public SolrClient createSolrClient(String zkConnectionString, String[] solrUrls, String collection) {
    logger.info("Creating solr client ...");
    logger.info("Using collection=" + collection);
    if (solrUrls != null && solrUrls.length > 0) {
      logger.info("Using lbHttpSolrClient with urls: {}",
        StringUtils.join(appendTo("/" + collection, solrUrls), ","));
      LBHttpSolrClient.Builder builder = new LBHttpSolrClient.Builder();
      builder.withBaseSolrUrls(solrUrls);
      return builder.build();
    } else {
      logger.info("Using zookeepr. zkConnectString=" + zkConnectionString);
      CloudSolrClient.Builder builder = new CloudSolrClient.Builder();
      builder.withZkHost(zkConnectionString);
      CloudSolrClient solrClient = builder.build();
      solrClient.setDefaultCollection(collection);
      return solrClient;
    }
  }

  private String[] appendTo(String toAppend, String... appendees) {
    for (int i = 0; i < appendees.length; i++) {
      appendees[i] = appendees[i] + toAppend;
    }
    return appendees;
  }

}
