/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.fc2wp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.icdm.model.a2l.BaseComponent;
import com.bosch.caltool.icdm.model.a2l.PTType;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * @author bne4cob
 */
public class FC2WPMappingWithDetails {

  private Map<String, FC2WPMapping> fc2wpMappingMap = new ConcurrentHashMap<>();
  private Map<Long, BaseComponent> bcMap = new ConcurrentHashMap<>();
  private Map<Long, PTType> ptTypeMap = new ConcurrentHashMap<>();
  private Map<Long, WorkPackageDivision> wpDetMap = new ConcurrentHashMap<>();
  private Map<Long, User> userMap = new ConcurrentHashMap<>();


  /**
   * @param fc2wpMappingMap the fc2wpMappingMap to set
   */
  public void setFc2wpMappingMap(final Map<String, FC2WPMapping> fc2wpMappingMap) {
    this.fc2wpMappingMap = fc2wpMappingMap;
  }


  /**
   * @param bcMap the bcMap to set
   */
  public void setBcMap(final Map<Long, BaseComponent> bcMap) {
    this.bcMap = bcMap;
  }


  /**
   * @param ptTypeMap the ptTypeMap to set
   */
  public void setPtTypeMap(final Map<Long, PTType> ptTypeMap) {
    this.ptTypeMap = ptTypeMap;
  }


  /**
   * @param wpDetMap the wpDetMap to set
   */
  public void setWpDetMap(final Map<Long, WorkPackageDivision> wpDetMap) {
    this.wpDetMap = wpDetMap;
  }

  /**
   * @return the wpDetMap
   */
  public Map<Long, WorkPackageDivision> getWpDetMap() {
    return this.wpDetMap;
  }

  /**
   * @return the fc2wpMappingMap
   */
  public Map<String, FC2WPMapping> getFc2wpMappingMap() {
    return this.fc2wpMappingMap;
  }

  /**
   * @return the ptTypeMap
   */
  public Map<Long, PTType> getPtTypeMap() {
    return this.ptTypeMap;
  }

  /**
   * @return the bcMap
   */
  public Map<Long, BaseComponent> getBcMap() {
    return this.bcMap;
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


}
