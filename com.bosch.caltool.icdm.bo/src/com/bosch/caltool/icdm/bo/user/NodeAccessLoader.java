/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Query;

import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupNodeAccessLoader;
import com.bosch.caltool.icdm.bo.general.ActiveDirectoryGroupUserLoader;
import com.bosch.caltool.icdm.bo.general.CommonParamLoader;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapperCloseable;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TActiveDirectoryGroup;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicNodeAccess;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.LdapUserInfo;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.NodeAccessDetailsExt;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author bne4cob
 */
public class NodeAccessLoader extends AbstractBusinessObject<NodeAccess, TabvApicNodeAccess> {


  /**
   *
   */
  public static final String SESSKEY_CUR_USER_NODE_ACCESS = "CUR_USER_NODE_ACCESS";

  /**
   * @param serviceData service Data
   */
  public NodeAccessLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.NODE_ACCESS, TabvApicNodeAccess.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected NodeAccess createDataObject(final TabvApicNodeAccess entity) throws DataException {
    NodeAccess access = new NodeAccess();

    setCommonFields(access, entity);

    User user = new UserLoader(getServiceData()).getDataObjectByID(entity.getTabvApicUser().getUserId());
    access.setUserId(user.getId());
    access.setName(user.getName());
    access.setDescription(user.getDescription());

    access.setNodeId(entity.getNodeId());
    access.setNodeType(entity.getNodeType());

    if (CommonUtilConstants.CODE_YES.equals(entity.getOwner())) {
      access.setOwner(true);
      access.setGrant(true);
      access.setWrite(true);
      access.setRead(true);
    }
    else if (CommonUtilConstants.CODE_YES.equals(entity.getGrantright())) {
      access.setOwner(false);
      access.setGrant(true);
      access.setWrite(true);
      access.setRead(true);
    }
    else if (CommonUtilConstants.CODE_YES.equals(entity.getWriteright())) {
      access.setOwner(false);
      access.setGrant(false);
      access.setWrite(true);
      access.setRead(true);
    }
    else if (CommonUtilConstants.CODE_YES.equals(entity.getReadright())) {
      access.setOwner(false);
      access.setGrant(false);
      access.setWrite(false);
      access.setRead(true);
    }

    return access;
  }

  /**
   * @param userName User NT ID
   * @return map of node access. Key - Node ID, Value - data object
   * @throws DataException if object cannot be created
   */
  public Map<Long, NodeAccess> getAllNodeAccessForUser(final String userName) throws DataException {
    TabvApicUser dbUser = new UserLoader(getServiceData()).getEntityObjectByUserName(userName);
    if (dbUser == null) {
      dbUser = new TabvApicUser();
      dbUser.setUsername(userName);
    }
    return getNodeAccess(dbUser);
  }

  /**
   * @param userNames Set of User NT ID
   * @return map of node access. Key - Node ID, Value - data object
   * @throws DataException if object cannot be created
   */
  public Map<Long, NodeAccess> getAllNodeAccessForSetOfUsers(final Set<String> userNames) throws DataException {
    Map<String, TabvApicUser> dbUserMap = new UserLoader(getServiceData()).getEntityObjectsByUserNameSet(userNames);
    Map<Long, NodeAccess> nodeAccessMap = new HashMap<>();
    dbUserMap.values().forEach(user -> {
      try {
        nodeAccessMap.putAll(getNodeAccess(user));
      }
      catch (DataException e) {
        getLogger().error("Error occured on getAllNodeAccessForSetOfUsers", e);
      }
    });
    return nodeAccessMap;
  }

  /**
   * @param userId User ID
   * @return map of node access. Key - Node ID, Value - data object
   * @throws DataException if object cannot be created
   */
  public Map<Long, NodeAccess> getAllNodeAccessForUser(final Long userId) throws DataException {
    TabvApicUser dbUser = new UserLoader(getServiceData()).getEntityObject(userId);
    return getNodeAccess(dbUser);
  }

