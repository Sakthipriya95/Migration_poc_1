/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.rm.RmMetaData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob Service client to get functions based on search criteria
 */
public class RmMetaDataClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public RmMetaDataClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_RISK_DEFINTION);
  }

  /**
   * Get all functions which matches the search criteria
   *
   * @return Rm Meta data object
   * @throws ApicWebServiceException error during service call
   */
  public RmMetaData getMetaData() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_METADATA);
    GenericType<RmMetaData> type = new GenericType<RmMetaData>() {};
    return get(wsTarget, type);
  }
}
