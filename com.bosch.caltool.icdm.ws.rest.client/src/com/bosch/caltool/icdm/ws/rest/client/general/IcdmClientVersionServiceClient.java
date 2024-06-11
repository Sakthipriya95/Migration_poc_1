/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author nip4cob
 * @deprecated to be removed after 2021.5.0 release
 */
@Deprecated
public class IcdmClientVersionServiceClient extends AbstractRestServiceClient {


  public IcdmClientVersionServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_ICDM_CLIENT_VERSION);
  }

  /**
   * @return - iCDM Version
   * @throws ApicWebServiceException error during webservice call
   */
  public String getIcdmVersion() throws ApicWebServiceException {
    // gets the iCDM version from DB
    String icdmVersion = get(getWsBase(), String.class);
    LOGGER.debug("iCDM Version is {}", icdmVersion);
    return icdmVersion;
  }
}
