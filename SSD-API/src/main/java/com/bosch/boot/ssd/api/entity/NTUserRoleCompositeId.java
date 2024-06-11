/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity;

import java.io.Serializable;

/**
 * @author ICP1COB
 */
public class NTUserRoleCompositeId implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -7203552607428113015L;

  private String userNtId;

  private int roleId;

  /**
   * @return the userNtId
   */
  public String getUserNtId() {
    return userNtId;
  }

  /**
   * @param userNtId the userNtId to set
   */
  public void setUserNtId(String userNtId) {
    this.userNtId = userNtId;
  }

  /**
   * @return the roleId
   */
  public int getRoleId() {
    return roleId;
  }

  /**
   * @param roleId the roleId to set
   */
  public void setRoleId(int roleId) {
    this.roleId = roleId;
  }
}

