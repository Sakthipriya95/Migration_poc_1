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
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.CommonParamKey;


/**
 * @author bne4cob
 * @deprecated to be removed after 2021.5.0 release
 */
@Deprecated
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ICDM_CLIENT_VERSION)
public class IcdmClientVersionService extends AbstractRestService {

  /**
   * @return iCDM version from database
   * @throws IcdmException - error during webservice call
   */
  @GET
  @CompressData
  @Produces({ MediaType.APPLICATION_JSON })
  public Response getIcdmVersion() throws IcdmException {
    // fetch the iCDM version from database
    String icdmVersion = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_CLIENT_VERSION);
    // Log the fetched version
    getLogger().info("IcdmClientVersionService.getIcdmVersion() completed. iCDM version is {}", icdmVersion);
    // return iCDM version
    return Response.ok(icdmVersion).build();
  }
}
