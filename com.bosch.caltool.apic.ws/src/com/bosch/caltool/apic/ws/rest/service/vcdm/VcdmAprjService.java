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
import com.bosch.caltool.icdm.bo.vcdm.VcdmAprjLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author dja7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_VCDM + "/" + WsCommonConstants.RWS_APRJ)
public class VcdmAprjService extends AbstractRestService {


  /**
   * @param aprjName APRJ Name
   * @return Aprj ID
   * @throws IcdmException service error
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getAprjIdByName(@QueryParam(value = WsCommonConstants.RWS_QP_APRJ_NAME) final String aprjName)
      throws IcdmException {

    String ret = new VcdmAprjLoader(getServiceData()).getAprjIdByName(aprjName);
    return Response.ok(ret).build();
  }
}
