/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pdh2cob
 */
public class PlatformFilterModel {

  private List<PlatformFilter> platformFilterList = new ArrayList<>();

  private boolean inverseFlag;


  /**
   * @return the platformFilterList
   */
  public List<PlatformFilter> getPlatformFilterList() {
    return this.platformFilterList;
  }


  /**
   * @param platformFilterList the platformFilterList to set
   */
  public void setPlatformFilterList(final List<PlatformFilter> platformFilterList) {
    this.platformFilterList = platformFilterList;
  }


  /**
   * @return the inverseFlag
   */
  public boolean isInverseFlag() {
    return this.inverseFlag;
  }


  /**
   * @param inverseFlag the inverseFlag to set
   */
  public void setInverseFlag(final boolean inverseFlag) {
    this.inverseFlag = inverseFlag;
  }


}
