/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPDefinitionCommand;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPDefLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;


/**
 * Get the FC2WP definition
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FC2WP_DEF)
public class FC2WPDefinitionService extends AbstractRestService {

  /**
   * Get all FC2WP Definitions
   *
   * @return Rest response
   * @throws IcdmException IcdmException exception in service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllDefinitions() throws IcdmException {

    // Create parameter properties loader object
    FC2WPDefLoader loader = new FC2WPDefLoader(getServiceData());

    // Fetch FC2WP mappings
    Set<FC2WPDef> retSet = loader.getAllDefinitions();

    WSObjectStore.getLogger().info("FC2WPDefinitionService.getAllDefinitions() completed. Number of definitions = {}",
        retSet.size());

    return Response.ok(retSet).build();
  }


  /**
   * Create a FC2WP definition
   *
   * @param def FC2WP Definition details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response createFC2WPDefinition(final FC2WPDef def) throws IcdmException {

    // Invoke command
    FC2WPDefinitionCommand cmd = new FC2WPDefinitionCommand(getServiceData(), def);
    executeCommand(cmd);

    FC2WPDef ret = cmd.getNewData();

    WSObjectStore.getLogger().info("FC2WPDefinitionService.createFC2WPDefinition completed. ID = {}", ret.getId());

    return Response.ok(ret).build();

  }


}
