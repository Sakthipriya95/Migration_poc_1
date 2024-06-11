/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;


/**
 * @author say8cob
 */
public class MonicaErrorMessage {

  private String monicaFileName;

  private String sheetName;

  private String reason;


  /**
   * @return the monicaFileName
   */
  public String getMonicaFileName() {
    return this.monicaFileName;
  }


  /**
   * @param monicaFileName the monicaFileName to set
   */
  public void setMonicaFileName(final String monicaFileName) {
    this.monicaFileName = monicaFileName;
  }


  /**
   * @return the sheetName
   */
  public String getSheetName() {
    return this.sheetName;
  }


  /**
   * @param sheetName the sheetName to set
   */
  public void setSheetName(final String sheetName) {
    this.sheetName = sheetName;
  }


  /**
   * @return the reason
   */
  public String getReason() {
    return this.reason;
  }


  /**
   * @param reason the reason to set
   */
  public void setReason(final String reason) {
    this.reason = reason;
  }


}
