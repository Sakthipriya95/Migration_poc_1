/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rgo7cob
 */
public class RmMetaData {


  private Map<Long, RmRiskLevel> riskLevelMap = new ConcurrentHashMap<>();

  private Map<Long, RmProjectCharacter> projCharMap = new ConcurrentHashMap<>();

  private Map<Long, RmCategoryMeasures> rmMeasuresMap = new ConcurrentHashMap<>();

  private Map<Long, RmCategory> rbInputDataCatagoryMap = new ConcurrentHashMap<>();

  private Map<Long, RmCategory> rbSwShareCategoryMap = new ConcurrentHashMap<>();

  private Map<Long, RmCategory> rbColumnCategoryMap = new ConcurrentHashMap<>();

  /**
   * @return the riskLevelMap
   */
  public Map<Long, RmRiskLevel> getRiskLevelMap() {
    return this.riskLevelMap;
  }


  /**
   * @return the projCharMap
   */
  public Map<Long, RmProjectCharacter> getProjCharMap() {
    return this.projCharMap;
  }


  /**
   * @return the rmMeasuresMap
   */
  public Map<Long, RmCategoryMeasures> getRmMeasuresMap() {
    return this.rmMeasuresMap;
  }


  /**
   * @param riskLevelMap the riskLevelMap to set
   */
  public void setRiskLevelMap(final Map<Long, RmRiskLevel> riskLevelMap) {
    this.riskLevelMap = riskLevelMap;
  }


  /**
   * @param projCharMap the projCharMap to set
   */
  public void setProjCharMap(final Map<Long, RmProjectCharacter> projCharMap) {
    this.projCharMap = projCharMap;
  }

  /**
   * @param rmMeasuresMap the rmMeasuresMap to set
   */
  public void setRmMeasuresMap(final Map<Long, RmCategoryMeasures> rmMeasuresMap) {
    this.rmMeasuresMap = rmMeasuresMap;
  }


  /**
   * @return the rbInputDataCatagoryMap
   */
  public Map<Long, RmCategory> getRbInputDataCatagoryMap() {
    return this.rbInputDataCatagoryMap;
  }


  /**
   * @param rbInputDataCatagoryMap the rbInputDataCatagoryMap to set
   */
  public void setRbInputDataCatagoryMap(final Map<Long, RmCategory> rbInputDataCatagoryMap) {
    this.rbInputDataCatagoryMap = rbInputDataCatagoryMap;
  }


  /**
   * @return the rbSwShareCategoryMap
   */
  public Map<Long, RmCategory> getRbSwShareCategoryMap() {
    return this.rbSwShareCategoryMap;
  }


  /**
   * @param rbSwShareCategoryMap the rbSwShareCategoryMap to set
   */
  public void setRbSwShareCategoryMap(final Map<Long, RmCategory> rbSwShareCategoryMap) {
    this.rbSwShareCategoryMap = rbSwShareCategoryMap;
  }


  /**
   * @return the rbColumnCategoryMap
   */
  public Map<Long, RmCategory> getRbColumnCategoryMap() {
    return this.rbColumnCategoryMap;
  }


  /**
   * @param rbColumnCategoryMap the rbColumnCategoryMap to set
   */
  public void setRbColumnCategoryMap(final Map<Long, RmCategory> rbColumnCategoryMap) {
    this.rbColumnCategoryMap = rbColumnCategoryMap;
  }
}
