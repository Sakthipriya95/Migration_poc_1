/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author bru2cob
 */
public class PidcA2LFiles {

  /**
   * Map of pidc versions with a2l files used during review
   * <p>
   * Key - version id <br>
   * Value - version name
   */
  private Map<Long, String> pidcVersMap = new HashMap<>();
  /**
   * Map of a2l files belong to each version used during review
   * <p>
   * Key - PIDC version id <br>
   * Value - list of a2l file info
   */
  private Map<Long, List<PidcA2LFileInfo>> pidcA2LInfo = new HashMap<>();
  /**
   * Map of pidc variants for a2l files
   * <p>
   * Key - variant id <br>
   * Value - variant eng name
   */
  private Map<Long, String> pidcVarsMap = new HashMap<>();

  /**
   * @return the pidcVersMap
   */
  public Map<Long, String> getPidcVersMap() {
    return this.pidcVersMap;
  }


  /**
   * @param pidcVersMap the pidcVersMap to set
   */
  public void setPidcVersMap(final Map<Long, String> pidcVersMap) {
    this.pidcVersMap = pidcVersMap;
  }


  /**
   * @return the pidcA2LInfo
   */
  public Map<Long, List<PidcA2LFileInfo>> getPidcA2LInfo() {
    return this.pidcA2LInfo;
  }


  /**
   * @param pidcA2LInfo the pidcA2LInfo to set
   */
  public void setPidcA2LInfo(final Map<Long, List<PidcA2LFileInfo>> pidcA2LInfo) {
    this.pidcA2LInfo = pidcA2LInfo;
  }


  /**
   * @return the pidcVarsMap
   */
  public Map<Long, String> getPidcVarsMap() {
    return this.pidcVarsMap;
  }


  /**
   * @param pidcVarsMap the pidcVarsMap to set
   */
  public void setPidcVarsMap(final Map<Long, String> pidcVarsMap) {
    this.pidcVarsMap = pidcVarsMap;
  }


}
