package com.bosch.caltool.apic.ws.rest.service.general;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NoResultException;
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
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupCommand;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupLoader;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupNodeAccessCommand;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupNodeAccessLoader;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupUserCommand;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupUserLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroup;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;


/**
 * Service class for ActiveDirectoryGroupNodeAccess
 *
 * @author SSN9COB
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_ACTIVEDIRECTORYGROUPNODEACCESS))
public class ActiveDirectoryGroupNodeAccessService extends AbstractRestService {

  /**
   * Rest web service path for ActiveDirectoryGroupNodeAccess
   */
  public static final String RWS_ACTIVEDIRECTORYGROUPNODEACCESS = "activedirectorygroupnodeaccess";

  /**
   * Update a ActiveDirectoryGroupNodeAccess record
   *
   * @param obj object to update
   * @return Rest response, with updated ActiveDirectoryGroupNodeAccess object
   * @throws IcdmException exception while invoking service
   */
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @CompressData
  public Response update(final ActiveDirectoryGroupNodeAccess obj) throws IcdmException {
    ActiveDirectoryGroupNodeAccessCommand cmd =
        new ActiveDirectoryGroupNodeAccessCommand(getServiceData(), obj, true, false);
    executeCommand(cmd);
    ActiveDirectoryGroupNodeAccess ret = cmd.getNewData();
    getLogger().info("Updated ActiveDirectoryGroupNodeAccess Id : {}", ret.getId());
    return Response.ok(ret).build();
  }

  /**
   * Delete a ActiveDirectoryGroupNodeAccess record
   *
   * @param objId id of object to delete
   * @return Empty Rest response
   * @throws IcdmException exception while invoking service
   */
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response delete(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    ActiveDirectoryGroupNodeAccessLoader loader = new ActiveDirectoryGroupNodeAccessLoader(getServiceData());
    ActiveDirectoryGroupNodeAccess obj = loader.getDataObjectByID(objId);
    ActiveDirectoryGroupNodeAccessCommand cmd =
        new ActiveDirectoryGroupNodeAccessCommand(getServiceData(), obj, false, true);
    executeCommand(cmd);
    return Response.ok().build();
  }

  /**
   * Create a TActiveDirectoryGroup record & TActiveDirectoryGroupNodeAccess record
   *
   * @param adGroupName groupName to create
   * @param adGroupSID GroupSID
   * @param nodeId NodeId to Assign Group Access
   * @param nodeType node Type
   * @return Rest response, with created ActiveDirectoryGroup object
   * @throws IcdmException exception while invoking service
   */
  @POST
  @Path(WsCommonConstants.RWS_APICCREATEADGROUPACCESS)
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(@QueryParam(WsCommonConstants.RWS_PIDCADGRPDETAIL) final String adGroupName,
      @QueryParam(WsCommonConstants.RWS_PIDCADGRPSID) final String adGroupSID,
      @QueryParam(WsCommonConstants.RWS_PIDCADGRPACCESSNODEID) final long nodeId,
      @QueryParam(WsCommonConstants.RWS_PIDCADGRPACCESSNODETYPE) final String nodeType)
      throws IcdmException {
    ActiveDirectoryGroupNodeAccess nodeAccess = getNodeAccesWithGroupDetails(adGroupName, adGroupSID);
    // if group is not assigned already
    if (!checkIfGroupAssignedToNode(nodeId, nodeAccess)) {
      nodeAccess.setNodeId(nodeId);
      nodeAccess.setNodeType(nodeType);
      nodeAccess.setRead(true);
      ActiveDirectoryGroupNodeAccessCommand nodeAccessCmd =
          new ActiveDirectoryGroupNodeAccessCommand(getServiceData(), nodeAccess, false, false);
      executeCommand(nodeAccessCmd);
      ActiveDirectoryGroupNodeAccess nodeAccessModel = nodeAccessCmd.getNewData();
      getLogger().info("Created TActiveDirectoryGroupNodeAccess Id : {}", nodeAccessModel.getId());
      return Response.ok(nodeAccessModel).build();
    }
    // Group already assigned - throw icdm exception
    throw new IcdmException("ACCESS_RIGHTS.GROUP_ALREADY_ASSIGNED", nodeAccess.getAdGroup().getGroupName());

  }

  /**
   * @param nodeId
   * @param nodeAccess
   * @return
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private boolean checkIfGroupAssignedToNode(final long nodeId, final ActiveDirectoryGroupNodeAccess nodeAccess)
      throws DataException, UnAuthorizedAccessException {
    // Check if group is already assigned to the node
    try {
      ActiveDirectoryGroupNodeAccess existingAccess = new ActiveDirectoryGroupNodeAccessLoader(getServiceData())
          .getByNodeIdAndGroupId(nodeId, nodeAccess.getAdGroup().getId());
      if (existingAccess != null) {
        // Group already assigned. return the group already assigned
        return true;
      }
    }
    catch (NoResultException e) {
      // group not assigned already
      return false;
    }
    return false;
  }

  /**
   * @param adGroupName
   * @param adGroupSID
   * @return
   * @throws DataException
   * @throws UnAuthorizedAccessException
   * @throws IcdmException
   */
  private ActiveDirectoryGroupNodeAccess getNodeAccesWithGroupDetails(final String adGroupName, final String adGroupSID)
      throws IcdmException {
    ActiveDirectoryGroupNodeAccess nodeAccess = new ActiveDirectoryGroupNodeAccess();
    boolean isGroupExists = false;
    // Check if group already exists in DB
    try {
      ActiveDirectoryGroup groupModel = new ActiveDirectoryGroupLoader(getServiceData()).getByGroupName(adGroupName);
      if ((groupModel != null) && (groupModel.getId() != null)) {
        // Group already exists. Assign group id to Node Access object
        isGroupExists = true;
        getLogger().info("Existing TActiveDirectoryGroup Id : {}", groupModel.getId());
        nodeAccess.setAdGroup(groupModel);
      }
    }
    catch (NoResultException e) {
      // Group not available in DB
      isGroupExists = false;
    }
    if (!isGroupExists) {
      // insert the new group details
      ActiveDirectoryGroup groupModel = createNewActiveDirectoryGroupEntry(adGroupName, adGroupSID);
      nodeAccess.setAdGroup(groupModel);
      // Insert the users list into TActiveDirectoryGroupUser table
      insertIntoGroupUsersTable(adGroupName);
    }
    return nodeAccess;
  }

  /**
   * @param adGroupName
   * @param grpLoader
   * @throws IcdmException
   */
  private void insertIntoGroupUsersTable(final String adGroupName) throws IcdmException {
    /*
     * Fetch list of users for the entered active directory group
     */
    LdapAuthenticationWrapper wrapper = new LdapAuthenticationWrapper();
    Set<String> userNames;
    try {
      userNames = wrapper.searchByDisplayName(adGroupName);// TODO Change to searchByGroupSID
      /*
       * Get the Tactivedirectorygroup and insert into TActiveDirectoryGroupUser entity for all users
       */
      userNames.forEach(user -> {
        try {
          ActiveDirectoryGroupLoader grpLoader = new ActiveDirectoryGroupLoader(getServiceData());
          ActiveDirectoryGroupUserLoader grpUserLoader = new ActiveDirectoryGroupUserLoader(getServiceData());

          ActiveDirectoryGroupUserCommand groupUserCmd = new ActiveDirectoryGroupUserCommand(getServiceData(),
              grpUserLoader.createDataObjectFromFields(grpLoader.getByGroupName(adGroupName), user), false, false);
          executeCommand(groupUserCmd);
        }
        catch (IcdmException e) {
          getLogger().error("Error Occured when fetching ActiveDirectoryGroupUser Info", e);
        }

      });
    }
    catch (LdapException e) {
      throw new IcdmException(e.getLocalizedMessage(), e);
    }
  }

  /**
   * @param adGroupName
   * @param adGroupSID SID
   * @return
   * @throws IcdmException
   */
  private ActiveDirectoryGroup createNewActiveDirectoryGroupEntry(final String adGroupName, final String adGroupSID)
      throws IcdmException {
    ActiveDirectoryGroup groupModel;
    ActiveDirectoryGroup newGroupModel = new ActiveDirectoryGroup();
    newGroupModel.setGroupName(adGroupName);
    newGroupModel.setGroupSid(adGroupSID);
    ActiveDirectoryGroupCommand groupDetailCmd =
        new ActiveDirectoryGroupCommand(getServiceData(), newGroupModel, false, false, false);
    executeCommand(groupDetailCmd);
    groupModel = groupDetailCmd.getNewData();
    getLogger().info("Created TActiveDirectoryGroup Id : {}", groupModel.getId());
    return groupModel;
  }

  /**
   * Get all TActiveDirectoryGroupNodeAccess records
   *
   * @param nodeId node ID
   * @return Rest response, with Map of ApicAdGrpUsersDetail objects
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_GET_AD_GRP_ACCESS_BY_NODE_ID)
  @CompressData
  public Response getByNodeId(@QueryParam(WsCommonConstants.RWS_QP_NODE_ID) final long nodeId) throws IcdmException {
    ActiveDirectoryGroupNodeAccessLoader loader = new ActiveDirectoryGroupNodeAccessLoader(getServiceData());
    Map<Long, List<ActiveDirectoryGroupNodeAccess>> retMap = loader.getByNodeId(nodeId);
    getLogger().info("TActiveDirectoryGroupNodeAccess getByNodeId completed. Total records = {}",
        retMap.values().size());
    return Response.ok(retMap).build();
  }
}
