/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.DaDataAssessmentLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TDaDataAssessment;


/**
 * @author NDV4KOR
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_DA_DATA_ASSESSMENT_DOWNLOAD)
public class DaDataAssessmentDownloadService extends AbstractRestService {

  /**
   * @param dataAssessmentId
   * @return
   * @throws IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response getDataAssessementReport(
      @QueryParam(WsCommonConstants.RWS_DA_BASELINE_ID) final Long dataAssessmentId)
      throws IcdmException {
    byte[] ret = new DaDataAssessmentLoader(getServiceData()).getEntityObject(dataAssessmentId).getTDaFiles().get(0)
        .getFileData();
    return Response.ok(ret).build();
  }

  /**
   * @param dataAssessmentId Data Assessment ID
   * @return File archival status
   * @throws IcdmException Exception in case of errors
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_CHECK_STATUS)
  @CompressData
  public Response checkFileDownloadStatus(@QueryParam(WsCommonConstants.RWS_DA_BASELINE_ID) final Long dataAssessmentId)
      throws IcdmException {

    getLogger().debug("Fetching the status of file archival for baseline with ID: {}", dataAssessmentId);
    String ret = null;
    TDaDataAssessment tDaDataAssessment =
        new DaDataAssessmentLoader(getServiceData()).getEntityObject(dataAssessmentId);

    if (CommonUtils.isNotNull(tDaDataAssessment)) {
      ret = tDaDataAssessment.getFileArchivalStatus();
    }
    getLogger().debug("The file archival status fetched as {} for baseline with ID: {}", ret, dataAssessmentId);
    return Response.ok(ret).build();

  }
}
