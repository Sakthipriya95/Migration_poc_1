/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;


/**
 * ICDM-886
 * 
 * @author bru2cob
 */
public class A2LParamInfo {

  /**
   * selected a2l param
   */
  private final A2LParameter a2lParam;

  /**
   * a2l param's file name
   */
  private final String a2lFileName;


  /**
   * @param a2lParam
   * @param a2lFileName
   */
  public A2LParamInfo(final A2LParameter a2lParam, final String a2lFileName) {
    this.a2lParam = a2lParam;
    this.a2lFileName = a2lFileName;
  }

  /**
   * @return the a2lParam
   */
  public A2LParameter getA2lParam() {
    return this.a2lParam;
  }

  /**
   * @return the a2lFileName
   */
  public String getA2lFileName() {
    return this.a2lFileName;
  }
}
