/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.vo;

import java.util.Map;


/**
 * Class CalDataVO.java - is responsible for providing data for the table in series statistics for a paramater
 * 
 * @author adn1cob
 */
// iCDM-217
public class CalDataVO {

  /**
   * X Axis unit
   */
  private String xUnit;
  /**
   * Y Axis unit
   */
  private String yUnit;
  /**
   * Y Axis values
   */
  private String yAxisValue;
  /**
   * Row data for the table, key is x column
   */
  private Map<String, String> rowValMap;


  /**
   * Constructor, CalDataVO
   * 
   * @param xUnit , x axis unit
   * @param yUnit , y axis unit
   * @param yAxisValue , y axis values
   * @param rowValMap , row data
   */
  public CalDataVO(final String xUnit, final String yUnit, final String yAxisValue, final Map<String, String> rowValMap) {
    this.xUnit = xUnit;
    this.yUnit = yUnit;
    this.yAxisValue = yAxisValue;
    this.rowValMap = rowValMap;
  }


  /**
   * @return the unit
   */
  public String getXUnit() {
    return this.xUnit;
  }


  /**
   * @param unit the unit to set
   */
  public void setXUnit(final String unit) {
    this.xUnit = unit;
  }


  /**
   * @return the rowValMap
   */
  public Map<String, String> getRowValMap() {
    return this.rowValMap;
  }


  /**
   * @param rowValMap the rowValMap to set
   */
  public void setRowValMap(final Map<String, String> rowValMap) {
    this.rowValMap = rowValMap;
  }


  /**
   * @return the yUnit
   */
  public String getYUnit() {
    return this.yUnit;
  }


  /**
   * @param yUnit the yUnit to set
   */
  public void setYUnit(final String yUnit) {
    this.yUnit = yUnit;
  }


  /**
   * @return the yAxisValue
   */
  public String getYAxisValue() {
    return this.yAxisValue;
  }


  /**
   * @param yAxisValue the yAxisValue to set
   */
  public void setYAxisValue(final String yAxisValue) {
    this.yAxisValue = yAxisValue;
  }

}
