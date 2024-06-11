/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l.compli;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.CompliParamOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class CompliParamServiceClient extends AbstractRestServiceClient {

  /**
   * Instantiates a new compli param service client.
   */
  public CompliParamServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_CONTEXT_COMPLI);
  }

  /**
   * Gets the compli params.
   *
   * @return the compli params
   * @throws ApicWebServiceException the apic web service exception
   */
  public CompliParamOutput getCompliParams() throws ApicWebServiceException {
    return get(getWsBase(), CompliParamOutput.class);
  }
}