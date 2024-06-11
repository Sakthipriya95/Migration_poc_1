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
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionCommand;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;


/**
 * Services for Work package divsions
 *
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_WP_DIV)
public class WorkPackageDivsionService extends AbstractRestService {

  /**
   * Fetch Work Package Divsion with the given Id
   *
   * @param objId Primary Key
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response findById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {

    WorkPackageDivisionLoader loader = new WorkPackageDivisionLoader(getServiceData());

    // Fetch WP division with given ID
    WorkPackageDivision ret = loader.getDataObjectByID(objId);

    return Response.ok(ret).build();

  }

  /**
   * Create a WorkPackageDivsion
   *
   * @param input WorkPackageDivsion
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final WorkPackageDivision input) throws IcdmException {

    WorkPackageDivisionCommand cmd = new WorkPackageDivisionCommand(getServiceData(), input);
    executeCommand(cmd);

    WorkPackageDivision ret = cmd.getNewData();

    WSObjectStore.getLogger().info("WorkPackageDivsion.create completed. ID = {}", ret.getId());

    return Response.ok(ret).build();


  }

  /**
   * Update an existing WorkPackageDivsion
   *
   * @param input WorkPackageDivsion details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final WorkPackageDivision input) throws IcdmException {

    WorkPackageDivisionCommand cmd = new WorkPackageDivisionCommand(getServiceData(), input, true);
    executeCommand(cmd);

    WorkPackageDivision ret = cmd.getNewData();

    WSObjectStore.getLogger().info("WorkPackageDivsion.update completed. ID = {} ", ret.getId());

    return Response.ok(ret).build();


  }

  /**
   * Fetch all WorkPackageDivsion for the given division ID (attribute value ID)
   *
   * @param divId Division ID (attribute value ID)
   * @param includeDeleted boolean
   * @param iccRelevantFlag 'Y'/'N' (optional parameter)
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_WP_DIV_BY_DIV_ID)
  @CompressData
  public Response findByDivId(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long divId,
      @QueryParam(value = WsCommonConstants.RWS_QP_INCLUDE_DELETED) final boolean includeDeleted,
      @QueryParam(value = WsCommonConstants.ICC_RELEVANT_FLAG) final String iccRelevantFlag)
      throws IcdmException {

    WorkPackageDivisionLoader loader = new WorkPackageDivisionLoader(getServiceData());

    // Fetch all WorkPackageDivsions of the given divId
    Set<WorkPackageDivision> retSet = loader.getWorkPackageDivByDivID(divId, includeDeleted, iccRelevantFlag);

    WSObjectStore.getLogger().info("WorkPackageDivsion.findByDivId() completed. Number of wp divisions = {}",
        retSet.size());

    return Response.ok(retSet).build();
  }


  /**
   * Fetch all WorkPackageDivsion for the given workpackage ID
   *
   * @param wpId workpackage id
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_WP_DIV_BY_WP_ID)
  @CompressData
  public Response findByWpId(@QueryParam(value = WsCommonConstants.RWS_QP_WP_ID) final Long wpId) throws IcdmException {

    WorkPackageDivisionLoader loader = new WorkPackageDivisionLoader(getServiceData());

    // Fetch all WorkPackageDivsions of the given wp id
    Set<WorkPackageDivision> retSet = loader.getWorkPackageDivByWpID(wpId);

    WSObjectStore.getLogger().info("WorkPackageDivsion.findByWpId() completed. Number of wp divisions = {}",
        retSet.size());

    return Response.ok(retSet).build();

  }

}
