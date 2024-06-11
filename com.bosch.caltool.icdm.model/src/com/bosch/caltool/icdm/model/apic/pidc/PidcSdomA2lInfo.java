/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.Map;

/**
 * @author dja7cob
 */
public class PidcSdomA2lInfo {

  private SdomPVER sdomPver;

  private Map<Long, PidcA2l> a2lMap;

  /**
   * @return the sdomPver
   */
  public SdomPVER getSdomPver() {
    return this.sdomPver;
  }


  /**
   * @param sdomPver the sdomPver to set
   */
  public void setSdomPver(final SdomPVER sdomPver) {
    this.sdomPver = sdomPver;
  }


  /**
   * @return the a2lMap
   */
  public Map<Long, PidcA2l> getA2lMap() {
    return this.a2lMap;
  }


  /**
   * @param a2lMap the a2lMap to set
   */
  public void setA2lMap(final Map<Long, PidcA2l> a2lMap) {
    this.a2lMap = a2lMap;
  }


}
