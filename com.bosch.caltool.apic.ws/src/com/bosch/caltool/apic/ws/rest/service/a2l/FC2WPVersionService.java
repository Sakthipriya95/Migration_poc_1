/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPVersionCommand;
import com.bosch.caltool.icdm.bo.fc2wp.FC2WPVersionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;


/**
 * Services for FC to WP Versions
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FC2WP_VERS)
public class FC2WPVersionService extends AbstractRestService {

  /**
   * Fetch FC2WP version with the given Id
   *
   * @param objId Primary Key
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
  @CompressData
  public Response findById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {

    FC2WPVersionLoader loader = new FC2WPVersionLoader(getServiceData());

    // Fetch FC2WPVersion
    FC2WPVersion ret = loader.getDataObjectByID(objId);

    return Response.ok(ret).build();

  }

  /**
   * Create a FC2WP version
   *
   * @param vers FC2WP version details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final FC2WPVersion vers) throws IcdmException {

    FC2WPVersionCommand cmd = new FC2WPVersionCommand(getServiceData(), vers);
    executeCommand(cmd);

    FC2WPVersion ret = cmd.getNewData();

    WSObjectStore.getLogger().info("FC2WPVersionService.createVersion completed. ID = {}", ret.getFcwpDefId());

    return Response.ok(ret).build();


  }

  /**
   * Update an existing FC2WP version
   *
   * @param vers FC2WP version details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final FC2WPVersion vers) throws IcdmException {

    FC2WPVersionCommand cmd = new FC2WPVersionCommand(getServiceData(), vers, true);
    executeCommand(cmd);

    FC2WPVersion ret = cmd.getNewData();

    WSObjectStore.getLogger().info("FC2WPVersionService.update completed. ID = {}", ret.getFcwpDefId());

    return Response.ok(ret).build();


  }

  /**
   * Fetch all FC to WP versions of the given definition
   *
   * @param fc2wpDefID FC to WP Definition ID
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_BY_DEF_ID)
  @CompressData
  public Response findByDefID(@QueryParam(value = WsCommonConstants.RWS_QP_FC2WP_DEF_ID) final Long fc2wpDefID)
      throws IcdmException {

    FC2WPVersionLoader loader = new FC2WPVersionLoader(getServiceData());

    // Fetch all FC to WP versions of the given definition
    Set<FC2WPVersion> retSet = loader.getVersionsByDefID(fc2wpDefID);

    WSObjectStore.getLogger().info("FC2WPVersionService.getVersionsByDefID() completed. Number of versions = {}",
        retSet.size());

    return Response.ok(retSet).build();

  }


  /**
   * Get the working set version of the given FC2WP definition
   *
   * @param fc2wpDefID FC to WP Definition ID
   * @return Rest response
   * @throws IcdmException exception from service
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_WS_VERS_BY_DEF_ID)
  @CompressData
  public Response getWorkingSetVersionByDefID(
      @QueryParam(value = WsCommonConstants.RWS_QP_FC2WP_DEF_ID) final Long fc2wpDefID)
      throws IcdmException {

    FC2WPVersionLoader loader = new FC2WPVersionLoader(getServiceData());

    // Fetch FC2WP version
    FC2WPVersion ret = loader.getWorkingSetVersion(fc2wpDefID);

    return Response.ok(ret).build();

  }

  /**
   * Get the working set version of the given FC2WP definition identified by name and division
   *
   * @param nameValueId Value ID of name attribute value
   * @param divValueId value ID of division attribute value
   * @return Rest response
   * @throws IcdmException exception from service call
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ACTIVE_VERS_BY_VALUE_ID)
  @CompressData
  public Response getActiveVersionByValueID(
      @QueryParam(value = WsCommonConstants.RWS_QP_FC2_WP_NAME) final String fc2wpName,
      @QueryParam(value = WsCommonConstants.RWS_QP_DIV_VALUE_ID) final Long divValueId)
      throws IcdmException {

    FC2WPVersionLoader loader = new FC2WPVersionLoader(getServiceData());

    // Fetch active FC2WP version
    FC2WPVersion ret = loader.getActiveVersionByValueID(fc2wpName, divValueId);

    return Response.ok(ret).build();
  }

  /**
   * Fetch FC2WP version with the given PIDC version Id.
   *
   * @param pidcVersId the pidc vers id
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ACTIVE_VERS_BY_PIDC)
  @CompressData
  public Response findByPidcVersId(@QueryParam(value = WsCommonConstants.RWS_QP_PIDC_VERS_ID) final Long pidcVersId)
      throws IcdmException {

    FC2WPVersionLoader loader = new FC2WPVersionLoader(getServiceData());

    // Fetch FC2WPVersion
    FC2WPVersion ret = loader.findActiveVersionByPidcVersion(pidcVersId);

    return Response.ok(ret).build();

  }

}