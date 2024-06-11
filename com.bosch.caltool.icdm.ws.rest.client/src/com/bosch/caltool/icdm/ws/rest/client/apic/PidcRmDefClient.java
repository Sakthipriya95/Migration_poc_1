/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.rm.PidcRmDefinition;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob Service client to get functions based on search criteria
 */
public class PidcRmDefClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public PidcRmDefClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_RISK_DEFINTION);
  }

  /**
   * Get all the Pidc Rm defintions For the version id
   *
   * @param pidVersId pidc version id
   * @return List of Pidc Rm defintions
   * @throws ApicWebServiceException error during service call
   */
  public List<PidcRmDefinition> getPidRmDefList(final Long pidVersId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_PID_RM_DEF).queryParam(WsCommonConstants.RWS_QP_VERS_ID, pidVersId);

    GenericType<List<PidcRmDefinition>> type = new GenericType<List<PidcRmDefinition>>() {};
    return get(wsTarget, type);
  }

  /**
   * @param def new definition details
   * @return definition details from service
   * @throws ApicWebServiceException any exception
   */
  public PidcRmDefinition create(final PidcRmDefinition def) throws ApicWebServiceException {
    return put(getWsBase().path(WsCommonConstants.RWS_PID_RM_DEF), def, PidcRmDefinition.class);
  }


  /**
   * Checks if pidc risk evaluation list is empty.
   *
   * @param pidcVersId pidc version id
   * @return the list
   * @throws ApicWebServiceException error during service call
   */
  public boolean isPidcRmEmpty(final Long pidcVersId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_IS_PID_RM_EMPTY)
        .queryParam(WsCommonConstants.RWS_QP_VERS_ID, pidcVersId);
    return get(wsTarget, Boolean.class);
  }

}
