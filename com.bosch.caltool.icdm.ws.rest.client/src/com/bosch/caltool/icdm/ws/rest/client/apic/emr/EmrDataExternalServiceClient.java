/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic.emr;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.emr.EmrDataExternalResponse;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Service client to fetch EMR sheet data
 *
 * @author dja7cob
 */
public class EmrDataExternalServiceClient extends AbstractRestServiceClient {

  /**
   * Service client for EmrFileService
   */
  public EmrDataExternalServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_EMISSION_ROBUSTNESS +
        WsCommonConstants.RWS_URL_DELIMITER + WsCommonConstants.RWS_EMR_DATA_EXTERNAL);
  }

  /**
   * @param pidcVersId pidc version id
   * @param variantId pidc variant id
   * @return EmrFileContentResponse
   * @throws ApicWebServiceException Exception in retrieving emr file contents
   */
  public EmrDataExternalResponse fetchEmrDataExternal(final Long pidcVersId, final Long variantId)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_PIDC_VERS_ID, pidcVersId)
        .queryParam(WsCommonConstants.RWS_QP_VARIANT_ID, variantId);
    EmrDataExternalResponse response = get(wsTarget, EmrDataExternalResponse.class);

    LOGGER.debug("Number of file records : {}, Number of EMR sheet data records : {}",
        response.getEmrFileDetailsMap().size(), response.getEmrDataMap().size());

    return response;
  }
}
