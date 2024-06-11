/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;

/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_APIC_ACCESS)
public class ApicAccessRightService extends AbstractRestService {

  /**
   * Get Current user access rights details
   *
   * @return response
   * @throws IcdmException data exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_APIC_CURRENT_USER)
  @CompressData
  public Response getCurrentUserAccessRights() throws IcdmException {
    // fetch the apic access rights of the current user
    ApicAccessRight accessRights = new ApicAccessRightLoader(getServiceData()).getAccessRightsCurrentUser();
    return Response.ok(accessRights).build();
  }

  /**
   * Get user access rights details
   *
   * @param userName NT userId
   * @return response
   * @throws IcdmException data exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_BY_USER_NT_ID)
  @CompressData
  public Response getAccessRightsByUserName(
      @QueryParam(value = WsCommonConstants.RWS_QP_USERNAME) final String userName) 
      throws IcdmException {
    // fetch the apic access rights of the given user
    ApicAccessRight accessRights = new ApicAccessRightLoader(getServiceData()).getAccessRightsByUserName(userName);
    return Response.ok(accessRights).build();
  }
}