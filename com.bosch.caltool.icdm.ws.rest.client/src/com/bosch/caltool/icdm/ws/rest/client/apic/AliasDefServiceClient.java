/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Alias Definition
 *
 * @author bne4cob
 */
public class AliasDefServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public AliasDefServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_ALIASDEF);
  }

  /**
   * Get all Alias Definition records in system
   *
   * @return Map. Key - id, Value - AliasDef object
   * @throws ApicWebServiceException exception while invoking service
   */
  public Map<Long, AliasDef> getAll() throws ApicWebServiceException {
    LOGGER.debug("Get all Alias Definition records ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, AliasDef>> type = new GenericType<Map<Long, AliasDef>>() {};
    Map<Long, AliasDef> retMap = get(wsTarget, type);
    LOGGER.debug("Alias Definition records loaded count = {}", retMap.size());
    return retMap;
  }

  /**
   * Get Alias Definition using its id
   *
   * @param objId object's id
   * @return AliasDef object
   * @throws ApicWebServiceException exception while invoking service
   */
  public AliasDef get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, AliasDef.class);
  }

}
