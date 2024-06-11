/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.QssdParamOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author hnu1cob
 */
public class QssdParamServiceClient extends AbstractRestServiceClient {

  /**
   * Instantiates a new QSSD param service client.
   */
  public QssdParamServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_QSSD);
  }

  /**
   * @return Map of qssd parameters.key- param name , value - ptype
   * @throws ApicWebServiceException exception
   */
  public QssdParamOutput getQssdParams() throws ApicWebServiceException {
    QssdParamOutput response = get(getWsBase(), QssdParamOutput.class);

    LOGGER.info("QSSD Parameters found  = {}", response.getQssdParamMap().size());

    return response;
  }
}
