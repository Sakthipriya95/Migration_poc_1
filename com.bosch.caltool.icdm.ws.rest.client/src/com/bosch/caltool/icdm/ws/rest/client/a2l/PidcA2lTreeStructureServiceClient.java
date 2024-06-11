/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.PidcA2lTreeStructureModel;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class PidcA2lTreeStructureServiceClient extends AbstractRestServiceClient {

  /**
   * PidcA2lTreeStructureServiceClient constructor
   */
  public PidcA2lTreeStructureServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_PIDC_A2L_TREE_STRUCT);
  }


  /**
   * Service to fetch the PidcA2lTreeStructureModel for a a2l file
   *
   * @param pidcA2lId as pidc a2l id
   * @return PidcA2lTreeStructureModel
   * @throws ApicWebServiceException as exeption
   */
  public PidcA2lTreeStructureModel getPidcA2lTreeStructuresModel(final Long pidcA2lId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_PIDC_A2L_ID, pidcA2lId);
    GenericType<PidcA2lTreeStructureModel> type = new GenericType<PidcA2lTreeStructureModel>() {};
    return get(wsTarget, type);
  }

  /**
   * @param a2lVarGrpId
   * @param activeWpDefnVersID
   * @return
   * @throws ApicWebServiceException
   */
  public Set<A2lWPRespModel> getA2lWpRespModelsForVarGrpWpDefnVersId(final Long a2lVarGrpId,
      final Long activeWpDefnVersID)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WPRESP_MODEL_FOR_VARGRP_DEFNVERSID)
        .queryParam(WsCommonConstants.RWS_QP_A2L_WP_VARIANT_GROUP_ID, a2lVarGrpId)
        .queryParam(WsCommonConstants.RWS_QP_WP_DEFN_VERS_ID, activeWpDefnVersID);
    GenericType<Set<A2lWPRespModel>> type = new GenericType<Set<A2lWPRespModel>>() {};
    return get(wsTarget, type);
  }
}
