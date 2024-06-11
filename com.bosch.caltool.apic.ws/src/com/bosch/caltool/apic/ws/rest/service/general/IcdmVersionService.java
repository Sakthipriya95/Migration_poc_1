/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.CommonParamKey;

/**
 * @author nip4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ICDM_VERSION)
public class IcdmVersionService extends AbstractRestService {

  /**
   * Get the iCDM version
   *
   * @return iCDM version
   * @throws IcdmException error during service call
   */
  @GET
  @CompressData
  public Response getIcdmVersion() throws IcdmException {
    String icdmVersion = new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ICDM_CLIENT_VERSION);
    getLogger().info("iCDM version is : {}", icdmVersion);

    return Response.ok(icdmVersion).build();
  }
}