  /**
   * @param dbUser DB User entity
   * @return map of node access. Key - Node ID, Value - data object
   * @throws DataException if object cannot be created
   */
  private Map<Long, NodeAccess> getNodeAccess(final TabvApicUser dbUser) throws DataException {
    Map<Long, NodeAccess> retMap = new HashMap<>();
    Long nodeID;

    if (!CommonUtils.isNull(dbUser)) {
      getLogger().debug("fetching access rights for user {}", dbUser.getUsername());
      Collection<TabvApicNodeAccess> nodeAccesses = dbUser.getTabvApicNodeAccesses();
      if (nodeAccesses != null) {
        for (TabvApicNodeAccess dbNodeAccessRight : dbUser.getTabvApicNodeAccesses()) {

          nodeID = dbNodeAccessRight.getNodeId();
          retMap.put(nodeID, createDataObject(dbNodeAccessRight));

        }
      }

      // Fetch user list based on active directory and create NodeAccess Object and add to map if user is not having
      // access to PIDC Directly
      assignGroupNodeAccessForUser(dbUser, retMap);
    }

    getLogger().debug("access rights count  = {}", retMap.size());

    return retMap;
  }

  /**
   * @param dbUser
   * @param retMap
   * @throws DataException
   */
  private void assignGroupNodeAccessForUser(final TabvApicUser dbUser, final Map<Long, NodeAccess> retMap)
      throws DataException {
    Set<TActiveDirectoryGroup> userGroups =
        new ActiveDirectoryGroupUserLoader(getServiceData()).getByUser(dbUser.getUsername());
    userGroups.forEach(group -> {
      try {
        checkAndAssignHigherAccessGroup(dbUser, retMap, userGroups);
      }
      catch (DataException e) {
        getLogger().error("Error occured on assignGroupNodeAccessForUser", e);
      }
    });
  }

  /**
   * @param dbUser
   * @param retMap
   * @param userGroups
   * @throws DataException
   */
  private void checkAndAssignHigherAccessGroup(final TabvApicUser dbUser, final Map<Long, NodeAccess> retMap,
      final Set<TActiveDirectoryGroup> userGroups)
      throws DataException {
    // Fetch group node access for each group id
    List<ActiveDirectoryGroupNodeAccess> groupNodeAccess =
        new ActiveDirectoryGroupNodeAccessLoader(getServiceData()).getByGroupIdSet(userGroups);

    List<ActiveDirectoryGroupNodeAccess> effectiveGrpNodeAccess = new ArrayList<>();
    // Check if user already has the node access and ignore those nodes in calculating the higher access since USER
    // DIRECT ACCESS is the priority
    // Remove the below for each block to give overall higer access between user and group
    groupNodeAccess.forEach(grpNodeAccess -> {
      if (!retMap.containsKey(grpNodeAccess.getNodeId())) {
        effectiveGrpNodeAccess.add(grpNodeAccess);
      }
    });

    effectiveGrpNodeAccess.forEach(grpNodeAccess -> {
      // If user already has access to this node, ignore. Else put Node access to RetMap
      if (!retMap.containsKey(grpNodeAccess.getNodeId())) {
        retMap.put(grpNodeAccess.getNodeId(), getNodeAccesFromGroup(dbUser, grpNodeAccess));
      }
      else if (retMap.containsKey(grpNodeAccess.getNodeId())) {
        // Check if exists is the group having higher access than current group
        NodeAccess existingGroup = retMap.get(grpNodeAccess.getNodeId());
        NodeAccess newGroup = getNodeAccesFromGroup(dbUser, grpNodeAccess);
        // check if new group has owner / grant / write and not in old (same order)
        if (((existingGroup != null) && (newGroup != null)) && ((newGroup.isOwner() && !existingGroup.isOwner()) ||
            (newGroup.isGrant() && !existingGroup.isGrant()) || (newGroup.isWrite() && !existingGroup.isWrite()))) {
          retMap.replace(grpNodeAccess.getNodeId(), newGroup);
        }
      }
    });
  }

