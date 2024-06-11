/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;


/**
 * @author dja7cob
 */
public class EmrData {

  /**
   * EMR file id
   */
  private Long emrFileId;

  /**
   * EMR file data id
   */
  private Long emrFileDataId;

  /**
   * Category name
   */
  private String categoryName;

  /**
   * Column name
   */
  private String columnName;

  /**
   * Column value
   */
  private String colValue;

  /**
   * Value (numeric)
   */
  private String valueNum;

  /**
   * Value (Text)
   */
  private String valueText;

  /**
   * Measurement unit
   */
  private String measureUnitName;

  /**
   * Emission standard Id
   */
  private Long emsId;

  /**
   * Emission standard name
   */
  private String emissionStandardName;

  /**
   * Test case name
   */
  private String testcaseName;

  /**
   * Test Identifier
   */
  private Long testIdentifier;


  /**
   * @return the testIdentifier
   */
  public Long getTestIdentifier() {
    return this.testIdentifier;
  }


  /**
   * @param testIdentifier the testIdentifier to set
   */
  public void setTestIdentifier(final Long testIdentifier) {
    this.testIdentifier = testIdentifier;
  }

  /**
   * @return the emrFileId
   */
  public Long getEmrFileId() {
    return this.emrFileId;
  }


  /**
   * @param emrFileId the emrFileId to set
   */
  public void setEmrFileId(final Long emrFileId) {
    this.emrFileId = emrFileId;
  }


  /**
   * @return the emrFileDataId
   */
  public Long getEmrFileDataId() {
    return this.emrFileDataId;
  }


  /**
   * @param emrFileDataId the emrFileDataId to set
   */
  public void setEmrFileDataId(final Long emrFileDataId) {
    this.emrFileDataId = emrFileDataId;
  }


  /**
   * @return the categoryName
   */
  public String getCategoryName() {
    return this.categoryName;
  }


  /**
   * @param categoryName the categoryName to set
   */
  public void setCategoryName(final String categoryName) {
    this.categoryName = categoryName;
  }


  /**
   * @return the columnName
   */
  public String getColumnName() {
    return this.columnName;
  }


  /**
   * @param columnName the columnName to set
   */
  public void setColumnName(final String columnName) {
    this.columnName = columnName;
  }


  /**
   * @return the colValue
   */
  public String getColValue() {
    return this.colValue;
  }


  /**
   * @param colValue the colValue to set
   */
  public void setColValue(final String colValue) {
    this.colValue = colValue;
  }


  /**
   * @return the valueNum
   */
  public String getValueNum() {
    return this.valueNum;
  }


  /**
   * @param valueNum the valueNum to set
   */
  public void setValueNum(final String valueNum) {
    this.valueNum = valueNum;
  }


  /**
   * @return the valueText
   */
  public String getValueText() {
    return this.valueText;
  }


  /**
   * @param valueText the valueText to set
   */
  public void setValueText(final String valueText) {
    this.valueText = valueText;
  }


  /**
   * @return the measureUnitName
   */
  public String getMeasureUnitName() {
    return this.measureUnitName;
  }


  /**
   * @param measureUnitName the measureUnitName to set
   */
  public void setMeasureUnitName(final String measureUnitName) {
    this.measureUnitName = measureUnitName;
  }


  /**
   * @return the emissionStandardName
   */
  public String getEmissionStandardName() {
    return this.emissionStandardName;
  }


  /**
   * @param emissionStandardName the emissionStandardName to set
   */
  public void setEmissionStandardName(final String emissionStandardName) {
    this.emissionStandardName = emissionStandardName;
  }


  /**
   * @return the testcaseName
   */
  public String getTestcaseName() {
    return this.testcaseName;
  }


  /**
   * @param testcaseName the testcaseName to set
   */
  public void setTestcaseName(final String testcaseName) {
    this.testcaseName = testcaseName;
  }


  /**
   * @return the emsId
   */
  public Long getEmsId() {
    return this.emsId;
  }


  /**
   * @param emsId the emsId to set
   */
  public void setEmsId(final Long emsId) {
    this.emsId = emsId;
  }
}
