/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;


/**
 * @author pdh2cob
 */
public class CaldataAnalyzerResultSummary {

  private int labelsAnalyzedCount = 0;

  private int paramFilterItemsCount = 0;

  private int functionFilterItemsCount = 0;

  private int platformFilterItemsCount = 0;

  private int customerFilterItemsCount = 0;

  private int sysconFilterItemsCount = 0;


  /**
   * @return the labelsAnalyzedCount
   */
  public int getLabelsAnalyzedCount() {
    return this.labelsAnalyzedCount;
  }


  /**
   * @param labelsAnalyzedCount the labelsAnalyzedCount to set
   */
  public void setLabelsAnalyzedCount(final int labelsAnalyzedCount) {
    this.labelsAnalyzedCount = labelsAnalyzedCount;
  }


  /**
   * @return the paramFilterItemsCount
   */
  public int getParamFilterItemsCount() {
    return this.paramFilterItemsCount;
  }


  /**
   * @param paramFilterItemsCount the paramFilterItemsCount to set
   */
  public void setParamFilterItemsCount(final int paramFilterItemsCount) {
    this.paramFilterItemsCount = paramFilterItemsCount;
  }


  /**
   * @return the functionFilterItemsCount
   */
  public int getFunctionFilterItemsCount() {
    return this.functionFilterItemsCount;
  }


  /**
   * @param functionFilterItemsCount the functionFilterItemsCount to set
   */
  public void setFunctionFilterItemsCount(final int functionFilterItemsCount) {
    this.functionFilterItemsCount = functionFilterItemsCount;
  }


  /**
   * @return the platformFilterItemsCount
   */
  public int getPlatformFilterItemsCount() {
    return this.platformFilterItemsCount;
  }


  /**
   * @param platformFilterItemsCount the platformFilterItemsCount to set
   */
  public void setPlatformFilterItemsCount(final int platformFilterItemsCount) {
    this.platformFilterItemsCount = platformFilterItemsCount;
  }


  /**
   * @return the customerFilterItemsCount
   */
  public int getCustomerFilterItemsCount() {
    return this.customerFilterItemsCount;
  }


  /**
   * @param customerFilterItemsCount the customerFilterItemsCount to set
   */
  public void setCustomerFilterItemsCount(final int customerFilterItemsCount) {
    this.customerFilterItemsCount = customerFilterItemsCount;
  }


  /**
   * @return the sysconFilterItemsCount
   */
  public int getSysconFilterItemsCount() {
    return this.sysconFilterItemsCount;
  }


  /**
   * @param sysconFilterItemsCount the sysconFilterItemsCount to set
   */
  public void setSysconFilterItemsCount(final int sysconFilterItemsCount) {
    this.sysconFilterItemsCount = sysconFilterItemsCount;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "CaldataAnalyzerResultSummary [parametersAnalyzedCount=" + this.labelsAnalyzedCount +
        ", paramFilterItemsCount=" + this.paramFilterItemsCount + ", functionFilterItemsCount=" +
        this.functionFilterItemsCount + ", platformFilterItemsCount=" + this.platformFilterItemsCount +
        ", customerFilterItemsCount=" + this.customerFilterItemsCount + ", sysconFilterItemsCount=" +
        this.sysconFilterItemsCount + "]";
  }


}
