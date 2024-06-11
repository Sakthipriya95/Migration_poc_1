/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author bru2cob
 */
public class EmrMetaData {

  private Map<String, Long> emissionStdMap = new ConcurrentHashMap<>();
  private Map<String, EmrCategory> categoryMap = new ConcurrentHashMap<>();
  private Map<String, Long> columnMap = new ConcurrentHashMap<>();
  private Map<String, Long> measureUnitMap = new ConcurrentHashMap<>();

  /**
   * @return the emissionStdMap
   */
  public Map<String, Long> getEmissionStdMap() {
    return this.emissionStdMap;
  }

  /**
   * @return the categoryMap
   */
  public Map<String, EmrCategory> getCategoryMap() {
    return this.categoryMap;
  }


  /**
   * @return the columnMap
   */
  public Map<String, Long> getColumnMap() {
    return this.columnMap;
  }


  /**
   * @return the measureUnitMap
   */
  public Map<String, Long> getMeasureUnitMap() {
    return this.measureUnitMap;
  }

  /**
   * @param emissionStdMap the emissionStdMap to set
   */
  public void setEmissionStdMap(final Map<String, Long> emissionStdMap) {
    this.emissionStdMap = emissionStdMap;
  }


  /**
   * @param categoryMap the categoryMap to set
   */
  public void setCategoryMap(final Map<String, EmrCategory> categoryMap) {
    this.categoryMap = categoryMap;
  }


  /**
   * @param columnMap the columnMap to set
   */
  public void setColumnMap(final Map<String, Long> columnMap) {
    this.columnMap = columnMap;
  }


  /**
   * @param measureUnitMap the measureUnitMap to set
   */
  public void setMeasureUnitMap(final Map<String, Long> measureUnitMap) {
    this.measureUnitMap = measureUnitMap;
  }


}