  /**
   * @param dbUser
   * @param grpNodeAccess
   */
  private NodeAccess getNodeAccesFromGroup(final TabvApicUser dbUser,
      final ActiveDirectoryGroupNodeAccess grpNodeAccess) {
    NodeAccess nodeAccess = new NodeAccess();
    nodeAccess.setUserId(dbUser.getUserId());
    nodeAccess.setGrant(grpNodeAccess.isGrant());
    nodeAccess.setOwner(grpNodeAccess.isOwner());
    nodeAccess.setRead(grpNodeAccess.isRead());
    nodeAccess.setWrite(grpNodeAccess.isWrite());
    nodeAccess.setCreatedDate(grpNodeAccess.getCreatedDate());
    nodeAccess.setCreatedUser(grpNodeAccess.getCreatedUser());
    nodeAccess.setModifiedDate(grpNodeAccess.getModifiedDate());
    nodeAccess.setModifiedUser(grpNodeAccess.getModifiedUser());
    nodeAccess.setNodeId(grpNodeAccess.getNodeId());
    nodeAccess.setNodeType(grpNodeAccess.getNodeType());
    nodeAccess.setId(grpNodeAccess.getId());
    return nodeAccess;
  }

  /**
   * @return map of node access. Key - Node ID, Value - data object
   * @throws DataException if object cannot be created
   */
  public Map<Long, NodeAccess> getAllNodeAccessForCurrentUser() throws DataException {
    Map<Long, NodeAccess> retMap;

    if (getServiceData().isAuthenticatedUser()) {
      TabvApicUser dbUser = new UserLoader(getServiceData()).getEntityObject(getServiceData().getUserId());
      retMap = getNodeAccess(dbUser);
    }
    else {
      retMap = new HashMap<>();
    }

    return retMap;
  }

  /**
   * @param nodeId Node ID
   * @return true, if current user is Owner
   * @throws DataException if object cannot be created
   */
  public boolean isCurrentUserOwner(final Long nodeId) throws DataException {
    NodeAccess access = getAccessRightsCurrentUser(nodeId);
    return (access != null) && access.isOwner();
  }

  /**
   * @param nodeId Node ID
   * @return true, if current user has Grant privileges for the node
   * @throws DataException if object cannot be created
   */
  public boolean isCurrentUserGrant(final Long nodeId) throws DataException {
    NodeAccess access = getAccessRightsCurrentUser(nodeId);
    return (access != null) && access.isGrant();
  }

  /**
   * @param nodeId Node ID
   * @return true, if current user has write access on the node
   * @throws DataException if object cannot be created
   */
  public boolean isCurrentUserWrite(final Long nodeId) throws DataException {
    NodeAccess access = getAccessRightsCurrentUser(nodeId);
    return (access != null) && access.isWrite();
  }

  /**
   * @param nodeId Node ID
   * @return true, if current user has read access on the node
   * @throws DataException if object cannot be created
   */
  public boolean isCurrentUserRead(final Long nodeId) throws DataException {
    NodeAccess access = getAccessRightsCurrentUser(nodeId);
    return (access != null) && access.isRead();
  }

  /**
   * Check if the user has Special Admin access
   *
   * @return TRUE if the user has Admin Node access
   * @throws DataException if object cannot be created
   */
  public boolean hasSpecialAdminAccess() throws DataException {
    Long adminNodeId =
        Long.valueOf(new CommonParamLoader(getServiceData()).getValue(CommonParamKey.ADMIN_ACCESS_NODE_ID));
    return isCurrentUserRead(adminNodeId);
  }

  /**
   * Get node access of current user on the given node ID
   *
   * @param nodeId Node ID
   * @return data object
   * @throws DataException if object cannot be created
   */
  private NodeAccess getAccessRightsCurrentUser(final Long nodeId) throws DataException {
    Object data = getServiceData().retrieveData(getClass(), SESSKEY_CUR_USER_NODE_ACCESS);
    if (data == null) {
      data = getAllNodeAccessForCurrentUser();
      getServiceData().storeData(getClass(), SESSKEY_CUR_USER_NODE_ACCESS, data);
    }
    return ((Map<Long, NodeAccess>) data).get(nodeId);
  }


  /**
   * @param userId nt user id of the given user.
   * @return map of node access. Key - Node ID, Value - data object
   * @throws DataException if object cannot be created
   */
  private Map<Long, NodeAccess> getAllNodeAccessForGivenUser(final String userId) throws DataException {
    Map<Long, NodeAccess> retMap;

    if (userId != null) {
      TabvApicUser dbUser = new UserLoader(getServiceData()).getEntityObjectByUserName(userId);
      retMap = getNodeAccess(dbUser);
    }
    else {
      retMap = new HashMap<>();
    }

    return retMap;
  }


