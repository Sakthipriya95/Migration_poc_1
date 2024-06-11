/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.user.User;

/**
 * Model to maintain PIDC WP Resp objects for given pidc id
 *
 * @author pdh2cob
 */
public class A2lResponsibilityModel {


  /**
   * Key - A2lResponsibility ID, Value - A2lResponsibility object
   */
  private Map<Long, A2lResponsibility> a2lResponsibilityMap = new HashMap<>();

  /**
   * This map contains user objects for those A2lResponsibility objects where userID is present Key - user id, value -
   * User object
   */
  private Map<Long, User> userMap = new HashMap<>();

  /**
   * Key - WpRespEnum type code - R,C Value- A2lResponsibility
   */
  private Map<String, A2lResponsibility> defaultA2lRespMap = new HashMap<>();

  /**
   * Key - A2l Resp ID, Value Map of Key - A2lRespBoschGroupUser ID, Value - A2lRespBoschGroupUser object
   */
  private Map<Long, Map<Long, A2lRespBoschGroupUser>> a2lBoschGrpUserMap = new HashMap<>();


  /**
   * @return the a2lBoschGrpUserMap
   */
  public Map<Long, Map<Long, A2lRespBoschGroupUser>> getA2lBoschGrpUserMap() {
    return this.a2lBoschGrpUserMap;
  }


  /**
   * @param a2lBoschGrpUserMap the a2lBoschGrpUserMap to set
   */
  public void setA2lBoschGrpUserMap(final Map<Long, Map<Long, A2lRespBoschGroupUser>> a2lBoschGrpUserMap) {
    this.a2lBoschGrpUserMap = a2lBoschGrpUserMap;
  }


  /**
   * @return the a2lResponsibilityMap
   */
  public Map<Long, A2lResponsibility> getA2lResponsibilityMap() {
    return this.a2lResponsibilityMap;
  }


  /**
   * @param a2lResponsibilityMap the a2lResponsibilityMap to set
   */
  public void setA2lResponsibilityMap(final Map<Long, A2lResponsibility> a2lResponsibilityMap) {
    this.a2lResponsibilityMap = a2lResponsibilityMap;
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
   * @return the defaultA2lRespMap
   */
  public Map<String, A2lResponsibility> getDefaultA2lRespMap() {
    return this.defaultA2lRespMap;
  }


  /**
   * @param defaultA2lRespMap the defaultA2lRespMap to set
   */
  public void setDefaultA2lRespMap(final Map<String, A2lResponsibility> defaultA2lRespMap) {
    this.defaultA2lRespMap = defaultA2lRespMap;
  }


}

