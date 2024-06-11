/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.bo.general.IcdmFilesLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author NDV4KOR
 */

@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ICDM_OSS_DOCUMENT)

public class OSSDocumentDownloadService extends AbstractRestService {

  /**
   * Download the iCDM OSS Document
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */

  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @CompressData
  public Response getOSSDocument() throws IcdmException {
    byte[] ret = new IcdmFilesLoader(getServiceData())
        .getEntityObject(Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.OSS_FILE_ID)))
        .getTabvIcdmFileData().getFileData();

    return Response.ok(ret).build();
  }

}