  /**
   * @param nodeType nodeType
   * @param reqUser required user
   * @param accLevel access level - OWNER, GRANT, WRITE, READ
   * @param idSet Set of Node Ids
   * @return node access details for the given input
   * @throws DataException if object cannot be created
   */
  public NodeAccessDetails getAllNodeAccessByNode(final String nodeType, final String reqUser,
      final NodeAccessLevel accLevel, final Set<Long> idSet)
      throws DataException {

    if (CommonUtils.isEmptyString(nodeType)) {
      throw new InvalidInputException("Node type is mandatory");
    }

    if (CommonUtils.isNotEmptyString(reqUser)) {
      return getAccessByUser(nodeType, reqUser, accLevel, idSet);
    }
    if (CommonUtils.isNotEmpty(idSet)) {
      return getAccessByNode(nodeType, accLevel, idSet);
    }
    return getAccessByType(nodeType, accLevel);


  }

  /**
   * @param nodeType nodeType
   * @param reqUserSet required user
   * @param accLevel access level - OWNER, GRANT, WRITE, READ
   * @param idSet Set of Node Ids
   * @return node access details for the given input
   * @throws DataException if object cannot be created
   */
  public NodeAccessDetails getNodeAccessByNodeAndUserSet(final String nodeType, final Set<String> reqUserSet,
      final NodeAccessLevel accLevel, final Set<Long> idSet)
      throws DataException {

    if (CommonUtils.isEmptyString(nodeType)) {
      throw new InvalidInputException("Node type is mandatory");
    }

    if (CommonUtils.isNotEmpty(reqUserSet)) {
      return getAccessByUserNames(nodeType, reqUserSet, accLevel, idSet);
    }
    if (CommonUtils.isNotEmpty(idSet)) {
      return getAccessByNode(nodeType, accLevel, idSet);
    }
    return getAccessByType(nodeType, accLevel);


  }


  /**
   * @param nodeType nodeType
   * @param reqUser NT ID of required user
   * @param reqLevel access level - OWNER, GRANT, WRITE, READ
   * @param idSet Set of Node Ids
   * @return node access details extended for the given input
   * @throws DataException if object cannot be created
   */
  public NodeAccessDetailsExt getAllNodeAccessByNodeExt(final String nodeType, final String reqUser,
      final NodeAccessLevel reqLevel, final Set<Long> idSet)
      throws DataException {

    NodeAccessDetails nodeAccDet = getAllNodeAccessByNode(nodeType, reqUser, reqLevel, idSet);
    NodeAccessDetailsExt nodeAccDetExt = new NodeAccessDetailsExt();
    nodeAccDetExt.setNodeAccessMap(nodeAccDet.getNodeAccessMap());
    nodeAccDetExt.setUserMap(nodeAccDet.getUserMap());

    try (LdapAuthenticationWrapperCloseable ldapCloseable = new LdapAuthenticationWrapperCloseable()) {
      for (User user : nodeAccDetExt.getUserMap().values()) {
        fillUserInfoMap(nodeAccDetExt, ldapCloseable, user);
      }
    }
    return nodeAccDetExt;
  }

  private void fillUserInfoMap(final NodeAccessDetailsExt nodeAccDetExt, final LdapAuthenticationWrapperCloseable ldap,
      final User user) {
    try {
      nodeAccDetExt.getUserInfoMap().put(user.getId(), toLdapUserInfo(ldap.getUserDetails(user.getName())));
    }
    catch (LdapException e) {
      getLogger().error(e.getMessage(), e);
    }
  }

  private LdapUserInfo toLdapUserInfo(final UserInfo userInfo) {
    LdapUserInfo ret = new LdapUserInfo();
    CommonUtils.shallowCopy(ret, userInfo);
    return ret;

  }

