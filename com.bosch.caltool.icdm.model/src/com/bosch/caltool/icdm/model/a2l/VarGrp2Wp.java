/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author bru2cob
 */
public class VarGrp2Wp {

  private String wpName;

  private String respName;

  private Long varGrpId;

  private String respTypeCode;

  private boolean excelWPRespTypeColAvail;


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
   * @return the respTypeCode
   */
  public String getRespTypeCode() {
    return respTypeCode;
  }


  /**
   * @param respTypeCode the respTypeCode to set
   */
  public void setRespTypeCode( String respTypeCode) {
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
