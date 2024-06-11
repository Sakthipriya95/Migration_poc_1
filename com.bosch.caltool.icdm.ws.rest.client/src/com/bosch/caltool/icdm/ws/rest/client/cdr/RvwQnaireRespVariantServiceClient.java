/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.DefineQnaireRespInputData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Client class for Qustionnaire response variant
 *
 * @author dmr1cob
 */
public class RvwQnaireRespVariantServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor - initialise RvwQnaireResponseVariantServiceClient
   */
  public RvwQnaireRespVariantServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RVW_QNAIRE_RESPONSE_VARIANT);
  }

  /**
   * @param rvwQnaireRespVariantId Qnaire resp var id
   * @return {@link RvwQnaireRespVariant} obj
   * @throws ApicWebServiceException exception in ws
   */
  public RvwQnaireRespVariant getRvwQnaireRespVariant(final Long rvwQnaireRespVariantId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, rvwQnaireRespVariantId);
    return get(wsTarget, RvwQnaireRespVariant.class);
  }


  /**
   * @param rvwQnaireRespId {@link RvwQnaireResponse} id
   * @return list of {@link RvwQnaireRespVariant}
   * @throws ApicWebServiceException Exception in webservice
   */
  public List<RvwQnaireRespVariant> getRvwQnaireRespVariantList(final Long rvwQnaireRespId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_RVW_QNAIRE_RESPONSE_VARIANT_LIST)
        .queryParam(WsCommonConstants.RVW_QP_QNAIRE_RESP_ID, rvwQnaireRespId);
    GenericType<List<RvwQnaireRespVariant>> type = new GenericType<List<RvwQnaireRespVariant>>() {};
    return get(wsTarget, type);
  }

  /**
   * @param pidcVersId selected pidc version id
   * @return {@link DefineQnaireRespInputData} object
   * @throws ApicWebServiceException exception in ws call
   */
  public DefineQnaireRespInputData getDefineQnaireRespInputData(final Long pidcVersId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_DEFINE_QNAIRERESP_INPUT_DATA)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_VERSION_ID, pidcVersId);
    GenericType<DefineQnaireRespInputData> type = new GenericType<DefineQnaireRespInputData>() {};
    return get(wsTarget, type);
  }
}
