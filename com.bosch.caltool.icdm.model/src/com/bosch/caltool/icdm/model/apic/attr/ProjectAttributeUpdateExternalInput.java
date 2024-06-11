/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

/**
 * @author mkl2cob
 */
public class ProjectAttributeUpdateExternalInput {

  /**
   * pidc id
   */
  private Long pidcId;
  /**
   * variant id
   */
  private Long variantId;
  /**
   * attribute id
   */
  private Long attributeId;
  /**
   * value as AttributeValueExtModel
   */
  private AttributeValueExtModel value;

  /**
   * used flag
   */
  private String usedFlag;

  /**
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }

  /**
   * @return the variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }

  /**
   * @param variantId the variantId to set
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }

  /**
   * @return the attributeId
   */
  public Long getAttributeId() {
    return this.attributeId;
  }

  /**
   * @param attributeId the attributeId to set
   */
  public void setAttributeId(final Long attributeId) {
    this.attributeId = attributeId;
  }

  /**
   * @return the value
   */
  public AttributeValueExtModel getValue() {
    return this.value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(final AttributeValueExtModel value) {
    this.value = value;
  }

  /**
   * @return the usedFlag
   */
  public String getUsedFlag() {
    return this.usedFlag;
  }

  /**
   * @param usedFlag the usedFlag to set
   */
  public void setUsedFlag(final String usedFlag) {
    this.usedFlag = usedFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ProjectAttributeUpdateExternalModel [");
    if (this.pidcId != null) {
      builder.append("pidcId=");
      builder.append(this.pidcId);
      builder.append(", ");
    }
    if (this.variantId != null) {
      builder.append("variantId=");
      builder.append(this.variantId);
      builder.append(", ");
    }
    if (this.attributeId != null) {
      builder.append("attributeId=");
      builder.append(this.attributeId);
      builder.append(", ");
    }
    if (this.value != null) {
      builder.append("value=");
      builder.append(this.value);
      builder.append(", ");
    }
    if (this.usedFlag != null) {
      builder.append("usedFlag=");
      builder.append(this.usedFlag);
    }
    builder.append("]");
    return builder.toString();
  }
}
