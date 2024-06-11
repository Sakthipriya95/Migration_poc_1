/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.general;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.ActiveDirectoryGroupNodeAccessServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.ActiveDirectoryGroupUserServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;

/**
 * @author gge6cob
 */
public class NodeAccessPageDataHandler extends AbstractClientDataHandler {

  private final IModel node;


  /**
   * @return the node
   */
  public IModel getNode() {
    return this.node;
  }

  private String errorMessage;
  private String title;

  private final CurrentUserBO currentUser = new CurrentUserBO();

  private final CommonDataBO commonData = new CommonDataBO();

  private boolean isReadColApplicable = false;
  private boolean canEditFlag = true;
  private boolean includeMonicaAuditor;
  private boolean includeApicWriteUsers = true;

  private boolean isNodeDeleted;
  private boolean isNormalized;

  private final ConcurrentMap<Long, NodeAccess> nodeAccessMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, User> userMap = new ConcurrentHashMap<>();

  private boolean dataLoaded;

  private NodeAccess attrNodeAccess;
  private User attrUser;

  /**
   * New Variables - Changes for PIDC Access with Access Directory Groups
   */
  // ALM - 727289: Allow adding groups to access rights page of PIDCs

  private final ConcurrentMap<Long, ActiveDirectoryGroupNodeAccess> nodeAccessADGroupMap = new ConcurrentHashMap<>();
  private ActiveDirectoryGroupNodeAccess attrNodeAccessADGroup;


  /**
   * @return the attrUser
   */
  public User getAttrUser() {
    return this.attrUser;
  }


  /**
   * @param attrUser the attrUser to set
   */
  public void setAttrUser(final User attrUser) {
    this.attrUser = attrUser;
  }


  /**
   * @return the attrNodeAccessSet
   */
  public NodeAccess getAttrNodeAccess() {
    return this.attrNodeAccess;
  }


  /**
   * @param attrNodeAccessSet the attrNodeAccessSet to set
   */
  public void setAttrNodeAccess(final NodeAccess attrNodeAccessSet) {
    this.attrNodeAccess = attrNodeAccessSet;
  }

  /**
   * @return the currentUser
   */
  public CurrentUserBO getCurrentUser() {
    return this.currentUser;
  }

  /**
   * @param node node
   */
  public NodeAccessPageDataHandler(final IModel node) {
    super();
    this.node = node;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(
        chData -> this.dataLoaded &&
            CommonUtils.isEqual(getNodeId(), ((NodeAccess) CnsUtils.getModel(chData)).getNodeId()),
        m -> this.dataLoaded = false, MODEL_TYPE.NODE_ACCESS);
    // CNS Register for Active Directory Group Node Access
    registerCns(
        chData -> this.dataLoaded &&
            CommonUtils.isEqual(getNodeId(), ((ActiveDirectoryGroupNodeAccess) CnsUtils.getModel(chData)).getNodeId()),
        m -> this.dataLoaded = false, MODEL_TYPE.ACTIVE_DIRECTORY_GROUP_NODE_ACCES);
  }

  /**
   * @return the nodeId
   */
  public Long getNodeId() {
    return this.node.getId();
  }

  /**
   * @return the nodeType
   */
  public IModelType getNodeType() {
    return ModelTypeRegistry.INSTANCE.getTypeOfModel(this.node);
  }

  /**
   * Returns whether the logged in user has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  private boolean canModifyAccessRights() {
    try {
      return (getCurrentUserNodeAccess() != null) && getCurrentUserNodeAccess().isGrant();
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }
  }

  /**
   * Returns whether the logged in user has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  private boolean canModifyOwnerRights() {
    try {
      if (this.currentUser.hasApicWriteAccess()) {
        return true;
      }
      return (getCurrentUserNodeAccess() != null) && (getCurrentUserNodeAccess().isOwner());
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }
  }


  /*
   * Extracted frequently used code to a private method
   */
  private NodeAccess getCurrentUserNodeAccess() throws ApicWebServiceException {
    return this.currentUser.getNodeAccessRight(getNodeId());
  }

  /**
   * @return boolean
   */
  public boolean isModifiable() {
    try {
      if (getNodeType() == MODEL_TYPE.PIDC) {
        // Check for lock status
        return canModifyAccessRights() || this.currentUser.hasApicWriteAccess();
      }

      // Check for normlized state
      if (getNodeType() == MODEL_TYPE.ATTRIBUTE) {
        return (this.currentUser.hasApicWriteAccess() || canModifyAccessRights());
      }
      if (getNodeType() == MODEL_TYPE.USE_CASE) {
        return this.currentUser.hasApicWriteAccess() || (canModifyAccessRights() && !isNodeDeleted());
      }

      return this.currentUser.hasApicWriteAccess() || canModifyAccessRights();

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      return false;
    }
  }


