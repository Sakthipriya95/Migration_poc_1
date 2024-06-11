/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class RvwCommentTemplateServiceClient extends AbstractRestServiceClient {

  /**
   * initialize client constructor with the comment templates
   */
  public RvwCommentTemplateServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_COMMENT_TEMPLATE);
  }

  /**
   * @return the sorted set of rule sets
   * @throws ApicWebServiceException any error while reading file
   */
  public Map<Long, RvwCommentTemplate> getAll() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long,RvwCommentTemplate>> type = new GenericType<Map<Long,RvwCommentTemplate>>() {};
    Map<Long, RvwCommentTemplate> rveCommentTemplateMap = get(wsTarget, type);
    LOGGER.info(
        "RvwCommentTemplateServiceClient.getAll() completed. Rvw Comment Template found = {}",
        rveCommentTemplateMap.size());
    return rveCommentTemplateMap;
  }
}
