/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class VcdmAprjServiceClient extends AbstractRestServiceClient {

  /**
  *
  */
  public VcdmAprjServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_VCDM, WsCommonConstants.RWS_APRJ);
  }

  /**
   * @param aprjName APRJ name
   * @return APRJ Id
   * @throws ApicWebServiceException Exception in retrieving APRJ Id
   */
  public String getAprjId(final String aprjName) throws ApicWebServiceException {
    LOGGER.debug("Retrieving APRJ ID from vCDM for APRJ name : {}", aprjName);

    WebTarget wsBase = getWsBase().queryParam(WsCommonConstants.RWS_QP_APRJ_NAME, aprjName);
    String aprjId = get(wsBase, String.class);

    LOGGER.debug("APRJ ID is : {}", aprjId);

    return aprjId;
  }
}
