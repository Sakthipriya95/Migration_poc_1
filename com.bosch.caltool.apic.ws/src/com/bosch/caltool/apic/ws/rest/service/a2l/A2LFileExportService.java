/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2LFileExport;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.A2LFileExportServiceInput;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.A2L_FILE_EXP)
public class A2LFileExportService extends AbstractRestService {


  /**
   * Service to Export A2l Files from iCDM
   *
   * @param a2lExpServInp as input
   * @return response
   * @throws IcdmException exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response exportA2lFile(final A2LFileExportServiceInput a2lExpServInp) throws IcdmException {
    // Use the constructA2LFileExport to export the a2l with Group info
    String outputZipFilePath = new A2LFileExport(getServiceData()).constructA2LFileExport(a2lExpServInp);

    ResponseBuilder response = Response.ok(new File(outputZipFilePath));
    // set the Response file name
    response.header("Content-Disposition", "attachment; filename=" + outputZipFilePath);
    return response.build();
  }


}
