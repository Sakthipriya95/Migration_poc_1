/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rgo7cob
 */
public class ConsolidatedRisks implements Comparable<ConsolidatedRisks> {


  /**
   * key is cat id
   */
  private Map<Long, RmCategory> categoryMap = new ConcurrentHashMap<>();

  /**
   * Map of measures for each category<br>
   * key is cat id
   */
  private Map<Long, RmCategoryMeasures> categoryMeasureMap = new ConcurrentHashMap<>();


  /**
   * key is risk id
   */
  private Map<Long, RmRiskLevel> riskLevelMap = new ConcurrentHashMap<>();


  /**
   * key is category id and value is risk id
   */
  private Map<Long, Long> catRiskMap = new ConcurrentHashMap<>();


  /**
   * @return the categoryMap
   */
  public Map<Long, RmCategory> getCategoryMap() {
    return this.categoryMap;
  }


  /**
   * @return the categoryMeasuerMap
   */
  public Map<Long, RmCategoryMeasures> getCategoryMeasureMap() {
    return this.categoryMeasureMap;
  }


  /**
   * @return the riskLevelMap
   */
  public Map<Long, RmRiskLevel> getRiskLevelMap() {
    return this.riskLevelMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ConsolidatedRisks o) {
    // TODO Auto-generated method stub
    return 0;
  }


  /**
   * @param categoryMap the categoryMap to set
   */
  public void setCategoryMap(final Map<Long, RmCategory> categoryMap) {
    this.categoryMap = categoryMap;
  }


  /**
   * @param categoryMeasuerMap the categoryMeasuerMap to set
   */
  public void setCategoryMeasureMap(final Map<Long, RmCategoryMeasures> categoryMeasuerMap) {
    this.categoryMeasureMap = categoryMeasuerMap;
  }


  /**
   * @param riskLevelMap the riskLevelMap to set
   */
  public void setRiskLevelMap(final Map<Long, RmRiskLevel> riskLevelMap) {
    this.riskLevelMap = riskLevelMap;
  }


  /**
   * @return the catRiskMap
   */
  public Map<Long, Long> getCatRiskMap() {
    return this.catRiskMap;
  }


  /**
   * @param catRiskMap the catRiskMap to set
   */
  public void setCatRiskMap(final Map<Long, Long> catRiskMap) {
    this.catRiskMap = catRiskMap;
  }


}
