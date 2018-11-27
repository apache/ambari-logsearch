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
import java.util.List;

import org.apache.ambari.logsearch.common.LogSearchContext;
import org.apache.ambari.logsearch.dao.MetadataSolrDao;
import org.apache.ambari.logsearch.model.request.impl.MetadataRequest;
import org.apache.ambari.logsearch.model.response.LogsearchMetaData;
import org.apache.ambari.logsearch.model.response.LogSearchMetaDataListResponse;
import org.apache.ambari.logsearch.util.SolrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.core.convert.ConversionService;

import javax.inject.Inject;
import javax.inject.Named;

import static org.apache.ambari.logsearch.solr.SolrConstants.EventHistoryConstants.ID;
import static org.apache.ambari.logsearch.solr.SolrConstants.EventHistoryConstants.USER_NAME;
import static org.apache.ambari.logsearch.solr.SolrConstants.EventHistoryConstants.VALUES;
import static org.apache.ambari.logsearch.solr.SolrConstants.EventHistoryConstants.FILTER_NAME;
import static org.apache.ambari.logsearch.solr.SolrConstants.EventHistoryConstants.ROW_TYPE;
import static org.apache.ambari.logsearch.solr.SolrConstants.EventHistoryConstants.SHARE_NAME_LIST;

@Named
public class MetadataManager extends JsonManagerBase {

  private static final Logger logger = LogManager.getLogger(MetadataManager.class);

  @Inject
  private MetadataSolrDao metadataSolrDao;
  @Inject
  private ConversionService conversionService;

  public String saveMetadata(LogsearchMetaData metadata) {
    String filterName = metadata.getFiltername();

    SolrInputDocument solrInputDoc = new SolrInputDocument();
    if (!isValid(metadata)) {
      throw new MalformedInputException("No FilterName Specified");
    }

    if (isNotUnique(filterName)) {
      throw new AlreadyExistsException(String.format("Name '%s' already exists", metadata.getFiltername()));
    }
    solrInputDoc.addField(ID, metadata.getId());
    solrInputDoc.addField(USER_NAME, LogSearchContext.getCurrentUsername());
    solrInputDoc.addField(VALUES, metadata.getValues());
    solrInputDoc.addField(FILTER_NAME, filterName);
    solrInputDoc.addField(ROW_TYPE, metadata.getRowType());
    List<String> shareNameList = metadata.getShareNameList();
    if (CollectionUtils.isNotEmpty(shareNameList)) {
      solrInputDoc.addField(SHARE_NAME_LIST, shareNameList);
    }

    metadataSolrDao.addDocs(solrInputDoc);
    return convertObjToString(solrInputDoc);
  }

  private boolean isNotUnique(String filterName) {

    if (filterName != null) {
      SolrQuery solrQuery = new SolrQuery();
      filterName = SolrUtil.makeSearcableString(filterName);
      solrQuery.setQuery("*:*");
      solrQuery.addFilterQuery(FILTER_NAME + ":" + filterName);
      solrQuery.addFilterQuery(USER_NAME + ":" + LogSearchContext.getCurrentUsername());
      SolrUtil.setRowCount(solrQuery, 0);
      try {
        Long numFound = metadataSolrDao.process(solrQuery).getResults().getNumFound();
        if (numFound > 0) {
          return true;
        }
      } catch (SolrException e) {
        logger.error("Error while checking if metadata is unique.", e);
      }
    }
    return false;
  }

  private boolean isValid(LogsearchMetaData mData) {
    return StringUtils.isNotBlank(mData.getFiltername())
        && StringUtils.isNotBlank(mData.getRowType())
        && StringUtils.isNotBlank(mData.getValues());
  }

  public void deleteMetadata(String id) {
    metadataSolrDao.deleteEventHistoryData(id);
  }

  @SuppressWarnings("unchecked")
  public LogSearchMetaDataListResponse getMetadata(MetadataRequest request) {
    LogSearchMetaDataListResponse response = new LogSearchMetaDataListResponse();
    String rowType = request.getRowType();
    if (StringUtils.isBlank(rowType)) {
      throw new MalformedInputException("Row type was not specified");
    }

    SolrQuery metadataQueryQuery = conversionService.convert(request, SolrQuery.class);
    metadataQueryQuery.addFilterQuery(String.format("%s:%s OR %s:%s", USER_NAME, LogSearchContext.getCurrentUsername(),
        SHARE_NAME_LIST, LogSearchContext.getCurrentUsername()));
    SolrDocumentList solrList = metadataSolrDao.process(metadataQueryQuery).getResults();

    Collection<LogsearchMetaData> configList = new ArrayList<>();

    for (SolrDocument solrDoc : solrList) {
      LogsearchMetaData metadata = new LogsearchMetaData();
      metadata.setFiltername("" + solrDoc.get(FILTER_NAME));
      metadata.setId("" + solrDoc.get(ID));
      metadata.setValues("" + solrDoc.get(VALUES));
      metadata.setRowType("" + solrDoc.get(ROW_TYPE));
      try {
        List<String> shareNameList = (List<String>) solrDoc.get(SHARE_NAME_LIST);
        metadata.setShareNameList(shareNameList);
      } catch (Exception e) {
        // do nothing
      }

      metadata.setUserName("" + solrDoc.get(USER_NAME));

      configList.add(metadata);
    }

    response.setName("metadataList");
    response.setMetadataList(configList);

    response.setStartIndex(Integer.parseInt(request.getStartIndex()));
    response.setPageSize(Integer.parseInt(request.getPageSize()));

    response.setTotalCount(solrList.getNumFound());

    return response;

  }

  public List<String> getAllUserName() {
    List<String> userList = new ArrayList<>();
    SolrQuery userListQuery = new SolrQuery();
    userListQuery.setQuery("*:*");
    SolrUtil.setFacetField(userListQuery, USER_NAME);
    QueryResponse queryResponse = metadataSolrDao.process(userListQuery);
    if (queryResponse == null) {
      return userList;
    }
    List<Count> counList = queryResponse.getFacetField(USER_NAME).getValues();
    for (Count cnt : counList) {
      String userName = cnt.getName();
      userList.add(userName);
    }
    return userList;
  }
}