  /**
   * @param nodeType
   * @param reqUser
   * @param accLevel
   * @param idSet
   * @return NodeAccessDetails
   * @throws DataException
   */
  private NodeAccessDetails getAccessByUser(final String nodeType, final String reqUser, final NodeAccessLevel accLevel,
      final Set<Long> idSet)
      throws DataException {

    NodeAccessDetails ret = new NodeAccessDetails();

    for (Entry<Long, NodeAccess> naccEntry : getAllNodeAccessForUser(reqUser).entrySet()) {
      NodeAccess data = naccEntry.getValue();

      if (CommonUtils.isEqual(nodeType, data.getNodeType()) &&
          (CommonUtils.isNullOrEmpty(idSet) || idSet.contains(naccEntry.getKey()))) {

        addNodeAccessObj(ret, data, accLevel);
      }
    }

    return ret;
  }

  /**
   * @param nodeType
   * @param reqUser
   * @param accLevel
   * @param idSet
   * @return NodeAccessDetails
   * @throws DataException
   */
  private NodeAccessDetails getAccessByUserNames(final String nodeType, final Set<String> reqUserSet,
      final NodeAccessLevel accLevel, final Set<Long> idSet)
      throws DataException {

    NodeAccessDetails ret = new NodeAccessDetails();

    for (Entry<Long, NodeAccess> naccEntry : getAllNodeAccessForSetOfUsers(reqUserSet).entrySet()) {
      NodeAccess data = naccEntry.getValue();

      if (CommonUtils.isEqual(nodeType, data.getNodeType()) &&
          (CommonUtils.isNullOrEmpty(idSet) || idSet.contains(naccEntry.getKey()))) {

        addNodeAccessObj(ret, data, accLevel);
      }
    }

    return ret;
  }

  /**
   * @param nodeType
   * @param reqUser
   * @param accLevel
   * @param idSet
   * @return
   * @throws DataException
   */
  private NodeAccessDetails getAccessByNode(final String nodeType, final NodeAccessLevel accLevel,
      final Set<Long> idSet)
      throws DataException {

    NodeAccessDetails ret = new NodeAccessDetails();

    for (Set<Long> childSet : CommonUtils.splitSet(idSet, 999)) {

      List<TabvApicNodeAccess> entityList = getServiceData().getEntMgr()
          .createNamedQuery(TabvApicNodeAccess.NQ_GET_ALL_BY_TYPE_N_NODES, TabvApicNodeAccess.class)
          .setParameter("nodeType", nodeType).setParameter("nodeIdSet", childSet).getResultList();

      addNodeAccessDetails(entityList, ret, accLevel);
    }

    return ret;
  }


  /**
   * @param nodeType
   * @param reqUser
   * @param accLevel
   * @return
   * @throws DataException
   */
  private NodeAccessDetails getAccessByType(final String nodeType, final NodeAccessLevel accLevel)
      throws DataException {
    NodeAccessDetails ret = new NodeAccessDetails();

    List<TabvApicNodeAccess> entityList =
        getServiceData().getEntMgr().createNamedQuery(TabvApicNodeAccess.NQ_GET_ALL_BY_TYPE, TabvApicNodeAccess.class)
            .setParameter("nodeType", nodeType).getResultList();

    return addNodeAccessDetails(entityList, ret, accLevel);
  }

  private NodeAccessDetails addNodeAccessDetails(final List<TabvApicNodeAccess> entityList,
      final NodeAccessDetails nodeAccDet, final NodeAccessLevel accLevelReq)
      throws DataException {

    for (TabvApicNodeAccess entity : entityList) {
      addNodeAccessObj(nodeAccDet, createDataObject(entity), accLevelReq);
    }

    return nodeAccDet;
  }

  private void addNodeAccessObj(final NodeAccessDetails nodeAccDet, final NodeAccess nodeAccessObj,
      final NodeAccessLevel accLevelReq)
      throws DataException {

    if ((accLevelReq != null) && !NodeAccessLevel.satisfies(accLevelReq, NodeAccessLevel.getType(nodeAccessObj))) {
      return;
    }

    Set<NodeAccess> nodeAccSet = nodeAccDet.getNodeAccessMap().get(nodeAccessObj.getNodeId());
    if (nodeAccSet == null) {
      nodeAccSet = new HashSet<>();
      nodeAccDet.getNodeAccessMap().put(nodeAccessObj.getNodeId(), nodeAccSet);
    }
    nodeAccSet.add(nodeAccessObj);
    if (nodeAccessObj.getUserId() != 0 && !nodeAccDet.getUserMap().containsKey(nodeAccessObj.getUserId())) {
      UserLoader usrLdr = new UserLoader(getServiceData());
      nodeAccDet.getUserMap().put(nodeAccessObj.getUserId(), usrLdr.getDataObjectByID(nodeAccessObj.getUserId()));
  }
  }

