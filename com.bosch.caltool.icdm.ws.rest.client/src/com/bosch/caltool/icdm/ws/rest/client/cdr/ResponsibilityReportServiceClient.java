/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.ResponsibiltyRvwDataReport;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class ResponsibilityReportServiceClient extends AbstractRestServiceClient {

  /**
   */
  public ResponsibilityReportServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_RESP_REPORT);
  }

  /**
   * @param pidcA2lId PIDC Version - A2L File mapping ID
   * @param variantId variant Id
   * @return Responsibilty Rvw Data Report
   * @throws ApicWebServiceException an error
   */
  public ResponsibiltyRvwDataReport getResponsiblityDetails(final Long pidcA2lId, final Long variantId)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId);
    if (variantId != null) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantId);
    }

    return get(wsTarget, ResponsibiltyRvwDataReport.class);
  }

}
