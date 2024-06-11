/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.math.BigDecimal;


/**
 * @author TRL1COB
 */
public class CodexResults {

  private String refProcedure;

  private String category;

  private String name;

  private String fuelType;

  private String column;

  private String measure;

  private String valType;

  private BigDecimal numValue;

  private String strValue;

  private boolean boolValue;

  private int dataRowNum;

  /**
   * @return the refProcedure
   */
  public String getRefProcedure() {
    return this.refProcedure;
  }

  /**
   * @param refProcedure the refProcedure to set
   */
  public void setRefProcedure(final String refProcedure) {
    this.refProcedure = refProcedure;
  }

  /**
   * @return the category
   */
  public String getCategory() {
    return this.category;
  }

  /**
   * @param category the category to set
   */
  public void setCategory(final String category) {
    this.category = category;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the column
   */
  public String getColumn() {
    return this.column;
  }

  /**
   * @param column the column to set
   */
  public void setColumn(final String column) {
    this.column = column;
  }

  /**
   * @return the measure
   */
  public String getMeasure() {
    return this.measure;
  }

  /**
   * @param measure the measure to set
   */
  public void setMeasure(final String measure) {
    this.measure = measure;
  }

  /**
   * @return the strValue
   */
  public String getStrValue() {
    return this.strValue;
  }

  /**
   * @param strValue the strValue to set
   */
  public void setStrValue(final String strValue) {
    this.strValue = strValue;
  }


  /**
   * @return the valType
   */
  public String getValType() {
    return this.valType;
  }

  /**
   * @param valType the valType to set
   */
  public void setValType(final String valType) {
    this.valType = valType;
  }


  /**
   * @return the dataRowNum
   */
  public int getDataRowNum() {
    return this.dataRowNum;
  }

  /**
   * @param dataRowNum the dataRowNum to set
   */
  public void setDataRowNum(final int dataRowNum) {
    this.dataRowNum = dataRowNum;
  }

  /**
   * @return the fuelType
   */
  public String getFuelType() {
    return this.fuelType;
  }

  /**
   * @param fuelType the fuelType to set
   */
  public void setFuelType(final String fuelType) {
    this.fuelType = fuelType;
  }

  /**
   * @return the numValue
   */
  public BigDecimal getNumValue() {
    return this.numValue;
  }

  /**
   * @param numValue the numValue to set
   */
  public void setNumValue(final BigDecimal numValue) {
    this.numValue = numValue;
  }

  /**
   * @param booleanCellValue boolean value to set
   */
  public void setBoolValue(final boolean booleanCellValue) {
    this.boolValue = booleanCellValue;

  }


  /**
   * @return the boolValue
   */
  public final boolean isBoolValue() {
    return this.boolValue;
  }
}
