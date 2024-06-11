/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class ApicAccessRightServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor.
   */
  public ApicAccessRightServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_APIC_ACCESS);
  }

  /**
   * Get details of current user access rights.
   *
   * @return ApicAccessRight current access object
   * @throws ApicWebServiceException icdm exception
   */
  public ApicAccessRight getCurrentUserApicAccessRight() throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_APIC_CURRENT_USER);
    return get(wsTarget, ApicAccessRight.class);
  }

  /**
   * Get details of given user access rights.
   * 
   * @param username NT userId
   * @return ApicAccessRight current access object
   * @throws ApicWebServiceException icdm exception
   */
  public ApicAccessRight getUserApicAccessRight(final String username) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_BY_USER_NT_ID)
        .queryParam(WsCommonConstants.RWS_QP_USERNAME, username);
    return get(wsTarget, ApicAccessRight.class);
  }
}
