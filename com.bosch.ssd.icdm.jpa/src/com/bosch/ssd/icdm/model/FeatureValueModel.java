/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.math.BigDecimal;


/**
 * @author gue1cob - This model stores the feature value
 */
public class FeatureValueModel {

  private String featureText;
  private String valueText;
  private String operatorText;
  
  private BigDecimal featureId;
  private BigDecimal valueId;
  private BigDecimal operatorId;


  
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
   * @return the featureId
   */
  public BigDecimal getFeatureId() {
    return this.featureId;
  }

  /**
   * @param featureId the featureId to set
   */
  public void setFeatureId(final BigDecimal featureId) {
    this.featureId = featureId;
  }

  /**
   * @return the valueId
   */
  public BigDecimal getValueId() {
    return this.valueId;
  }

  /**
   * @param valueId the valueId to set
   */
  public void setValueId(final BigDecimal valueId) {
    this.valueId = valueId;
  }

  /**
   * @return the operatorId
   */
  public BigDecimal getOperatorId() {
    return this.operatorId;
  }

  
  /**
   * @param operatorId the operatorId to set
   */
  public void setOperatorId(final BigDecimal operatorId) {
   
    this.operatorId = operatorId;
  }

  /**
   * @return the featureText
   */
  public String getFeatureText() {
    return this.featureText;
  }

  /**
   * @param featureText the featureText to set
   */
  public void setFeatureText(final String featureText) {
    this.featureText = featureText;
  }


  /**
   * @return the operatorText
   */
  public String getOperatorText() {
    return this.operatorText;
  }

  
  /**
   * @param operatorText the operatorText to set
   */
  public void setOperatorText(final String operatorText) {
    
    this.operatorText = operatorText;
  }
}
