/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


import com.bosch.caltool.icdm.model.cdr.ParamCollection;


/**
 * @author dja7cob
 */
public class Function extends ParamCollection {


  /**
   *
   */
  private static final long serialVersionUID = -7918520033226924976L;

  private String longName;

  private String isCustFunc;


  private String bigFunc;

  private String relevantName;


  private String upperName;

  /**
   * @return the upperName
   */
  public String getUpperName() {
    return this.upperName;
  }


  /**
   * @param upperName the upperName to set
   */
  public void setUpperName(final String upperName) {
    this.upperName = upperName;
  }


  /**
   * @return the longName
   */
  public String getLongName() {
    return this.longName;
  }


  /**
   * @param longName the longName to set
   */
  public void setLongName(final String longName) {
    this.longName = longName;
  }


  /**
   * @return the isCustFunc
   */
  public String getIsCustFunc() {
    return this.isCustFunc;
  }


  /**
   * @param isCustFunc the isCustFunc to set
   */
  public void setIsCustFunc(final String isCustFunc) {
    this.isCustFunc = isCustFunc;
  }


  /**
   * @return the bigFunc
   */
  public String getBigFunc() {
    return this.bigFunc;
  }


  /**
   * @param bigFunc the bigFunc to set
   */
  public void setBigFunc(final String bigFunc) {
    this.bigFunc = bigFunc;
  }


  /**
   * @return the relevantName
   */
  public String getRelevantName() {
    return this.relevantName;
  }


  /**
   * @param relevantName the relevantName to set
   */
  public void setRelevantName(final String relevantName) {
    this.relevantName = relevantName;
  }


}
