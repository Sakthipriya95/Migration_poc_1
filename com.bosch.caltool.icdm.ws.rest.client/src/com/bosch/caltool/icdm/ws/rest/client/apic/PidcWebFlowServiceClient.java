/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowElementRespType;
import com.bosch.caltool.icdm.model.apic.pidc.PidcWebFlowReponseType;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dmr1cob
 */
public class PidcWebFlowServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public PidcWebFlowServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_WEBFLOW);
  }

  /**
   * @param elementId PidcVersId or PidcVarinatId
   * @return {@link PidcWebFlowReponseType}
   * @throws ApicWebServiceException Exception
   */
  public PidcWebFlowReponseType getPidcWebFlowData(final Long elementId) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WEB_FLOW_DATA)
        .queryParam(WsCommonConstants.RWS_QP_ELEMENT_ID, elementId);

    GenericType<PidcWebFlowReponseType> type = new GenericType<PidcWebFlowReponseType>() {};
    return get(wsTarget, type);
  }

  /**
   * @param elementId PidcVersId or PidcVarinatId
   * @return {@link PidcWebFlowElementRespType}
   * @throws ApicWebServiceException Exception
   */
  public PidcWebFlowElementRespType getPidcWebFlowDataElement(final Long elementId) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_WEB_FLOW_ELEMENT)
        .queryParam(WsCommonConstants.RWS_QP_ELEMENT_ID, elementId);

    GenericType<PidcWebFlowElementRespType> type = new GenericType<PidcWebFlowElementRespType>() {};
    return get(wsTarget, type);
  }
}
