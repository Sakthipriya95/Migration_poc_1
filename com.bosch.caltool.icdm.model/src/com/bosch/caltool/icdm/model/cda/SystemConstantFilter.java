/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;


/**
 * @author pdh2cob
 */
public class SystemConstantFilter {

  private String systemConstantName;

  private String systemConstantValue;

  private boolean inverseFlag;


  /**
   * @return the systemConstantName
   */
  public String getSystemConstantName() {
    return this.systemConstantName;
  }


  /**
   * @param systemConstantName the systemConstantName to set
   */
  public void setSystemConstantName(final String systemConstantName) {
    this.systemConstantName = systemConstantName;
  }


  /**
   * @return the systemConstantValue
   */
  public String getSystemConstantValue() {
    return this.systemConstantValue;
  }


  /**
   * @param systemConstantValue the systemConstantValue to set
   */
  public void setSystemConstantValue(final String systemConstantValue) {
    this.systemConstantValue = systemConstantValue;
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
