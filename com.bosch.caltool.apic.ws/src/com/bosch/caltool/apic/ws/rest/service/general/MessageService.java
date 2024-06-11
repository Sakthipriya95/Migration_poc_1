/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_MESSAGE)
public class MessageService extends AbstractRestService {

  /**
   * Get all Messages
   *
   * @return all messages
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll() throws IcdmException {

    // Create loader object
    MessageLoader loader = new MessageLoader(getServiceData());
    Map<String, String> retMap = loader.getAllMessagesMap();

    getLogger().info("MessageService.getAll() completed. Number of Messages = {}", retMap.size());

    return Response.ok(retMap).build();
  }

}