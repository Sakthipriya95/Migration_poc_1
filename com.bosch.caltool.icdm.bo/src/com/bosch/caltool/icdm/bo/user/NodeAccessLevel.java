/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.user;

import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * Node access level
 *
 * @author bne4cob
 */
public enum NodeAccessLevel {
                         /**
                          * READ access
                          */
                         READ,
                         /**
                          * WRITE access
                          */
                         WRITE,
                         /**
                          * GRANT access
                          */
                         GRANT,
                         /**
                          * OWNER access
                          */
                         OWNER;

  /**
   * String to level
   *
   * @param levelStr level string
   * @return level
   */
  public static NodeAccessLevel getType(final String levelStr) {
    NodeAccessLevel ret = null;
    for (NodeAccessLevel level : NodeAccessLevel.values()) {
      if (level.name().equalsIgnoreCase(levelStr)) {
        ret = level;
        break;
      }
    }

    return ret;
  }

  /**
   * Get access level from pojo
   *
   * @param nodeAccess nodeAccess pojo
   * @return level
   */
  public static NodeAccessLevel getType(final NodeAccess nodeAccess) {
    NodeAccessLevel ret = null;
    if (nodeAccess.isOwner()) {
      ret = NodeAccessLevel.OWNER;
    }
    else if (nodeAccess.isGrant()) {
      ret = NodeAccessLevel.GRANT;
    }
    else if (nodeAccess.isWrite()) {
      ret = NodeAccessLevel.WRITE;
    }
    else if (nodeAccess.isRead()) {
      ret = NodeAccessLevel.READ;
    }

    return ret;
  }

  /**
   * Checks whether access level satisfies the required level
   *
   * @param levelReq required level
   * @param levelAct actual level
   * @return true if actual level satisfies required level
   */
  public static boolean satisfies(final NodeAccessLevel levelReq, final NodeAccessLevel levelAct) {
    return (levelAct != null) && (levelAct.ordinal() >= levelReq.ordinal());
  }

}