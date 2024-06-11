/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.vcdm;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class VcdmAvailabilityCheckServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public VcdmAvailabilityCheckServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_VCDM, WsCommonConstants.RWS_VCDM_AVAILABILITY);
  }

  /**
   * @return is vcdm super user login successful
   * @throws ApicWebServiceException Exception in retrieving cal data from vCDM
   */
  public boolean isVcdmAvailable() throws ApicWebServiceException {
    boolean available = get(getWsBase(), boolean.class);
    LOGGER.debug("vCDM Services available : {}", available);

    return available;
  }
}
