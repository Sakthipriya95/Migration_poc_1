/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.ResponsibilityReportLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.ResponsibiltyRvwDataReport;


/**
 * Rest service to fetch review details of all parameters for the given A2L file
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_RESP_REPORT)
public class ResponsibilityReportService extends AbstractRestService {

  /**
   * Get the review details
   *
   * @param pidcA2lId PIDC Version - A2L File mapping ID
   * @param variantID PIDC Variant ID
   * @return Rest response
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  public Response createReport(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_A2L_ID) final Long pidcA2lId,
      @QueryParam(value = WsCommonConstants.RWS_QP_VARIANT_ID) final Long variantID)
      throws IcdmException {

    // get the resp crteator
    ResponsibilityReportLoader respRepCreator = new ResponsibilityReportLoader(getServiceData());
    // create the report information.
    respRepCreator.createReportOutput(pidcA2lId, variantID);
    // Get the report
    ResponsibiltyRvwDataReport respReport = respRepCreator.getReport();

    if (respReport.getDataReportSet().isEmpty()) {
      return Response.ok(CommonUtils.concatenate("There are no reviews for the given input PIDC A2l File id", pidcA2lId,
          " variant id", variantID)).build();
    }

    return Response.ok(respReport).build();
  }


}
