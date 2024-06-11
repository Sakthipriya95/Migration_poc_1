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
 * @author NIP4COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ICDM_CLIENT_STARTUP)

public class IcdmClientStartupService extends AbstractRestService {


  /**
   * download the welcome page files
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_GET_WELCOME_PAGE_FILES)
  @CompressData
  public Response getWelcomePageFiles() throws IcdmException {
    byte[] ret = new IcdmFilesLoader(getServiceData())
        .getEntityObject(Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.WELCOME_FILE_ID)))
        .getTabvIcdmFileData().getFileData();

    return Response.ok(ret).build();
  }

  /**
   * retrieve the dislaimer file
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_GET_DISCLAIMER_FILE)
  @CompressData
  public Response getDisclaimerFile() throws IcdmException {
    byte[] ret = new IcdmFilesLoader(getServiceData())
        .getEntityObject(
            Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.DISCLAIMER_FILE_ID)))
        .getTabvIcdmFileData().getFileData();

    return Response.ok(ret).build();
  }

  /**
   * retrieve the dislaimer file
   *
   * @return rest response
   * @throws IcdmException error during webservice call
   */
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM })
  @Path(WsCommonConstants.RWS_GET_MAIL_TEMPLATE_FILE)
  @CompressData
  public Response getMailtoHotLineFile() throws IcdmException {
    byte[] ret = new IcdmFilesLoader(getServiceData())
        .getEntityObject(
            Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_HOTLINE_TEMPL_FILE)))
        .getTabvIcdmFileData().getFileData();

    return Response.ok(ret).build();
  }


}
