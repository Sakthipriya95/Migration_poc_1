/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.cdfx;

/**
 * @author pdh2cob
 */
public class CdfxExportOutput {

  private String cdfxFileName;

  private String excelFileName;

  private int a2lParamCount;

  // Parameters which are reviewed
  private int calDataObjCount;

  private String exportFilePath;

  // Parameters which are in official review,socre 9 and locked (100% status)
  private int completedParamCount;

  // Parameters in the selected scope/WP
  private int wpRespParamCount;

  private int notRevParamCount;

  private int preCalParamCount;

  private int calibratedParamCount;

  private int checkedParamCount;


  /**
   * @return the cdfxFileName
   */
  public String getCdfxFileName() {
    return this.cdfxFileName;
  }


  /**
   * @param cdfxFileName the cdfxFileName to set
   */
  public void setCdfxFileName(final String cdfxFileName) {
    this.cdfxFileName = cdfxFileName;
  }


  /**
   * @return the excelFileName
   */
  public String getExcelFileName() {
    return this.excelFileName;
  }


  /**
   * @param excelFileName the excelFileName to set
   */
  public void setExcelFileName(final String excelFileName) {
    this.excelFileName = excelFileName;
  }


  /**
   * @return the a2lParamCount
   */
  public int getA2lParamCount() {
    return this.a2lParamCount;
  }


  /**
   * @param a2lParamCount the a2lParamCount to set
   */
  public void setA2lParamCount(final int a2lParamCount) {
    this.a2lParamCount = a2lParamCount;
  }


  /**
   * @return the calDataObjCount
   */
  public int getCalDataObjCount() {
    return this.calDataObjCount;
  }


  /**
   * @param calDataObjCount the calDataObjCount to set
   */
  public void setCalDataObjCount(final int calDataObjCount) {
    this.calDataObjCount = calDataObjCount;
  }


  /**
   * @return the exportFilePath
   */
  public String getExportFilePath() {
    return this.exportFilePath;
  }


  /**
   * @param exportFilePath the exportFilePath to set
   */
  public void setExportFilePath(final String exportFilePath) {
    this.exportFilePath = exportFilePath;
  }


  /**
   * @return the completedParamCount
   */
  public int getCompletedParamCount() {
    return this.completedParamCount;
  }


  /**
   * @param completedParamCount the completedParamCount to set
   */
  public void setCompletedParamCount(final int completedParamCount) {
    this.completedParamCount = completedParamCount;
  }


  /**
   * @return the wpRespParamCount
   */
  public int getWpRespParamCount() {
    return this.wpRespParamCount;
  }


  /**
   * @param wpRespParamCount the wpRespParamCount to set
   */
  public void setWpRespParamCount(final int wpRespParamCount) {
    this.wpRespParamCount = wpRespParamCount;
  }


  /**
   * @return the notRevParamCount
   */
  public int getNotRevParamCount() {
    return this.notRevParamCount;
  }


  /**
   * @param notRevParamCount the notRevParamCount to set
   */
  public void setNotRevParamCount(final int notRevParamCount) {
    this.notRevParamCount = notRevParamCount;
  }


  /**
   * @return the preCalParamCount
   */
  public int getPreCalParamCount() {
    return this.preCalParamCount;
  }


  /**
   * @param preCalParamCount the preCalParamCount to set
   */
  public void setPreCalParamCount(final int preCalParamCount) {
    this.preCalParamCount = preCalParamCount;
  }


  /**
   * @return the calibratedParamCount
   */
  public int getCalibratedParamCount() {
    return this.calibratedParamCount;
  }


  /**
   * @param calibratedParamCount the calibratedParamCount to set
   */
  public void setCalibratedParamCount(final int calibratedParamCount) {
    this.calibratedParamCount = calibratedParamCount;
  }


  /**
   * @return the checkedParamCount
   */
  public int getCheckedParamCount() {
    return this.checkedParamCount;
  }


  /**
   * @param checkedParamCount the checkedParamCount to set
   */
  public void setCheckedParamCount(final int checkedParamCount) {
    this.checkedParamCount = checkedParamCount;
  }


}
