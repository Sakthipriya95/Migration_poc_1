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


/**
 * Rest service to get the details of database refresh service
 *
 * @author bne4cob
 * @deprecated not used
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_SYS + "/" + WsCommonConstants.RWS_CQN_STATE)
@Deprecated
public class DataModelRefreshService {

  /**
   * @return 'OK' response if refresh is running properly. Else 'ERROR'
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
  public Response getRefreshState() {

    // If updater is successful until now, current state is OK else ERROR
    String state = "OK";
    // Build the response
    return Response.ok(state).build();
  }
}
