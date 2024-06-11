package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.List;

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
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupUserCommand;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupUserLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapperCloseable;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;


/**
 * Service class for ActiveDirectoryGroupUser
 *
 * @author SSN9COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ACTIVEDIRECTORYGROUPUSER))
public class ActiveDirectoryGroupUserService extends AbstractRestService {

  /**
   * Rest web service path for ActiveDirectoryGroupUser
   */
  public static final String RWS_ACTIVEDIRECTORYGROUPUSER = "activedirectorygroupuser";


  /**
   * Create a ActiveDirectoryGroupUser record
   *
   * @param obj object to create
   * @return Rest response, with created ActiveDirectoryGroupUser object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response create(final ActiveDirectoryGroupUser obj) throws IcdmException {
    ActiveDirectoryGroupUserCommand cmd = new ActiveDirectoryGroupUserCommand(getServiceData(), obj, false, false);
    executeCommand(cmd);
    ActiveDirectoryGroupUser ret = cmd.getNewData();
    getLogger().info("Created ActiveDirectoryGroupUser Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Update a ActiveDirectoryGroupUser record
   *
   * @param obj object to update
   * @return Rest response, with updated ActiveDirectoryGroupUser object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final ActiveDirectoryGroupUser obj) throws IcdmException {
    ActiveDirectoryGroupUserCommand cmd = new ActiveDirectoryGroupUserCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    ActiveDirectoryGroupUser ret = cmd.getNewData();
    getLogger().info("Updated ActiveDirectoryGroupUser Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a ActiveDirectoryGroupUser record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    ActiveDirectoryGroupUserLoader loader = new ActiveDirectoryGroupUserLoader(getServiceData());
    ActiveDirectoryGroupUser obj = loader.getDataObjectByID(objId);
    ActiveDirectoryGroupUserCommand cmd = new ActiveDirectoryGroupUserCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

  /**
   * Get all TabvApicAdGrpUsersDetail records
   *
   * @param adGroupId group ID
   * @return Rest response, with Map of ApicAdGrpUsersDetail objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_AD_GRP_USERS_BY_GROUP_ID)
  @CompressData
  public Response getByGroupId(@QueryParam(WsCommonConstants.RWS_QP_AD_GROUP_ID) final long adGroupId)
      throws IcdmException {
    ActiveDirectoryGroupUserLoader loader = new ActiveDirectoryGroupUserLoader(getServiceData());
    List<ActiveDirectoryGroupUser> retList = loader.getByADGroupId(adGroupId);
    try (LdapAuthenticationWrapperCloseable wrapper = new LdapAuthenticationWrapperCloseable()) {
      // set name & department from LDAP Connection
      retList.forEach(object -> {
        try {
          UserInfo info = wrapper.getUserDetails(object.getUsername());
          object.setGroupUserDept(info.getDepartment());
          object.setGroupUserName(info.getSurName() + ", " + info.getGivenName());
        }
        catch (LdapException e) {
          getLogger().error("Error Occured when fetching info from LDAP", e);
        }
      });
    }
    getLogger().info("TabvApicAdGrpUsersDetail getByADGroupId completed. Total records = {}", retList.size());
    return Response.ok(retList).build();
  }
}
