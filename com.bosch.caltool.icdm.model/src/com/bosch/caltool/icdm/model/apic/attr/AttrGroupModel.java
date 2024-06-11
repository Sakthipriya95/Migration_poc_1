/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author bne4cob
 */
public class AttrGroupModel {

  private Map<Long, AttrSuperGroup> allSuperGroupMap = new HashMap<>();

  private Map<Long, AttrGroup> allGroupMap = new HashMap<>();

  private Map<Long, Set<Long>> groupBySuperGroupMap = new HashMap<>();

  /**
   * @return the allSuperGroupMap
   */
  public Map<Long, AttrSuperGroup> getAllSuperGroupMap() {
    return this.allSuperGroupMap;
  }


  /**
   * @param allSuperGroupMap the allSuperGroupMap to set
   */
  public void setAllSuperGroupMap(final Map<Long, AttrSuperGroup> allSuperGroupMap) {
    this.allSuperGroupMap = allSuperGroupMap;
  }


  /**
   * @return the allGroupMap
   */
  public Map<Long, AttrGroup> getAllGroupMap() {
    return this.allGroupMap;
  }


  /**
   * @param allGroupMap the allGroupMap to set
   */
  public void setAllGroupMap(final Map<Long, AttrGroup> allGroupMap) {
    this.allGroupMap = allGroupMap;
  }


  /**
   * @return the groupBySuperGroupMap
   */
  public Map<Long, Set<Long>> getGroupBySuperGroupMap() {
    return this.groupBySuperGroupMap;
  }


  /**
   * @param groupBySuperGroupMap the groupBySuperGroupMap to set
   */
  public void setGroupBySuperGroupMap(final Map<Long, Set<Long>> groupBySuperGroupMap) {
    this.groupBySuperGroupMap = groupBySuperGroupMap;
  }

}
