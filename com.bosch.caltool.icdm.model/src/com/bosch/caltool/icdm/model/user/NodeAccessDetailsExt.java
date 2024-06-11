/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

import java.util.HashMap;
import java.util.Map;


/**
 * @author bne4cob
 */
public class NodeAccessDetailsExt extends NodeAccessDetails {

  /**
   * Key - user ID, Value - LDAP User Info
   */
  private Map<Long, LdapUserInfo> userInfoMap = new HashMap<>();

  /**
   * @return the userInfoMap
   */
  public Map<Long, LdapUserInfo> getUserInfoMap() {
    return this.userInfoMap;
  }

  /**
   * @param userInfoMap the userInfoMap to set
   */
  public void setUserInfoMap(final Map<Long, LdapUserInfo> userInfoMap) {
    this.userInfoMap = userInfoMap;
  }
}
