/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2LFiles;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class PidcA2LFilesServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public PidcA2LFilesServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_A2L);
  }

  /**
   * Get the pidc a2l files
   *
   * @param projectID Project(PIDC) ID
   * @return Rest response
   * @throws ApicWebServiceException service error
   */
  public PidcA2LFiles getPidcA2lFilesWithResults(final Long projectID) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_PROJECT_ID, projectID);
    PidcA2LFiles ret = get(wsTarget, PidcA2LFiles.class);
    LOGGER.info("PIDC A2L files returned = {}, versions = {}, variants = {}", ret.getPidcA2LInfo(),
        ret.getPidcVersMap(), ret.getPidcVarsMap());
    return ret;
  }

}
