/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.vcdm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.vcdm.VcdmFileDownload;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_VCDM + "/" + WsCommonConstants.RWS_FILE_DOWNLOAD)
public class VcdmFileDownloadService extends AbstractRestService {

  /**
   * Download file from vCDM
   *
   * @param pidcId pidc ID
   * @param vCDMFileId vCDM file ID
   * @return rest response byte array of vCDM file
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response get(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_ID) final Long pidcId,
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long vCDMFileId)
      throws IcdmException {

    getLogger().debug("vCDM File Download Service : start");
    // method to get the vcdm file
    byte[] ret = new VcdmFileDownload(getServiceData()).fetchVcdmFile(pidcId, vCDMFileId);

    getLogger().debug("vCDM File Download Service : end");

    return Response.ok(ret).build();
  }
}
