/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.ssd;

import java.math.BigDecimal;

/**
 * @author dmr1cob
 */
public class FeatureValueICDMModel {

  private String featureText;
  private String valueText;
  private BigDecimal featureId;
  private BigDecimal valueId;

  /**
   * @return the featureText
   */
  public String getFeatureText() {
    return featureText;
  }

  /**
   * @param featureText the featureText to set
   */
  public void setFeatureText(String featureText) {
    this.featureText = featureText;
  }

  /**
   * @return the valueText
   */
  public String getValueText() {
    return valueText;
  }

  /**
   * @param valueText the valueText to set
   */
  public void setValueText(String valueText) {
    this.valueText = valueText;
  }

  /**
   * @return the featureId
   */
  public BigDecimal getFeatureId() {
    return featureId;
  }

  /**
   * @param featureId the featureId to set
   */
  public void setFeatureId(BigDecimal featureId) {
    this.featureId = featureId;
  }

  /**
   * @return the valueId
   */
  public BigDecimal getValueId() {
    return valueId;
  }

  /**
   * @param valueId the valueId to set
   */
  public void setValueId(BigDecimal valueId) {
    this.valueId = valueId;
  }

}
