/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic.emr;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.emr.EmrFileLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;

/**
 * @author mkl2cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_EMISSION_ROBUSTNESS + "/" +
    WsCommonConstants.RWS_HANDLE_FILE + "/" + WsCommonConstants.RWS_EMR_FILE_UPLOAD_RESULT)
public class EMRResultService extends AbstractRestService {

  /**
   * Returns Emr File-Variant-Ems mapping.
   *
   * @param fileIds Emr file Ids
   * @return mapping data
   * @throws IcdmException IcdmException
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  // @Path(WsCommonConstants.RWS_GET_EMS_VARIANT_MAP)
  @CompressData
  public Response getFileUploadErrors(@QueryParam(value = WsCommonConstants.RWS_EMR_FILE_ID) final Long fileId)
      throws IcdmException {
    ServiceData serviceData = getServiceData();
    // Create parameter properties loader object
    EmrFileLoader loader = new EmrFileLoader(serviceData);
    // Fetch the EMRfile Upload Errors
    List<EmrUploadError> emrFileUploadErrors = loader.getEMRFileUploadErrors(fileId);
    WSObjectStore.getLogger().info("EMR file-errors list ount : " + emrFileUploadErrors.size());
    //Build the response object and feed the file upload errors to it.
    ResponseBuilder response = Response.ok(emrFileUploadErrors);
    return response.build();
  }

}
