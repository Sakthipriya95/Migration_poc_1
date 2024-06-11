/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivCdlCommand;
import com.bosch.caltool.icdm.bo.wp.WorkpackageDivisionCdlLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;

/**
 * Services for Work package divsion CDL
 * 
 * @author apj4cob
 *
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_WP_DIV_CDL)
public class WorkPackageDivCdlService extends AbstractRestService {
  /**
   * Create a WorkPackageDivsionCdl
   *
   * @param input WorkPackageDivsionCdl
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final WorkpackageDivisionCdl input) throws IcdmException {

    WorkPackageDivCdlCommand cmd = new WorkPackageDivCdlCommand(getServiceData(), input);
    executeCommand(cmd);

    WorkpackageDivisionCdl ret = cmd.getNewData();

    WSObjectStore.getLogger().info("WorkPackageDivsionCdl.create completed. ID = {}", ret.getId());

    return Response.ok(ret).build();


  }
  /**
   * Update an existing WorkPackageDivsionCdl
   *
   * @param input WorkPackageDivsionCdl details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final WorkpackageDivisionCdl input) throws IcdmException {

    WorkPackageDivCdlCommand cmd = new WorkPackageDivCdlCommand(getServiceData(), input, true);
    executeCommand(cmd);

    WorkpackageDivisionCdl ret = cmd.getNewData();

    WSObjectStore.getLogger().info("WorkPackageDivsionCdl.update completed. ID = {} ", ret.getId());

    return Response.ok(ret).build();


  }
  /**
   * Delete a TWorkpackageDivisionCdl record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
   WorkpackageDivisionCdlLoader loader = new WorkpackageDivisionCdlLoader(getServiceData());
    
   WorkpackageDivisionCdl obj = loader.getDataObjectByID(objId);
    
   WorkPackageDivCdlCommand cmd = new WorkPackageDivCdlCommand(getServiceData(), obj, false);
    
   executeCommand(cmd);
    
   return Response.ok().build();
  }

  /**
   * @param wpDivId Work Package Division Id
   * @return Rest response
   * @throws IcdmException if input data is invalid 
   */
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  @Path(WsCommonConstants.RWS_GET_ALL_BY_WP_DIV_ID)
  @CompressData
  public Response findCdlByDivId(@QueryParam(value = WsCommonConstants.RWS_QP_WP_DIV_ID) final Long wpDivId) throws IcdmException{
    WorkpackageDivisionCdlLoader loader=new WorkpackageDivisionCdlLoader(getServiceData());
    Set<WorkpackageDivisionCdl> retSet=loader.getWorkpkgDivCdlByWpDivId(wpDivId);
    WSObjectStore.getLogger().info("WorkPackageDivsion.findCdlByDivId() completed. Number of wp divisions cdl = {}",
        retSet.size());
    return Response.ok(retSet).build();
}

}
