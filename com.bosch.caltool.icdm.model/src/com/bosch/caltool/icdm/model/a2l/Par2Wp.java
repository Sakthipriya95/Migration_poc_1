/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author bru2cob
 */
public class Par2Wp {

  private String paramName;

  private String wpName;

  private String respName;

  private String respTypeCode;

  private Long a2lParamId;

  private String funcName;

  private Long varGrpId;

  private boolean excelWPRespTypeColAvail;

  /**
   * @return the varGrpId
   */
  public Long getVarGrpId() {
    return this.varGrpId;
  }


  /**
   * @param varGrpId the varGrpId to set
   */
  public void setVarGrpId(final Long varGrpId) {
    this.varGrpId = varGrpId;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }


  /**
   * @return the respName
   */
  public String getRespName() {
    return this.respName;
  }


  /**
   * @param respName the respName to set
   */
  public void setRespName(final String respName) {
    this.respName = respName;
  }


  /**
   * @return the a2lParamId
   */
  public Long getA2lParamId() {
    return this.a2lParamId;
  }


  /**
   * @param a2lParamId the a2lParamId to set
   */
  public void setA2lParamId(final Long a2lParamId) {
    this.a2lParamId = a2lParamId;
  }


  /**
   * @return the funcName
   */
  public String getFuncName() {
    return this.funcName;
  }


  /**
   * @param funcName the funcName to set
   */
  public void setFuncName(final String funcName) {
    this.funcName = funcName;
  }


  /**
   * @return the respTypeCode
   */
  public String getRespTypeCode() {
    return this.respTypeCode;
  }


  /**
   * @param respTypeCode the respTypeCode to set
   */
  public void setRespTypeCode(final String respTypeCode) {
    this.respTypeCode = respTypeCode;
  }


  /**
   * @return the isWPRespTypeColAvail
   */
  public boolean isExcelWPRespTypeColAvail() {
    return this.excelWPRespTypeColAvail;
  }


  /**
   * @param isWPRespTypeColAvail the isWPRespTypeColAvail to set
   */
  public void setExcelWPRespTypeColAvail(final boolean isWPRespTypeColAvail) {
    this.excelWPRespTypeColAvail = isWPRespTypeColAvail;
  }


}
