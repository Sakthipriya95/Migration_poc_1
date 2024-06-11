/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * @author gue1cob
 */
public class VLdb2SsdCompAllPK implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * Feature ID
   */
  private BigDecimal featureId;
  /**
   * Lab Obj Id
   */
  private BigDecimal labObjId;
  /**
   * Rev ID
   */
  private BigDecimal revId;
  /**
   * Value ID
   */
  private BigDecimal valueId;

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.featureId == null) ? 0 : this.featureId.hashCode());
    result = (prime * result) + ((this.labObjId == null) ? 0 : this.labObjId.hashCode());
    result = (prime * result) + ((this.revId == null) ? 0 : this.revId.hashCode());
    result = (prime * result) + ((this.valueId == null) ? 0 : this.valueId.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    // if equal, true
    if (this == obj) {
      return true;
    }
    // if null, equal
    if (obj == null) {
      return false;
    }
    // if different, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    VLdb2SsdCompAllPK other = (VLdb2SsdCompAllPK) obj;
    // if null, false
    if (this.featureId == null) {
      if (other.featureId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.featureId.equals(other.featureId)) {
      return false;
    }
    // if null, false
    if (this.labObjId == null) {
      if (other.labObjId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.labObjId.equals(other.labObjId)) {
      return false;
    }
    // if null, false
    if (this.revId == null) {
      if (other.revId != null) {
        return false;
      }
    }
    // if unequal, null
    else if (!this.revId.equals(other.revId)) {
      return false;
    }
    // if null, false
    if (this.valueId == null) {
      if (other.valueId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.valueId.equals(other.valueId)) {
      return false;
    }
    return true;
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
   * @return the labObjId
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId the labObjId to set
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return the revId
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId the revId to set
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
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
}
