/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic.emr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.emr.EmrDataExternalFetcher;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.emr.EmrDataExternalResponse;

/**
 * Service to fetch EMR sheet data
 *
 * @author dja7cob
 */
@Path(WsCommonConstants.RWS_URL_DELIMITER + WsCommonConstants.RWS_CONTEXT_APIC + WsCommonConstants.RWS_URL_DELIMITER +
    WsCommonConstants.RWS_EMISSION_ROBUSTNESS + WsCommonConstants.RWS_URL_DELIMITER +
    WsCommonConstants.RWS_EMR_DATA_EXTERNAL)
public class EmrDataExternalService extends AbstractRestService {

  /**
   * @param pidcVersionId pidc version id
   * @param variantId pidc variant id
   * @return EmrFileContentResponse with file details and file contents
   * @throws IcdmException Exception in fetchng EMR sheet data
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response fetchEmrDataExternal(
      @QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersionId,
      @QueryParam(value = WsCommonConstants.RWS_QP_VARIANT_ID) final Long variantId)
      throws IcdmException {
    //Get the EMR Data
    //Feed emr data to the response object
    EmrDataExternalResponse response =
        new EmrDataExternalFetcher(getServiceData()).fetchEmrDataExternal(pidcVersionId, variantId);
    return Response.ok(response).build();
  }
}