  /**
   * @return true, if current user can modify owner access
   */
  public boolean isOwnerModifiable() {

    return canModifyOwnerRights();
  }

  /**
   * @return the errorMessage
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }


  /**
   * @param errorMessage the errorMessage to set
   */
  public void setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
  }


  /**
   * @return the includeMonicaAuditor
   */
  public boolean isIncludeMonicaAuditor() {
    return this.includeMonicaAuditor;
  }


  /**
   * @param includeMonicaAuditor the includeMonicaAuditor to set
   */
  public void setIncludeMonicaAuditor(final boolean includeMonicaAuditor) {
    this.includeMonicaAuditor = includeMonicaAuditor;
  }


  /**
   * @return the includeApicWriteUsers
   */
  public boolean isIncludeApicWriteUsers() {
    return this.includeApicWriteUsers;
  }


  /**
   * @param includeApicWriteUsers the includeApicWriteUsers to set
   */
  public void setIncludeApicWriteUsers(final boolean includeApicWriteUsers) {
    this.includeApicWriteUsers = includeApicWriteUsers;
  }


  /**
   * @return the isNormalized
   */
  public boolean isNormalized() {
    return this.isNormalized;
  }


  /**
   * @param isNormalized the isNormalized to set
   */
  public void setNormalized(final boolean isNormalized) {
    this.isNormalized = isNormalized;
  }


  /**
   * @return canEdit
   */
  public boolean getCanEditFlag() {
    return this.canEditFlag;
  }


  /**
   * @param canEdit flag
   */
  public void setCanEditFlag(final boolean canEdit) {
    this.canEditFlag = canEdit;
  }

  /**
   * @return the isReadFlag
   */
  public boolean isReadColApplicable() {
    return this.isReadColApplicable;
  }

  /**
   * @param isReadFlag the isReadFlag to set
   */
  public void setReadColApplicable(final boolean isReadFlag) {
    this.isReadColApplicable = isReadFlag;
  }

  /**
   * @return the isNodeDeleted
   */
  public boolean isNodeDeleted() {
    return this.isNodeDeleted;
  }


  /**
   * @param isNodeDeleted the isNodeDeleted to set
   */
  public void setNodeDeleted(final boolean isNodeDeleted) {
    this.isNodeDeleted = isNodeDeleted;
  }


  /**
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }


  /**
   * @param title the title to set
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * @return the nodeAccessMap
   */
  private ConcurrentMap<Long, NodeAccess> getNodeAccessMap() {
    loadData();
    if (getNodeType().getTypeCode().equals(ApicConstants.ATTR_NODE_TYPE) && (this.attrNodeAccess != null) &&
        !this.nodeAccessMap.containsKey(this.attrNodeAccess.getId())) {
      this.nodeAccessMap.put(this.attrNodeAccess.getId(), this.attrNodeAccess);
    }
    return this.nodeAccessMap;
  }

  /**
   * @return the userMap
   */
  private ConcurrentMap<Long, User> getUserMap() {
    loadData();
    if (getNodeType().getTypeCode().equals(ApicConstants.ATTR_NODE_TYPE) && (this.attrNodeAccess != null) &&
        !this.userMap.containsKey(this.attrNodeAccess.getUserId())) {
      this.userMap.put(this.attrNodeAccess.getUserId(), this.attrUser);
    }
    return this.userMap;
  }

  /**
   * @param userId User ID
   * @return User
   */
  public User getUser(final Long userId) {
    return getUserMap().get(userId);
  }

  /**
   * @return get all node accesss records of this node
   */
  public SortedSet<NodeAccess> getNodeAccess() {
    return new TreeSet<>(getNodeAccessMap().values());
  }

  private void loadData() {
    if (!this.dataLoaded) {
      this.nodeAccessMap.clear();
      this.userMap.clear();
      this.nodeAccessADGroupMap.clear();
      try {
        NodeAccessDetails wsRet = new NodeAccessServiceClient().getNodeAccessDetailsByNode(getNodeType(), getNodeId());

        wsRet.getNodeAccessMap().values().forEach(set -> set.forEach(acc -> this.nodeAccessMap.put(acc.getId(), acc)));

        // Changes to add list of groups to existing node Access Details object
        loadDataOfAdGroupForNodeId(wsRet);

        this.userMap.putAll(wsRet.getUserMap());

        this.dataLoaded = true;
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog("Error in retrieving access rights data : " + e.getMessage(), e,
            Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * Get user name for the given node access ID
   *
   * @param nodeAccesId node access ID
   * @return user name (NT ID).
   */
  public String getUserName(final Long nodeAccesId) {
    User user = getUserInNodeAccess(nodeAccesId);
    return user == null ? ApicConstants.EMPTY_STRING : user.getName();
  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @param nodeAccesId node access ID
   * @return the users fullName
   */
  public String getUserFullName(final Long nodeAccesId) {
    User user = getUserInNodeAccess(nodeAccesId);
    if (user != null) {
      final StringBuilder fullName = new StringBuilder();
      if (!CommonUtils.isEmptyString(user.getLastName())) {
        fullName.append(user.getLastName()).append(", ");
      }
      if (!CommonUtils.isEmptyString(user.getFirstName())) {
        fullName.append(user.getFirstName());
      }
      if (fullName.length() == 0) {
        fullName.append(user.getName());
      }
      return fullName.toString();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * @param nodeAccesId nodeAccesId
   * @return department name of user
   */
  public String getUserDepartment(final Long nodeAccesId) {
    User user = getUserInNodeAccess(nodeAccesId);
    return user == null ? ApicConstants.EMPTY_STRING : user.getDepartment();
  }

  private User getUserInNodeAccess(final Long nodeAccesId) {
    Long userId = getNodeAccess(nodeAccesId).getUserId();
    return getUser(userId);
  }

  /**
   * @param nodeAccesId nodeAccesId
   * @return POJO
   */
  public NodeAccess getNodeAccess(final Long nodeAccesId) {
    return getNodeAccessMap().get(nodeAccesId);
  }


  /**
   * Changes for PIDC Access with Access Directory Groups
   */
  // ALM - 727289: Allow adding groups to access rights page of PIDCs

  /**
   * @return the nodeAccessMap
   */
  private ConcurrentMap<Long, ActiveDirectoryGroupNodeAccess> getNodeAccessADGroupMap() {
    loadData();
    if (getNodeType().getTypeCode().equals(ApicConstants.PIDC_NODE_TYPE) && (this.attrNodeAccessADGroup != null) &&
        !this.nodeAccessADGroupMap.containsKey(this.attrNodeAccessADGroup.getId())) {
      this.nodeAccessADGroupMap.put(this.attrNodeAccessADGroup.getId(), this.attrNodeAccessADGroup);
    }
    return this.nodeAccessADGroupMap;
  }

  /**
   * @param wsRet
   * @throws ApicWebServiceException
   */
  private void loadDataOfAdGroupForNodeId(final NodeAccessDetails wsRet) throws ApicWebServiceException {
    Map<Long, List<ActiveDirectoryGroupNodeAccess>> nodesGroupList =
        new ActiveDirectoryGroupNodeAccessServiceClient().getNodeAccessByNodeId(getNodeId());
    Map<Long, Set<ActiveDirectoryGroupNodeAccess>> nodeAccessADGrpMap = new HashMap<>();
    nodeAccessADGrpMap.put(getNodeId(), new HashSet<>(nodesGroupList.get(getNodeId())));
    wsRet.setNodeAccessADGrpMap(nodeAccessADGrpMap);
    wsRet.getNodeAccessADGrpMap().values()
        .forEach(set -> set.forEach(acc -> this.nodeAccessADGroupMap.put(acc.getId(), acc)));
  }

  /**
   * @return get all node accesss records of this node
   */
  public SortedSet<ActiveDirectoryGroupNodeAccess> getNodeADGroupAccess() {
    return new TreeSet<>(getNodeAccessADGroupMap().values());
  }

  /**
   * @return the attrNodeAccessADGroup
   */
  public ActiveDirectoryGroupNodeAccess getAttrNodeAccessADGroup() {
    return this.attrNodeAccessADGroup;
  }


  /**
   * @param attrNodeAccessADGroup the attrNodeAccessADGroup to set
   */
  public void setAttrNodeAccessADGroup(final ActiveDirectoryGroupNodeAccess attrNodeAccessADGroup) {
    this.attrNodeAccessADGroup = attrNodeAccessADGroup;
  }

  /**
   * Fetch list of users in a group
   *
   * @param groupId ID
   * @return list
   * @throws ApicWebServiceException e
   */
  public List<ActiveDirectoryGroupUser> getADGroupUsersByGroupId(final long groupId) throws ApicWebServiceException {
    return new ActiveDirectoryGroupUserServiceClient().getByGroupId(groupId);
  }


  /**
   * @return the commonData
   */
  public CommonDataBO getCommonData() {
    return this.commonData;
  }
}
