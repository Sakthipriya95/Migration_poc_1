/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcCreationDetails;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcCreationServiceClient extends AbstractRestServiceClient {

  /**
   * constructor
   */
  public PidcCreationServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_CREATION);
  }

  /**
   * @param projectNameAttr
   * @return
   * @throws ApicWebServiceException
   */
  public PidcCreationDetails getPidcCreationDetails(final int projNameAttrLvl) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PIDC_CREATION_DETAILS)
        .queryParam(WsCommonConstants.RWS_QP_PROJ_NAME_ATTR_LVL, projNameAttrLvl);
    return get(wsTarget, PidcCreationDetails.class);
  }

}
