/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bne4cob
 */
public class PidcVersionInfo {

  /**
   * PidcVersion instance
   */
  private PidcVersion pidcVersion;
  /**
   * Pidc instance
   */
  private Pidc pidc;
  /**
   * Key - level, Value - level attribute in project
   */
  private Map<Long, PidcVersionAttribute> levelAttrMap = new HashMap<>();


  private boolean active;


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }


  /**
   * @param pidc the pidc to set
   */
  public void setPidc(final Pidc pidc) {
    this.pidc = pidc;
  }


  /**
   * @return the levelAttrMap
   */
  public Map<Long, PidcVersionAttribute> getLevelAttrMap() {
    return this.levelAttrMap;
  }


  /**
   * @param levelAttrMap the levelAttrMap to set
   */
  public void setLevelAttrMap(final Map<Long, PidcVersionAttribute> levelAttrMap) {
    this.levelAttrMap = levelAttrMap;
  }

  /**
   * @return the active
   */
  public boolean isActive() {
    return this.active;
  }


  /**
   * @param active the active to set
   */
  public void setActive(final boolean active) {
    this.active = active;
  }
}
