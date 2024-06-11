/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;


/**
 * Primary Key for TSsd2TempFeaval
 *
 * @author apl1cob
 */
public class TSsd2TempFeavalPK implements Serializable {

  /**
   * Serial Version UID
   */
  private static final long serialVersionUID = 1L;

  /**
   * Feature iD
   */
  @Column(name = "FEATURE_ID")
  private BigDecimal featureId;


  /**
   * Value ID
   */
  @Column(name = "VALUE_ID")
  private BigDecimal valueId;


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
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.featureId == null) ? 0 : this.featureId.hashCode());
    result = (prime * result) + ((this.valueId == null) ? 0 : this.valueId.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    /*
     * Check and return true if equal
     */
    if (this == obj) {
      return true;
    }
    // if null false
    if (obj == null) {
      return false;
    }
    // if different class, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    TSsd2TempFeavalPK other = (TSsd2TempFeavalPK) obj;
    // if one of them s null, false
    if (this.featureId == null) {
      if (other.featureId != null) {
        return false;
      }
    }
    // if feature id is not equal, false
    else if (!this.featureId.equals(other.featureId)) {
      return false;
    }
    // if value is null, false
    if (this.valueId == null) {
      if (other.valueId != null) {
        return false;
      }
    }
    // if value id is not equal false
    else if (!this.valueId.equals(other.valueId)) {
      return false;
    }
    return true;
  }
}
