/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReportModel;
import com.bosch.caltool.icdm.model.cdr.CdrReport;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author gge6cob
 */
public class CdrReportServiceClient extends AbstractRestServiceClient {

  /**
   * Instantiates a new CDR service client.
   */
  public CdrReportServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_CDR_REPORT);
  }

  /**
   * Get the review details of all parameters in the given A2L File
   *
   * @param pidcA2lId PIDC Version - A2L File mapping ID
   * @param variantID PIDC variant ID, optional
   * @param maxReviews maximum reviews to be considered/parameter
   * @param fetchCheckVal true if checkvalue has to be fetched
   * @param a2lRespId a2l Responsibility Id
   * @param a2lWpId a2l Workpackage ID
   * @return the review details object
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public CdrReport getCdrReport(final Long pidcA2lId, final Long variantID, final int maxReviews,
      final boolean fetchCheckVal, final Long a2lRespId, final Long a2lWpId)
      throws ApicWebServiceException {

    LOGGER.debug("Loading CDR Report for pidcA2lId = {}; variantID = {}; maxReviews = {}; fetchCheckVal = {}",
        pidcA2lId, variantID, maxReviews, fetchCheckVal);

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_FETCH_CHECKVAL, fetchCheckVal)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId)
        .queryParam(WsCommonConstants.RWS_CDRRPT_QP_MAX_REVIEWS, maxReviews)
        .queryParam(WsCommonConstants.RWS_A2L_RESP_ID, a2lRespId)
        .queryParam(WsCommonConstants.A2L_WP_ID, a2lWpId);

    if (variantID != null) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantID);
    }

    CdrReport response = get(wsTarget, CdrReport.class);

    LOGGER.debug("CDR Report loaded. No. of definitions : {}", response.getReviewDetMap().size());

    return response;
  }

  /**
   * Get the review details of all parameters in the given A2L File
   *
   * @param pidcA2lId PIDC Version - A2L File mapping ID
   * @param variantID PIDC variant ID, optional
   * @param maxReviews maximum reviews to be considered/parameter
   * @param fetchCheckVal true if checkvalue has to be fetched
   * @param a2lRespId a2l Responsibility Id
   * @param a2lWpId a2l Workpackage ID
   * @return the review details object
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  public CDRReportModel getCdrReportModel(final Long pidcA2lId, final Long variantID, final int maxReviews,
      final boolean fetchCheckVal, final Long a2lRespId, final Long a2lWpId)
      throws ApicWebServiceException {

    LOGGER.debug("Loading CDR Report for pidcA2lId = {}; variantID = {}; maxReviews = {}; fetchCheckVal = {}",
        pidcA2lId, variantID, maxReviews, fetchCheckVal);

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CDR_REPORT_MODEL)
        .queryParam(WsCommonConstants.RWS_QP_FETCH_CHECKVAL, fetchCheckVal)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2lId)
        .queryParam(WsCommonConstants.RWS_CDRRPT_QP_MAX_REVIEWS, maxReviews)
        .queryParam(WsCommonConstants.RWS_A2L_RESP_ID, a2lRespId).queryParam(WsCommonConstants.A2L_WP_ID, a2lWpId);

    if (variantID != null) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantID);
    }

    CDRReportModel response = get(wsTarget, CDRReportModel.class);

    LOGGER.debug("CDR Report loaded. Number of CDR Report Parameters : {}", response.getCdrReportParams().size());

    return response;
  }

}
