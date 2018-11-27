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

package org.apache.ambari.logsearch.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.ambari.logsearch.common.LogSearchContext;
import org.apache.ambari.logsearch.dao.MetadataSolrDao;
import org.apache.ambari.logsearch.model.request.impl.MetadataRequest;
import org.apache.ambari.logsearch.model.response.LogsearchMetaData;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.ConversionService;

import javax.inject.Inject;
import javax.inject.Named;

import static org.apache.ambari.logsearch.solr.SolrConstants.MetadataConstants.ID;
import static org.apache.ambari.logsearch.solr.SolrConstants.MetadataConstants.USER_NAME;
import static org.apache.ambari.logsearch.solr.SolrConstants.MetadataConstants.VALUE;
import static org.apache.ambari.logsearch.solr.SolrConstants.MetadataConstants.NAME;
import static org.apache.ambari.logsearch.solr.SolrConstants.MetadataConstants.TYPE;

@Named
public class MetadataManager extends JsonManagerBase {

  private static final Logger logger = LogManager.getLogger(MetadataManager.class);

  @Inject
  private MetadataSolrDao metadataSolrDao;
  @Inject
  private ConversionService conversionService;

  public String saveMetadata(LogsearchMetaData metadata) {
    String name = metadata.getName();

    SolrInputDocument solrInputDoc = new SolrInputDocument();
    if (!isValid(metadata, false)) {
      throw new MalformedInputException("Name,type and value should be specified");
    }
    final String userName = LogSearchContext.getCurrentUsername().toLowerCase();
    solrInputDoc.addField(ID, generateUniqueId(metadata, userName));
    solrInputDoc.addField(USER_NAME, userName);
    solrInputDoc.addField(VALUE, metadata.getValue());
    solrInputDoc.addField(NAME, name);
    solrInputDoc.addField(TYPE, metadata.getType());

    metadataSolrDao.addDocs(solrInputDoc);
    return convertObjToString(solrInputDoc);
  }

  public void deleteMetadata(LogsearchMetaData metaData) {
    if (!isValid(metaData, true)) {
      throw new MalformedInputException("Name and type should be specified");
    }
    metadataSolrDao.deleteMetadata(metaData.getName(), metaData.getType());
  }

  @SuppressWarnings("unchecked")
  public Collection<LogsearchMetaData> getMetadata(MetadataRequest request) {
    SolrQuery metadataQueryQuery = conversionService.convert(request, SolrQuery.class);

    final String userName;
    if (StringUtils.isNotBlank(request.getUserName())) {
      userName = request.getUserName();
    } else {
      userName = LogSearchContext.getCurrentUsername();
    }
    if (StringUtils.isBlank(userName)) {
      throw new IllegalArgumentException("User name is not found for metadata request.");
    }
    metadataQueryQuery.addFilterQuery(String.format("%s:%s", USER_NAME, userName.toLowerCase()));
    SolrDocumentList solrList = metadataSolrDao.process(metadataQueryQuery).getResults();

    Collection<LogsearchMetaData> metadataList = new ArrayList<>();

    for (SolrDocument solrDoc : solrList) {
      LogsearchMetaData metadata = new LogsearchMetaData();
      metadata.setName(solrDoc.get(NAME).toString());
      metadata.setId(solrDoc.get(ID).toString());
      metadata.setValue(solrDoc.getOrDefault(VALUE, "").toString());
      metadata.setType(solrDoc.get(TYPE).toString());
      metadata.setUserName(solrDoc.get(USER_NAME).toString());

      metadataList.add(metadata);
    }

    return metadataList;

  }

  private String generateUniqueId(LogsearchMetaData metaData, String userName) {
    String strToUUID = metaData.getName() +
      metaData.getType() +
      userName;
    return UUID.nameUUIDFromBytes(strToUUID.getBytes()).toString();
  }

  private boolean isValid(LogsearchMetaData mData, boolean skipValueCheck) {
    return StringUtils.isNotBlank(mData.getName())
      && StringUtils.isNotBlank(mData.getType())
      && (skipValueCheck || StringUtils.isNotBlank(mData.getValue()));
  }
}
