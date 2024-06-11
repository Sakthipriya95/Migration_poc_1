/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.rm.ConsolidatedRisks;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob Service client to get functions based on search criteria
 */
public class ConsolidatedRiskCatClient extends AbstractRestServiceClient {

  /**
   * Service client for WorkPackageDivisionService
   */
  public ConsolidatedRiskCatClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_RISK_DEFINTION);
  }

  /**
   * Get all the Pidc Rm defintions For the version id
   *
   * @param pidcRmId pidcRmId
   * @return List of Pidc Rm defintions
   * @throws ApicWebServiceException error during service call
   */
  public ConsolidatedRisks getConsolidatedRisks(final Long pidcRmId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RISK_CONS_CAT_RISK)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_RM_ID, pidcRmId);

    GenericType<ConsolidatedRisks> type = new GenericType<ConsolidatedRisks>() {};
    return get(wsTarget, type);
  }

  /**
   * Get all the Pidc Rm defintions For the version id
   *
   * @param pidcVersID pidcVersID
   * @return Consolidated Risks
   * @throws ApicWebServiceException error during service call
   */
  public ConsolidatedRisks getConsolidatedRisksForVersion(final Long pidcVersID) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RISK_CONS_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_VERS_ID, pidcVersID);

    GenericType<ConsolidatedRisks> type = new GenericType<ConsolidatedRisks>() {};
    return get(wsTarget, type);
  }


}
