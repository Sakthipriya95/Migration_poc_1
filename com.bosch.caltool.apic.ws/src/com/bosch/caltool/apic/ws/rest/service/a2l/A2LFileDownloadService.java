/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.a2l.A2LFileDownload;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * Rest service download A2L File
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_A2L_DOWNLOAD)
public class A2LFileDownloadService extends AbstractRestService {


  /**
   * Get A2L file for the vCDM A2L file ID as octet stream
   *
   * @param vcdmA2lFileId vcdm A2L file ID
   * @return Rest response
   * @throws IcdmException the icdm exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response getA2lFileById(@QueryParam(value = WsCommonConstants.RWS_QP_VCDM_A2LFILE_ID) final Long vcdmA2lFileId)
      throws IcdmException {

    byte[] ret = new A2LFileDownload(getServiceData()).getA2lFile(vcdmA2lFileId);
    return Response.ok(ret).build();
  }
}
