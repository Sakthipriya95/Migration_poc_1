/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.general;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.model.admin.NodeAccessOutput;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.NodeAccessDetailsExt;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.model.user.NodeAccessWithUserInput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class NodeAccessServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public NodeAccessServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_GEN, WsCommonConstants.RWS_NODE_ACCESS);
  }

  /**
   * Get node access details
   *
   * @param nodeType node type
   * @param nodeId Node IDs
   * @return Map - Key - Node Access ID; Value - NodeAccessDetails
   * @throws ApicWebServiceException error during service call
   */
  public NodeAccessDetails getNodeAccessDetailsByNode(final IModelType nodeType, final Long... nodeId)
      throws ApicWebServiceException {

    LOGGER.debug("Loading NodeAccessDetailsByNode map ");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_BY_PARENT)
        .queryParam(WsCommonConstants.RWS_QP_TYPE, nodeType.getTypeCode())
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, nodeId);

    NodeAccessDetails details = get(wsTarget, NodeAccessDetails.class);

    LOGGER.debug("NodeAccessDetailsByNode map loaded.");

    return details;
  }

  /**
   * Get node access details, along with LDAP User info
   *
   * @param nodeType node type
   * @param user User's NT ID
   * @param accessLevel required access level
   * @param nodeIdSet set of Node IDs
   * @return Map - Key - Node Access ID; Value - NodeAccessDetailsExt
   * @throws ApicWebServiceException error during service call
   */
  public NodeAccessDetailsExt getNodeAccessDetailsByNodeExt(final IModelType nodeType, final String user,
      final String accessLevel, final Set<Long> nodeIdSet)
      throws ApicWebServiceException {

    LOGGER.debug("Loading NodeAccessDetailsByNodeExt");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_BY_PARENT_EXT)
        .queryParam(WsCommonConstants.RWS_QP_TYPE, nodeType.getTypeCode())
        .queryParam(WsCommonConstants.RWS_QP_USER, user).queryParam(WsCommonConstants.RWS_QP_LEVEL, accessLevel);

    for (Long node : nodeIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, node);
    }

    NodeAccessDetailsExt details = get(wsTarget, NodeAccessDetailsExt.class);

    LOGGER.debug("NodeAccessDetailsByNodeExt loaded.");

    return details;
  }


  /**
   * @param user user id
   * @return the node access info
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<Long, NodeAccessInfo> findNodeAccessForGivenUser(final String user) throws ApicWebServiceException {

    LOGGER.debug("Loading all NodeAccess for current user");

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_GET_ALL_FOR_USER).queryParam(WsCommonConstants.RWS_QP_USER, user);

    GenericType<Map<Long, NodeAccessInfo>> type = new GenericType<Map<Long, NodeAccessInfo>>() {};

    Map<Long, NodeAccessInfo> response = get(wsTarget, type);

    LOGGER.debug("all NodeAccess for current user loaded : {}", response.size());

    return response;
  }

  /**
   * @param inputLabFunFile file path
   * @return the node access info
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public NodeAccessOutput findNodeAccessForFunFile(final String inputLabFunFile) throws ApicWebServiceException {
    Set<String> tempFilesSet = new HashSet<>();
    List<FormDataMultiPart> multiPartList = new ArrayList<>();
    NodeAccessOutput nodeAccessOutput = new NodeAccessOutput();
    try {

      FormDataMultiPart multipart = new FormDataMultiPart();
      multiPartList.add(multipart);

      String zippedFile = compressFile(inputLabFunFile);
      tempFilesSet.add(zippedFile);
      FileDataBodyPart reviewFilePart =
          new FileDataBodyPart(WsCommonConstants.LAB_FUN_FILE_MULTIPART, new File(zippedFile));
      // add file name to header
      reviewFilePart.getHeaders().add(WsCommonConstants.LAB_FUN_FILE_NAME, inputLabFunFile);
      multipart = (FormDataMultiPart) multipart.bodyPart(reviewFilePart);
      multiPartList.add(multipart);

      WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_NODE_BY_FUNFILE);

      nodeAccessOutput = post(wsTarget, multipart, NodeAccessOutput.class);

    }
    finally {
      closeResource(multiPartList);
      deleteFiles(tempFilesSet);
    }
    return nodeAccessOutput;
  }

  /**
   * @param nodeType nodeType
   * @param user user id
   * @param nodeName node name
   * @return the node access info
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public Map<Long, NodeAccessInfo> findNodeAccessForUserAndNodeNames(final String nodeType, final String user,
      final String nodeName)
      throws ApicWebServiceException {

    LOGGER.debug("Loading NodeAccessDetailsByNodeExt");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_NODE_BY_USER_NODENAME)
        .queryParam(WsCommonConstants.RWS_QP_TYPE, nodeType).queryParam(WsCommonConstants.RWS_QP_USER, user);


    wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_NAME, nodeName);


    GenericType<Map<Long, NodeAccessInfo>> type = new GenericType<Map<Long, NodeAccessInfo>>() {};


    Map<Long, NodeAccessInfo> nodeAccessInfoMap = get(wsTarget, type);

    LOGGER.debug("NodeAccessDetailsByNodeExt loaded.");

    return nodeAccessInfoMap;
  }

  /**
   * Get all node access details for current user
   *
   * @return Map - Key - Node ID; Value - NodeAccess
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, NodeAccess> getAllNodeAccessforCurrentUser() throws ApicWebServiceException {

    LOGGER.debug("Loading all NodeAccess for current user");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL);
    GenericType<Map<Long, NodeAccess>> type = new GenericType<Map<Long, NodeAccess>>() {};

    Map<Long, NodeAccess> response = get(wsTarget, type);

    LOGGER.debug("all NodeAccess for current user loaded : {}", response.size());

    return response;
  }

  /**
   * @return Map<Long, NodeAccessInfo>
   * @throws ApicWebServiceException exception
   */
  public Map<Long, NodeAccessInfo> getAllSpecialNodeAccess() throws ApicWebServiceException {

    LOGGER.debug("Loading all Special NodeAccess");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_ALL_SPECIAL_ACCESS);
    GenericType<Map<Long, NodeAccessInfo>> type = new GenericType<Map<Long, NodeAccessInfo>>() {};

    Map<Long, NodeAccessInfo> response = get(wsTarget, type);

    LOGGER.debug("all Special NodeAccess loaded : {}", response.size());

    return response;
  }


  /**
   * Create new NodeAccess for given user & node.
   *
   * @param nodeAccess NodeAccess
   * @return updated data NodeAccess
   * @throws ApicWebServiceException error during service call
   */
  public NodeAccess create(final NodeAccess nodeAccess) throws ApicWebServiceException {
    return create(getWsBase(), nodeAccess);
  }

  /**
   * Create new NodeAccess for given user & node.
   *
   * @param nodeAccessList NodeAccess
   * @return the map of created , updated nodes
   * @throws ApicWebServiceException error during service call
   */
  public Map<Long, NodeAccess> createUpdateMultiNode(final List<NodeAccessWithUserInput> nodeAccessList)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MULTIPLE_NODE_ACCESS_MGMT);
    GenericType<Map<Long, NodeAccess>> type = new GenericType<Map<Long, NodeAccess>>() {};
    return create(wsTarget, nodeAccessList, type);

  }


  /**
   * Update an existing NodeAccess for given user & node.
   *
   * @param nodeAccess the NodeAccess
   * @return updated data NodeAccess
   * @throws ApicWebServiceException error during service call
   */
  public NodeAccess update(final NodeAccess nodeAccess) throws ApicWebServiceException {
    return update(getWsBase(), nodeAccess);
  }

  /**
   * Delete an existing NodeAccess for given user & node.
   *
   * @param nodeAccess node access right
   * @throws ApicWebServiceException error during service call
   */
  public void delete(final NodeAccess nodeAccess) throws ApicWebServiceException {
    delete(getWsBase(), nodeAccess);
  }

  /**
   * Fetch all node access group rights for user
   *
   * @param nodeType PIDC
   * @param reqUserSet Set of User NT IDs
   * @param pidcIdSet Set of PIDC IDs
   * @return Map - Key - Node Access ID; Value - NodeAccessDetails
   * @throws ApicWebServiceException error during service call
   */
  public NodeAccessDetails getAdGroupDetails(final String nodeType, final Set<String> reqUserSet,
      final Set<Long> pidcIdSet)
      throws ApicWebServiceException {

    LOGGER.debug("Loading NodeAccessDetailsByNode map ");

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_AD_GROUP_DETAILS)
        .queryParam(WsCommonConstants.RWS_QP_TYPE, nodeType);

    for (String user : reqUserSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_USERNAME, user);
    }

    for (Long node : pidcIdSet) {
      wsTarget = wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, node);
    }

    NodeAccessDetails details = get(wsTarget, NodeAccessDetails.class);

    LOGGER.debug("Fetched all node access group rights for user");

    return details;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected <O> String getObjectIdentifier(final O model) {
    NodeAccess acc = (NodeAccess) model;
    return "Node Type = '" + acc.getNodeType() + "', Node ID = '" + acc.getNodeId() + "'";
  }
}
