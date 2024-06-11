/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
import com.bosch.caltool.icdm.bo.user.UserCommand;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author NIP4COB
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_APIC + "/" + WsCommonConstants.RWS_USER)
public class UserService extends AbstractRestService {

  /**
   * @param userId userId
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getById(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long userId) throws IcdmException {
    UserLoader loader = new UserLoader(getServiceData());
    User user = loader.getDataObjectByID(userId);
    return Response.ok(user).build();
  }

  /**
   * @param includeMonicaAuditor boolean
   * @return all users
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAll(
      @QueryParam(value = WsCommonConstants.RWS_INCLUDE_MONICA_AUDITOR) final boolean includeMonicaAuditor)
      throws IcdmException {

    // Create loader object
    UserLoader loader = new UserLoader(getServiceData());
    SortedSet<User> retMap = loader.getAllApicUsers(includeMonicaAuditor);

    getLogger().info("UserService.getAll() completed. Number of Users = {}", retMap.size());

    return Response.ok(retMap).build();
  }

  /**
   * @param userNameList list of Username
   * @return response
   * @throws IcdmException dataexception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_APIC_USER_BY_USERNAME)
  @CompressData
  public Response getApicUserByUsername(final List<String> userNameList) throws IcdmException {

    Map<String, User> apicUserMap = new HashMap<>();
    UserLoader loader = new UserLoader(getServiceData());
    for (String username : userNameList) {
      apicUserMap.put(username, loader.getDataObjectByUserName(username));
    }

    return Response.ok(apicUserMap).build();
  }

  /**
   * Get Current user details
   *
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_APIC_CURRENT_USER)
  @CompressData
  public Response getCurrentUserDetails() throws IcdmException {

    UserLoader loader = new UserLoader(getServiceData());
    User user = loader.getDataObjectCurrentUser();
    return Response.ok(user).build();
  }

  /**
   * Get all user details
   *
   * @param nodeType NODE_TYPE
   * @param idSet node Id
   * @param includeApicWriteUsers boolean
   * @param includeMonicaAuditor boolean
   * @return response
   * @throws IcdmException dataexception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_BY_NODE)
  @CompressData
  public Response getAllUsersForNewNodeAccess(
      @QueryParam(value = WsCommonConstants.RWS_QP_NODE_TYPE) final String nodeType,
      @QueryParam(value = WsCommonConstants.RWS_QP_NODE_ID) final Set<Long> idSet,
      @QueryParam(value = WsCommonConstants.RWS_QP_INCLUDE_APIC_WRITE_USERS) final boolean includeApicWriteUsers,
      @QueryParam(value = WsCommonConstants.RWS_INCLUDE_MONICA_AUDITOR) final boolean includeMonicaAuditor)
      throws IcdmException {

    UserLoader loader = new UserLoader(getServiceData());
    SortedSet<User> ret = loader.getAllUsersForNodeAccess(nodeType, idSet, includeApicWriteUsers, includeMonicaAuditor);

    getLogger().info("UserService.getAllUsersForNewNodeAccess() completed. User count = {}", ret.size());

    return Response.ok(ret).build();
  }

  /**
   * Create new iCDM user
   *
   * @param user User
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final User user) throws IcdmException {
    UserCommand usrCmd = new UserCommand(getServiceData(), user);
    executeCommand(usrCmd);
    User ret = usrCmd.getNewData();
    WSObjectStore.getLogger().info("UserService.create completed. UserID = {}", ret.getId());
    return Response.ok(ret).build();

  }

  /**
   * updates the user
   *
   * @param user user
   * @return rest response
   * @throws IcdmException error during webservicecall
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final User user) throws IcdmException {
    UserCommand userCommand = new UserCommand(getServiceData(), user, true);
    userCommand.setUpdateTimestamp(true);
    executeCommand(userCommand);
    User retUser = userCommand.getNewData();
    return Response.ok(retUser).build();

  }
}