  /**
   * Resets current user's node access details
   */
  protected void resetNodeAccessCache() {
    getServiceData().clearData(getClass(), SESSKEY_CUR_USER_NODE_ACCESS);
  }


  /**
   * @param reqUser reqUser
   * @return the node access info map.
   * @throws IcdmException IcdmException
   */
  public Map<Long, NodeAccessInfo> getAllNodeAccessInfoForGivenUser(final String reqUser) throws IcdmException {
    Map<String, Set<Long>> nodeAccessTypeMap = new HashMap<>();

    Map<Long, NodeAccessInfo> nodeAccInfoMap = new HashMap<>();


    Map<Long, NodeAccess> nodeAccessMap = getAllNodeAccessForGivenUser(reqUser);

    for (NodeAccess nodeAcc : nodeAccessMap.values()) {
      try {
        Long nodeId = nodeAcc.getNodeId();
        String typeCode = nodeAcc.getNodeType();
        Set<Long> nodeIdSet = nodeAccessTypeMap.get(typeCode);
        com.bosch.caltool.icdm.common.bo.apic.NodeType.getNodeType(typeCode);
        if (nodeIdSet == null) {
          nodeIdSet = new HashSet<>();
        }
        nodeIdSet.add(nodeId);
        nodeAccessTypeMap.put(typeCode, nodeIdSet);

      }
      catch (IllegalArgumentException ex) {
        getLogger().error("Node is not available in Node type " + nodeAcc.getNodeType(), ex);
      }
    }

    for (Entry<String, Set<Long>> nodeAccessTypeEntry : nodeAccessTypeMap.entrySet()) {
      String typeCode = nodeAccessTypeEntry.getKey();
      Set<Long> nodeIds = nodeAccessTypeEntry.getValue();
      NodeAccessInfoResolver resolver =
          new NodeAccessInfoResolver(typeCode, new ArrayList<>(), reqUser, getServiceData());

      Map<Long, IDataObject> fillNodeAccMapForUser = resolver.fillNodeAccMapForUser(nodeIds);

      for (IDataObject dataObj : fillNodeAccMapForUser.values()) {
        NodeAccess nodeAccess = nodeAccessMap.get(dataObj.getId());
        NodeAccessInfo info = new NodeAccessInfo();
        info.setAccess(nodeAccess);
        info.setNodeName(dataObj.getName());
        info.setNodeDesc(dataObj.getDescription());
        nodeAccInfoMap.put(nodeAccess.getId(), info);

      }

    }
    return nodeAccInfoMap;
  }


  /**
   * @return map of Special Node Access with node IDs
   */
  public Map<Long, NodeAccessInfo> getAllSpecialNodeAccess() {
    Map<Long, NodeAccessInfo> specialNodeAccessMap = new HashMap<>();
    Query query = getEntMgr().createNamedQuery(TabvApicNodeAccess.NQ_GET_ALL_SPECIAL_NODES, TabvApicNodeAccess.class);
    List<Object> dbRows = query.getResultList();
    for (Object rowObj : dbRows) {
      if (rowObj instanceof Object[]) {
        Object[] itemArr = (Object[]) rowObj;

        String nodeType = itemArr[0].toString();
        Long nodeId = Long.valueOf(itemArr[1].toString());

        NodeAccessInfo accessInfo = new NodeAccessInfo();

        NodeAccess nodeAccess = new NodeAccess();
        nodeAccess.setNodeId(nodeId);
        nodeAccess.setNodeType(nodeType);
        nodeAccess.setOwner(false);
        nodeAccess.setGrant(false);
        nodeAccess.setRead(false);
        nodeAccess.setWrite(false);

        accessInfo.setAccess(nodeAccess);
        accessInfo.setNodeName(nodeType);
        specialNodeAccessMap.put(nodeId, accessInfo);
      }
    }
    return specialNodeAccessMap;
  }

}
