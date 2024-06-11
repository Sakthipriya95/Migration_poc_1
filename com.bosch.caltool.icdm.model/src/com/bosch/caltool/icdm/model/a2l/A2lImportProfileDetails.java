/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.io.Serializable;

/**
 * @author and4cob
 */
public class A2lImportProfileDetails implements Serializable {

  private static final long serialVersionUID = 2377508274122096992L;

  private Long headingRowNum;

  private String sheetName;

  private String importMode;

  private String wpColumn;

  private String respColumn;

  private String respTypeColumn;

  private String labelColumn;

  private String prefixForWp;

  private String prefixForResp;

  private A2lImportProfileFileType fileType;


  /**
   * @return the headingRowNum
   */
  public Long getHeadingRowNum() {
    return this.headingRowNum;
  }

  /**
   * @param headingRowNum the headingRowNum to set
   */
  public void setHeadingRowNum(final Long headingRowNum) {
    this.headingRowNum = headingRowNum;
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
   * @return the importMode
   */
  public String getImportMode() {
    return this.importMode;
  }

  /**
   * @param importMode the importMode to set
   */
  public void setImportMode(final String importMode) {
    this.importMode = importMode;
  }

  /**
   * @return the wpColumn
   */
  public String getWpColumn() {
    return this.wpColumn;
  }

  /**
   * @param wpColumn the wpColumn to set
   */
  public void setWpColumn(final String wpColumn) {
    this.wpColumn = wpColumn;
  }

  /**
   * @return the respColumn
   */
  public String getRespColumn() {
    return this.respColumn;
  }

  /**
   * @param respColumn the respColumn to set
   */
  public void setRespColumn(final String respColumn) {
    this.respColumn = respColumn;
  }

  /**
   * @return the labelColumn
   */
  public String getLabelColumn() {
    return this.labelColumn;
  }

  /**
   * @param labelColumn the labelColumn to set
   */
  public void setLabelColumn(final String labelColumn) {
    this.labelColumn = labelColumn;
  }

  /**
   * @return the prefixForWp
   */
  public String getPrefixForWp() {
    return this.prefixForWp;
  }

  /**
   * @param prefixForWp the prefixForWp to set
   */
  public void setPrefixForWp(final String prefixForWp) {
    this.prefixForWp = prefixForWp;
  }

  /**
   * @return the prefixForResp
   */
  public String getPrefixForResp() {
    return this.prefixForResp;
  }

  /**
   * @param prefixForResp the prefixForResp to set
   */
  public void setPrefixForResp(final String prefixForResp) {
    this.prefixForResp = prefixForResp;
  }

  /**
   * @return the fileType
   */
  public A2lImportProfileFileType getFileType() {
    return this.fileType;
  }

  /**
   * @param fileType the fileType to set
   */
  public void setFileType(final A2lImportProfileFileType fileType) {
    this.fileType = fileType;
  }

  /**
   * @return the respTypeColumn
   */
  public String getRespTypeColumn() {
    return this.respTypeColumn;
  }

  /**
   * @param respTypeColumn the respTypeColumn to set
   */
  public void setRespTypeColumn(final String respTypeColumn) {
    this.respTypeColumn = respTypeColumn;
  }
}
