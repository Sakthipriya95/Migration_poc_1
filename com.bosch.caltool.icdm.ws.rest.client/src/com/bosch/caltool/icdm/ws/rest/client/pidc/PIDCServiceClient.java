/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.pidc;

import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PIDCVersionReport;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PIDCServiceClient extends AbstractRestServiceClient {

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public PIDCServiceClient() {
    super("", WsCommonConstants.RWS_CONTEXT_PIDC);
  }

  /**
   * @param pidcVersionId
   * @return
   * @throws ApicWebServiceException
   */
  public PIDCVersionReport searchProject(final Long pidcVersionId) throws ApicWebServiceException {
    GenericType<PIDCVersionReport> type = new GenericType<PIDCVersionReport>() {};
    PIDCVersionReport response = post(getWsBase(), pidcVersionId, type);
    return response;
  }
}
