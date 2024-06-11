/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPDefRelvPTtypeCommand;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPRelvPTTypeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPRelvPTType;


/**
 * Get the Relevant PT definition
 *
 * @author dmo5cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_RELEVANT_PT_TYPE)
public class FC2WPRelevantPTTypeService extends AbstractRestService {

  /**
   * Get the FC2WP Relevant Power Train types
   *
   * @param fc2wpDefID FC2WP Definition ID
   * @return Rest response
   * @throws IcdmException service errors
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getFC2WPRelevantPTtypes(
      @QueryParam(value = WsCommonConstants.RWS_QP_FC2WP_DEF_ID) final Long fc2wpDefID)
      throws IcdmException {

    WSObjectStore.getLogger().info("getFC2WPRelevantPTtypes() started. User Inputs : fc2wpDefID = {}", fc2wpDefID);

    // Create definition loader object
    FC2WPRelvPTTypeLoader loader = new FC2WPRelvPTTypeLoader(getServiceData());

    // Fetch FC2WP relevant PTtypes
    Set<FC2WPRelvPTType> retSet = loader.getFC2WPRelevantPTtypes(fc2wpDefID);

    WSObjectStore.getLogger()
        .info("FC2WPDefinitionService.getFC2WPRelevantPTtypes() completed. Number of definitions = {}", retSet.size());

    return Response.ok(retSet).build();
  }

  /**
   * Create a FC2WP definition
   *
   * @param relvPTType FC2WPRelvPTType
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response createRelvtPTType(final FC2WPRelvPTType relvPTType) throws IcdmException {

    // Invoke command
    FC2WPDefRelvPTtypeCommand cmd = new FC2WPDefRelvPTtypeCommand(getServiceData(), relvPTType, false);
    executeCommand(cmd);

    FC2WPRelvPTType ret = cmd.getNewData();

    WSObjectStore.getLogger().info("FC2WPDefinitionService.createFC2WPDefinition completed. ID = {} ", ret.getId());

    return Response.ok(ret).build();

  }

  /**
   * Create a FC2WP definition.
   *
   * @param fc2wpRelPTTypeID FC2WPRelvPTType key
   * @return the response
   * @throws IcdmException error during execution
   */
  @DELETE
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response deleteRelvtPTType(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long fc2wpRelPTTypeID)
      throws IcdmException {

    ServiceData servData = getServiceData();

    FC2WPRelvPTTypeLoader loader = new FC2WPRelvPTTypeLoader(servData);
    FC2WPRelvPTType relvPTType = loader.getDataObjectByID(fc2wpRelPTTypeID);
    // Invoke command
    FC2WPDefRelvPTtypeCommand cmd = new FC2WPDefRelvPTtypeCommand(servData, relvPTType, true);
    executeCommand(cmd);

    return Response.ok().build();
  }
}
