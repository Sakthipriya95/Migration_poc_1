package com.bosch.caltool.apic.ws.rest.service.general;

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
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupBO;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupCommand;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;


/**
 * Service class for ActiveDirectoryGroup
 *
 * @author SSN9COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ACTIVEDIRECTORYGROUP))
public class ActiveDirectoryGroupService extends AbstractRestService {

  /**
   * Rest web service path for ActiveDirectoryGroup
   */
  public static final String RWS_ACTIVEDIRECTORYGROUP = "activedirectorygroup";


  /**
   * Create a ActiveDirectoryGroup record
   *
   * @param obj object to create
   * @return Rest response, with created ActiveDirectoryGroup object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final ActiveDirectoryGroup obj) throws IcdmException {
    ActiveDirectoryGroupCommand cmd = new ActiveDirectoryGroupCommand(getServiceData(), obj, false, false, false);
    executeCommand(cmd);
    ActiveDirectoryGroup ret = cmd.getNewData();
    getLogger().info("Created ActiveDirectoryGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a ActiveDirectoryGroup record
   *
   * @param obj object to update
   * @return Rest response, with updated ActiveDirectoryGroup object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final ActiveDirectoryGroup obj) throws IcdmException {
    ActiveDirectoryGroupCommand cmd = new ActiveDirectoryGroupCommand(getServiceData(), obj, true, false, false);
    executeCommand(cmd);
    ActiveDirectoryGroup ret = cmd.getNewData();
    getLogger().info("Updated ActiveDirectoryGroup Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a ActiveDirectoryGroup record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    ActiveDirectoryGroupLoader loader = new ActiveDirectoryGroupLoader(getServiceData());
    ActiveDirectoryGroup obj = loader.getDataObjectByID(objId);
    ActiveDirectoryGroupCommand cmd = new ActiveDirectoryGroupCommand(getServiceData(), obj, false, true, false);
    executeCommand(cmd);
    return Response.ok().build();
  }

  /**
   * @param objName group name
   * @return model
   * @throws IcdmException exception
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_NAME) final String objName) throws IcdmException {
    ActiveDirectoryGroupLoader loader = new ActiveDirectoryGroupLoader(getServiceData());
    ActiveDirectoryGroup ret = loader.getByGroupName(objName);
    return Response.ok(ret).build();
  }

  /**
   * Sync all ActiveDirectoryGroups with LDAP Data
   *
   * @return Success message
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Path(WsCommonConstants.RWS_SYNC_ACTIVEDIRECTORYGROUPUSERS)
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response syncActiveDirectoryGroupUsers() throws IcdmException {
    new ActiveDirectoryGroupBO(getServiceData()).syncGroupUserDetails();
    getLogger().info("All the Active Directory Group Users Synced with LDAP data ");
    return Response.ok().build();
  }


}
