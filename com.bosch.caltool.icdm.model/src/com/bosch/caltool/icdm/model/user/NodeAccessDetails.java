/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;

/**
 * @author bne4cob
 */
public class NodeAccessDetails {

  /**
   * Key - Node ID; Value - Set of Node access objects
   */
  private Map<Long, Set<NodeAccess>> nodeAccessMap = new HashMap<>();
  /**
   * Key - User ID; Value - User objects
   */
  private Map<Long, User> userMap = new HashMap<>();

  /**
   * Key - Node ID; Value - Set of Node access AD Group objects
   */
  private Map<Long, Set<ActiveDirectoryGroupNodeAccess>> nodeAccessADGrpMap = new HashMap<>();

  private final Map<Long, Set<ActiveDirectoryGroupUser>> nodeAccessADGrpUsersMap = new HashMap<>();

  /**
   * @return the nodeAccessMap
   */
  public Map<Long, Set<NodeAccess>> getNodeAccessMap() {
    return this.nodeAccessMap;
  }

  /**
   * @param nodeAccessMap the nodeAccessMap to set
   */
  public void setNodeAccessMap(final Map<Long, Set<NodeAccess>> nodeAccessMap) {
    this.nodeAccessMap = nodeAccessMap;
  }

  /**
   * @return the userMap
   */
  public Map<Long, User> getUserMap() {
    return this.userMap;
  }

  /**
   * @param userMap the userMap to set
   */
  public void setUserMap(final Map<Long, User> userMap) {
    this.userMap = userMap;
  }

  /**
   * @return the nodeAccessADGrpMap
   */
  public Map<Long, Set<ActiveDirectoryGroupNodeAccess>> getNodeAccessADGrpMap() {
    return this.nodeAccessADGrpMap;
  }

  /**
   * @param nodeAccessADGrpMap the nodeAccessADGrpMap to set
   */
  public void setNodeAccessADGrpMap(final Map<Long, Set<ActiveDirectoryGroupNodeAccess>> nodeAccessADGrpMap) {
    this.nodeAccessADGrpMap = nodeAccessADGrpMap;
  }

  /**
   * @return the nodeAccessADGrpUsersMap
   */
  public Map<Long, Set<ActiveDirectoryGroupUser>> getNodeAccessADGrpUsersMap() {
    return nodeAccessADGrpUsersMap;
  }


}
