/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacter;
import com.bosch.caltool.icdm.model.rm.PidcRmProjCharacterExt;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob Service client to get functions based on search criteria
 */
public class PidcRmProjCharClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public PidcRmProjCharClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_RISK_DEFINTION);
  }


  /**
   * @param projchar new definition details
   * @return definition details from service
   * @throws ApicWebServiceException any exception
   */
  public PidcRmProjCharacter create(final PidcRmProjCharacter projchar) throws ApicWebServiceException {
    return put(getWsBase().path(WsCommonConstants.RWS_PID_RM_PROJ_CHAR), projchar, PidcRmProjCharacter.class);
  }

  /**
   * @param projchar new definition details
   * @return definition details from service
   * @throws ApicWebServiceException any exception
   */
  public PidcRmProjCharacter update(final PidcRmProjCharacter projchar) throws ApicWebServiceException {
    return post(getWsBase().path(WsCommonConstants.RWS_PID_RM_PROJ_CHAR_UPDT), projchar, PidcRmProjCharacter.class);
  }

  /**
   * Get all the Pidc Rm defintions For the version id
   * 
   * @param pidcRmId pidcRmId
   * @return List of Pidc Rm defintions
   * @throws ApicWebServiceException error during service call
   */
  public Set<PidcRmProjCharacterExt> getPidcRmProjcharExt(final Long pidcRmId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_PID_RM_OUTPUT)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_RM_ID, pidcRmId);

    GenericType<Set<PidcRmProjCharacterExt>> type = new GenericType<Set<PidcRmProjCharacterExt>>() {};
    return get(wsTarget, type);
  }
}
