/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.general;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.db.WSObjectStore;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.AbstractCommand.COMMAND_MODE;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupNodeAccessLoader;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupUserLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccInfoFactory;
import com.bosch.caltool.icdm.bo.user.NodeAccessCommand;
import com.bosch.caltool.icdm.bo.user.NodeAccessInfoResolver;
import com.bosch.caltool.icdm.bo.user.NodeAccessLevel;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.ParserLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.admin.NodeAccessOutput;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.NodeAccessDetailsExt;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.model.user.NodeAccessWithUserInput;
import com.bosch.caltool.labfunparser.exception.ParserException;
import com.bosch.caltool.labfunparser.textparser.InputFileParser;


/**
 * @author bne4cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_GEN + "/" + WsCommonConstants.RWS_NODE_ACCESS)
public class NodeAccessService extends AbstractRestService {

  /**
   * Fetch node access rights for given node id(s)
   *
   * @param nodeType Node Type. Mandatory
   * @param reqUserSet required user's NT ID
   * @param accLevel access level - OWNER, GRANT, WRITE, READ
   * @param idSet set of node IDs. Optional. If not provided, access rights of all nodes, for the given type, will be
   *          returned
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_BY_PARENT)
  @CompressData
  public Response findByNodeId(@QueryParam(value = WsCommonConstants.RWS_QP_TYPE) final String nodeType,
      @QueryParam(value = WsCommonConstants.RWS_QP_USERNAME) final Set<String> reqUserSet,
      @QueryParam(value = WsCommonConstants.RWS_QP_LEVEL) final String accLevel,
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> idSet)
      throws IcdmException {

    // Find node access details
    NodeAccessDetails ret = (new NodeAccessLoader(getServiceData())).getNodeAccessByNodeAndUserSet(nodeType, reqUserSet,
        NodeAccessLevel.getType(accLevel), idSet);

    ActiveDirectoryGroupNodeAccessLoader activeDirectoryGroupNodeAccessLoader =
        new ActiveDirectoryGroupNodeAccessLoader(getServiceData());

    for (Long id : idSet) {

      Map<Long, List<ActiveDirectoryGroupNodeAccess>> tempMap = activeDirectoryGroupNodeAccessLoader.getByNodeId(id);

      Map<Long, Set<ActiveDirectoryGroupNodeAccess>> convertedMap = new HashMap<>();

      for (Map.Entry<Long, List<ActiveDirectoryGroupNodeAccess>> entry : tempMap.entrySet()) {
        List<ActiveDirectoryGroupNodeAccess> accessList = entry.getValue();
        convertedMap.put(entry.getKey(), new HashSet<>(accessList));
      }

      ret.getNodeAccessADGrpMap().putAll(convertedMap);
    }

    // for each group in map - get list of users
    ActiveDirectoryGroupUserLoader activeDirectoryGroupUserLoader =
        new ActiveDirectoryGroupUserLoader(getServiceData());
    Set<ActiveDirectoryGroupNodeAccess> allSets = new HashSet<>();

    // Iterate over the values of nodeAccessADGrpMap
    for (Set<ActiveDirectoryGroupNodeAccess> accessSet : ret.getNodeAccessADGrpMap().values()) {
      allSets.addAll(accessSet); // Add the current set to the new set
    }
    UserLoader userLoader = new UserLoader(getServiceData());

    for (ActiveDirectoryGroupNodeAccess groupNodeAccess : allSets) {

      List<ActiveDirectoryGroupUser> retList =
          activeDirectoryGroupUserLoader.getByADGroupId(groupNodeAccess.getAdGroup().getId());


      for (ActiveDirectoryGroupUser user : retList) {
        Long userId = userLoader.getUserIdByUserName(user.getUsername());
        user.setUserId(userId);
      }

      ret.getNodeAccessADGrpUsersMap().put(groupNodeAccess.getAdGroup().getId(), new HashSet<>(retList));

    }


    getLogger().info("NodeAccessService.findByNodeId() completed. Node access count = {}",
        ret.getNodeAccessMap().size());

    return Response.ok(ret).build();

  }

  /**
   * Fetch node access rights for given node id(s). Returns LDAP user info also
   *
   * @param nodeType Node Type. Mandatory
   * @param reqUser required user's NT ID
   * @param accLevel access level - OWNER, GRANT, WRITE, READ
   * @param idSet set of node IDs. Optional. If not provided, access rights of all nodes, for the given type, will be
   *          returned
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_BY_PARENT_EXT)
  @CompressData
  public Response findByNodeIdExt(@QueryParam(value = WsCommonConstants.RWS_QP_TYPE) final String nodeType,
      @QueryParam(value = WsCommonConstants.RWS_QP_USER) final String reqUser,
      @QueryParam(value = WsCommonConstants.RWS_QP_LEVEL) final String accLevel,
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> idSet)
      throws IcdmException {

    // Find all node access extended details
    NodeAccessDetailsExt ret = (new NodeAccessLoader(getServiceData())).getAllNodeAccessByNodeExt(nodeType, reqUser,
        NodeAccessLevel.getType(accLevel), idSet);

    getLogger().info("NodeAccessService.findByNodeIdExt() completed. Nodes count = {}", ret.getNodeAccessMap().size());

    return Response.ok(ret).build();
  }


  /**
   * Fetch node access rights for given node id(s). Returns LDAP user info also
   *
   * @param typeCode Node Type. Mandatory
   * @param reqUser required user's NT ID
   * @param nodeName pidc/function/ rule set/ Questionnaire name
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_NODE_BY_USER_NODENAME)
  @CompressData
  public Response findNodeAccessForUserAndNodeNames(
      @QueryParam(value = WsCommonConstants.RWS_QP_TYPE) final String typeCode,
      @QueryParam(value = WsCommonConstants.RWS_QP_USER) final String reqUser,
      @QueryParam(value = WsCommonConstants.RWS_QP_NAME) final String nodeName)
      throws IcdmException {
    List<String> nodeNameList = new ArrayList<>();
    nodeNameList.add(nodeName.replace("\\*", "\\%"));
    NodeAccessInfoResolver resolver =
        new NodeAccInfoFactory(getServiceData()).createNodeAccInfoResObject(typeCode, nodeNameList, reqUser);
    Map<Long, NodeAccessInfo> ret = resolver.resNodeAccInfoForUser();
    return Response.ok(ret).build();
  }


  /**
   * Import function nodes from LAB/FUN files
   *
   * @param multiPart as input
   * @return Map<Long, NodeAccessInfo>
   * @throws IcdmException as exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_GET_NODE_BY_FUNFILE)
  @CompressData
  public Response findNodeAccessUsingFunFile(final FormDataMultiPart multiPart) throws IcdmException {
    NodeAccessOutput nodeAccessOutput = new NodeAccessOutput();
    try {
      FormDataBodyPart formDataBodyPart = multiPart.getField(WsCommonConstants.LAB_FUN_FILE_MULTIPART);
      if (formDataBodyPart != null) {
        String filePath = formDataBodyPart.getHeaders().get(WsCommonConstants.LAB_FUN_FILE_NAME).get(0);
        InputStream unzipIfZippedStream = ZipUtils.unzipIfZipped(formDataBodyPart.getValueAs(InputStream.class));
        byte[] inputFileBytes = IOUtils.toByteArray(unzipIfZippedStream);
        InputStream fileInputStream = new ByteArrayInputStream(inputFileBytes);
        // parser for fun and lab file
        InputFileParser labFunParser = new InputFileParser(ParserLogger.getInstance(), fileInputStream, filePath);
        // parser
        labFunParser.parse();
        // returns functions
        List<String> functions = labFunParser.getFunctions();
        // to replace all the * with % for function search
        functions.replaceAll(val -> val.replace("\\*", "\\%"));

        NodeAccessInfoResolver resolver = new NodeAccInfoFactory(getServiceData())
            .createNodeAccInfoResObject(MODEL_TYPE.CDR_FUNCTION.getTypeCode(), functions, null);

        nodeAccessOutput.setNodeAccessInfoMap(resolver.resNodeAccInfoForUser());
        nodeAccessOutput.setMissingNodes(resolver.getMissingNodes());
      }
    }
    catch (IOException | ParserException exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }

    return Response.ok(nodeAccessOutput).build();
  }

  /**
   * Fetch all node access rights for given user
   *
   * @param reqUser nt user id
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_FOR_USER)
  @CompressData
  public Response getAllNodeAccessForGivenUser(@QueryParam(value = WsCommonConstants.RWS_QP_USER) final String reqUser)
      throws IcdmException {
    // Find all node access for current user

    NodeAccessLoader nodeAcessLoader = new NodeAccessLoader(getServiceData());
    Map<Long, NodeAccessInfo> nodeAccInfoMap = nodeAcessLoader.getAllNodeAccessInfoForGivenUser(reqUser);

    getLogger().info("NodeAccessService.getAllNodeAccessForCurrentUser() completed. Nodes count = {}",
        nodeAccInfoMap.size());

    return Response.ok(nodeAccInfoMap).build();
  }

  /**
   * @return nodeAccInfoMap
   * @throws IcdmException exception
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL_SPECIAL_ACCESS)
  @CompressData
  public Response getAllSpecialNodeAccess() throws IcdmException {
    NodeAccessLoader nodeAcessLoader = new NodeAccessLoader(getServiceData());
    Map<Long, NodeAccessInfo> nodeAccInfoMap = nodeAcessLoader.getAllSpecialNodeAccess();

    getLogger().info("NodeAccessService.getAllSpecialNodeAccess() completed. Nodes count = {}", nodeAccInfoMap.size());

    return Response.ok(nodeAccInfoMap).build();
  }


  /**
   * Fetch all node access rights for current user
   *
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_ALL)
  @CompressData
  public Response getAllNodeAccessForCurrentUser() throws IcdmException {


    // Find all node access for current user
    Map<Long, NodeAccess> ret = new NodeAccessLoader(getServiceData()).getAllNodeAccessForCurrentUser();

    getLogger().info("NodeAccessService.getAllNodeAccessForCurrentUser() completed. Nodes count = {}", ret.size());

    return Response.ok(ret).build();
  }

  /**
   * Create a Node Access rigths for the given user under given Node
   *
   * @param nodeAccess NodeAccess details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response create(final NodeAccess nodeAccess) throws IcdmException {

    NodeAccessCommand cmd = new NodeAccessCommand(getServiceData(), nodeAccess);
    executeCommand(cmd);

    NodeAccess ret = cmd.getNewData();

    WSObjectStore.getLogger().info("NodeAccessService.create completed. NodeID = {} UserID = {}", ret.getNodeId(),
        ret.getUserId());

    return Response.ok(ret).build();
  }


  /**
   * Create/Update a Node Access rigths for the given user under given Node
   *
   * @param nodeAccessInputList nodeAccessUserInputList
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_MULTIPLE_NODE_ACCESS_MGMT)
  @CompressData
  public Response manageMultiNodeAccess(final List<NodeAccessWithUserInput> nodeAccessInputList) throws IcdmException {

    if ((nodeAccessInputList == null) || nodeAccessInputList.isEmpty()) {
      throw new IcdmException("No node access available for creation/updation of node access");
    }

    List<NodeAccess> nodeAccessTobeCreated = new ArrayList<>();
    List<NodeAccess> nodeAccessTobeUpdated = new ArrayList<>();
    List<NodeAccess> nodeAccessTobeDeleted = new ArrayList<>();
    Map<Long, Map<Long, NodeAccess>> nodeAccessUserCache = new HashMap<>();


    for (NodeAccessWithUserInput nodeAccessInput : nodeAccessInputList) {
      addNodeAccesToList(nodeAccessTobeCreated, nodeAccessTobeUpdated, nodeAccessTobeDeleted, nodeAccessUserCache,
          nodeAccessInput);
    }

    List<AbstractSimpleCommand> nodeAccessCmdList = new ArrayList<>();
    for (NodeAccess createNew : nodeAccessTobeCreated) {
      NodeAccessCommand cmd = new NodeAccessCommand(getServiceData(), createNew);
      nodeAccessCmdList.add(cmd);
    }

    for (NodeAccess updateNode : nodeAccessTobeUpdated) {
      NodeAccessCommand cmd = new NodeAccessCommand(getServiceData(), updateNode, true);
      nodeAccessCmdList.add(cmd);
    }

    for (NodeAccess deleteNode : nodeAccessTobeDeleted) {
      NodeAccessCommand cmd = new NodeAccessCommand(getServiceData(), deleteNode, false);
      nodeAccessCmdList.add(cmd);
    }

    executeCommand(nodeAccessCmdList);

    Map<Long, NodeAccess> nodeAccessOut = new HashMap<>();
    for (AbstractSimpleCommand abstractSimpleCommand : nodeAccessCmdList) {
      NodeAccessCommand command = (NodeAccessCommand) abstractSimpleCommand;
      if ((command.getCmdMode() == COMMAND_MODE.CREATE) || (command.getCmdMode() == COMMAND_MODE.UPDATE)) {
        nodeAccessOut.put(command.getObjId(), command.getNewData());
      }
    }

    getLogger().info("NodeAccessService.createMultiNodes. Access Ceated Modified {} {} {}",
        nodeAccessTobeCreated.size(), nodeAccessTobeUpdated.size(), nodeAccessTobeDeleted.size());

    return Response.ok(nodeAccessOut).build();
  }

  /**
   * @param nodeAccessTobeCreated
   * @param nodeAccessTobeUpdated
   * @param nodeAccessTobeDeleted
   * @param nodeAccessUserCache
   * @param nodeAccessInput
   * @throws DataException
   * @throws UnAuthorizedAccessException
   */
  private void addNodeAccesToList(final List<NodeAccess> nodeAccessTobeCreated,
      final List<NodeAccess> nodeAccessTobeUpdated, final List<NodeAccess> nodeAccessTobeDeleted,
      final Map<Long, Map<Long, NodeAccess>> nodeAccessUserCache, final NodeAccessWithUserInput nodeAccessInput)
      throws DataException, UnAuthorizedAccessException {
    // Existing node access.
// Get all the node access for the user in input
    Map<Long, NodeAccess> currentUserNodeMap = nodeAccessUserCache.get(nodeAccessInput.getNodeAccess().getUserId());
    if (currentUserNodeMap == null) {
      // Data to be fetched from Db
      // currentUserNodeMap
      // Map<Long, NodeAccess> allNodeAccessForUser =

      // add it to the current User node map
      currentUserNodeMap =
          new NodeAccessLoader(getServiceData()).getAllNodeAccessForUser((nodeAccessInput.getNodeAccess().getUserId()));

      // Add it to general cache
      nodeAccessUserCache.put(nodeAccessInput.getNodeAccess().getUserId(), currentUserNodeMap);
    }


    boolean isNewNode = true;
    NodeAccess newNodeAccess = nodeAccessInput.getNodeAccess();
    for (NodeAccess existingNode : currentUserNodeMap.values()) {
      NodeAccess clonedAccess = existingNode.clone();

      if (existingNode.getNodeId().equals(newNodeAccess.getNodeId())) {

        if (nodeAccessInput.isDelete()) {
          nodeAccessTobeDeleted.add(clonedAccess);
        }
        else {
          nodeAccessTobeUpdated.add(clonedAccess);
          clonedAccess.setRead(newNodeAccess.isRead());
          clonedAccess.setWrite(newNodeAccess.isWrite());
          clonedAccess.setGrant(newNodeAccess.isGrant());
          clonedAccess.setOwner(newNodeAccess.isOwner());

        }
        isNewNode = false;
      }
    }
    if (isNewNode) {
      nodeAccessTobeCreated.add(newNodeAccess);
    }
  }

  /**
   * Update existing Node Access rigths for the given user under given Node
   *
   * @param nodeAccess NodeAccess details
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response update(final NodeAccess nodeAccess) throws IcdmException {

    NodeAccessCommand cmd = new NodeAccessCommand(getServiceData(), nodeAccess, true);
    executeCommand(cmd);

    NodeAccess ret = cmd.getNewData();

    WSObjectStore.getLogger().info("NodeAccessService.update completed. NodeID = {} UserID = {}", ret.getNodeId(),
        ret.getUserId());

    return Response.ok(ret).build();
  }

  /**
   * Delete existing Node Access rigths for the given user under given Node
   *
   * @param nodeAccessId NodeAccess ID
   * @return Rest response
   * @throws IcdmException error during execution
   */
  @DELETE
  @Produces({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response delete(@QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Long nodeAccessId)
      throws IcdmException {

    NodeAccessCommand cmd = new NodeAccessCommand(getServiceData(),
        new NodeAccessLoader(getServiceData()).getDataObjectByID(nodeAccessId), false);
    executeCommand(cmd);
    WSObjectStore.getLogger().info("NodeAccessService.delete completed. NodeAccessID = {}", nodeAccessId);

    return Response.ok().build();
  }

  /**
   * Fetch all node access group rights for user
   *
   * @param nodeType PIDC
   * @param reqUserSet Set of User NT IDs
   * @param pidcIdSet Set of PIDC IDs
   * @return Rest response
   * @throws IcdmException if input data is invalid
   */
  @GET
  @Produces({ MediaType.APPLICATION_JSON })
  @Path(WsCommonConstants.RWS_GET_AD_GROUP_DETAILS)
  @CompressData
  public Response getAdGroupDetails(@QueryParam(value = WsCommonConstants.RWS_QP_TYPE) final String nodeType,
      @QueryParam(value = WsCommonConstants.RWS_QP_USERNAME) final Set<String> reqUserSet,
      @QueryParam(value = WsCommonConstants.RWS_QP_OBJ_ID) final Set<Long> pidcIdSet)
      throws IcdmException {
    ActiveDirectoryGroupNodeAccessLoader loader = new ActiveDirectoryGroupNodeAccessLoader(getServiceData());
    Set<String> userSet = new HashSet<>();
    // Find all node access Groups for user
    Map<Long, Set<ActiveDirectoryGroupNodeAccess>> nodeAdGrpMap;
    if (CommonUtils.isNotEmpty(reqUserSet)) {
      userSet.addAll(reqUserSet.stream().map(String::toUpperCase).collect(Collectors.toSet()));
      nodeAdGrpMap = loader.getAdGrpNodeAccessByUsernames(userSet, pidcIdSet, nodeType);
    }
    else if (CommonUtils.isNotEmpty(pidcIdSet)) {
      nodeAdGrpMap = loader.getAdGrpNodeAccessByPidcIds(pidcIdSet);
    }
    else {
      nodeAdGrpMap = loader.getAllAdGrpNodeAccess(nodeType);
    }

    NodeAccessDetails nodeAccessDetails = new NodeAccessDetails();
    nodeAccessDetails.getNodeAccessADGrpMap().putAll(nodeAdGrpMap);

    // for each group in map - get list of users
    ActiveDirectoryGroupUserLoader activeDirectoryGroupUserLoader =
        new ActiveDirectoryGroupUserLoader(getServiceData());
    Set<ActiveDirectoryGroupNodeAccess> allSets = new HashSet<>();

    // Iterate over the values of nodeAccessADGrpMap
    for (Set<ActiveDirectoryGroupNodeAccess> accessSet : nodeAccessDetails.getNodeAccessADGrpMap().values()) {
      allSets.addAll(accessSet); // Add the current set to the new set
    }
    UserLoader userLoader = new UserLoader(getServiceData());

    for (ActiveDirectoryGroupNodeAccess groupNodeAccess : allSets) {

      List<ActiveDirectoryGroupUser> retList =
          activeDirectoryGroupUserLoader.getByADGroupId(groupNodeAccess.getAdGroup().getId());

      List<ActiveDirectoryGroupUser> filteredUsersList = new ArrayList<>();

      for (ActiveDirectoryGroupUser user : retList) {
        if (userSet.isEmpty() || userSet.contains(user.getUsername().toUpperCase())) {
          Long userId = userLoader.getUserIdByUserName(user.getUsername());
          user.setUserId(userId);
          filteredUsersList.add(user);
        }
      }

      nodeAccessDetails.getNodeAccessADGrpUsersMap().put(groupNodeAccess.getAdGroup().getId(),
          new HashSet<>(filteredUsersList));

    }

    getLogger().info("NodeAccessService.getAdGroupDetails() completed. Node access count = {}",
        nodeAccessDetails.getNodeAccessADGrpMap().size());

    return Response.ok(nodeAccessDetails).build();

  }
}
