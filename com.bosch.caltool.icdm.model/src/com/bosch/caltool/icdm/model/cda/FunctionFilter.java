/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;


/**
 * @author pdh2cob
 */
public class FunctionFilter {


  private String functionName;

  private String functionVersion;


  /**
   * @return the functionName
   */
  public String getFunctionName() {
    return this.functionName;
  }


  /**
   * @param functionName the functionName to set
   */
  public void setFunctionName(final String functionName) {
    this.functionName = functionName;
  }


  /**
   * @return the functionVersion
   */
  public String getFunctionVersion() {
    return this.functionVersion;
  }


  /**
   * @param functionVersion the functionVersion to set
   */
  public void setFunctionVersion(final String functionVersion) {
    this.functionVersion = functionVersion;
  }


}
