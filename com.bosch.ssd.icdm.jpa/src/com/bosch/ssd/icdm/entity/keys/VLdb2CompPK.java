package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;

/**
 * Primary key for VLdb2Comp
 * 
 * @author SSN9COB
 */
public class VLdb2CompPK implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Feature id
   */
  @Column(name = "FEATURE_ID", nullable = false, precision = 8)
  private BigDecimal featureId;

  /**
   * Lab Obj ID
   */
  @Column(name = "LAB_OBJ_ID", nullable = false, precision = 10)
  private BigDecimal labObjId;

  /**
   * Rev ID
   */
  @Column(name = "REV_ID", nullable = false, precision = 10)
  private BigDecimal revId;

  /**
   * Value ID
   */
  @Column(name = "VALUE_ID", nullable = false, precision = 8)
  private BigDecimal valueId;

  /**
   * @return feature
   */
  public BigDecimal getFeatureId() {
    return this.featureId;
  }

  /**
   * @param featureId id
   */
  public void setFeatureId(final BigDecimal featureId) {
    this.featureId = featureId;
  }

  /**
   * @return id
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }

  /**
   * @param labObjId id
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }

  /**
   * @return id
   */
  public BigDecimal getRevId() {
    return this.revId;
  }

  /**
   * @param revId id
   */
  public void setRevId(final BigDecimal revId) {
    this.revId = revId;
  }

  /**
   * @return id
   */
  public BigDecimal getValueId() {
    return this.valueId;
  }

  /**
   * @param valueId id
   */
  public void setValueId(final BigDecimal valueId) {
    this.valueId = valueId;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.featureId == null) ? 0 : this.featureId.hashCode());
    result = (prime * result) + ((this.revId == null) ? 0 : this.revId.hashCode());
    result = (prime * result) + ((this.labObjId == null) ? 0 : this.labObjId.hashCode());
    result = (prime * result) + ((this.valueId == null) ? 0 : this.valueId.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    VLdb2CompPK other = (VLdb2CompPK) obj;
    if (this.featureId == null) {
      if (other.featureId != null) {
        return false;
      }
    }
    else if (!this.featureId.equals(other.featureId)) {
      return false;
    }
    if (this.revId == null) {
      if (other.revId != null) {
        return false;
      }
    }
    else if (!this.revId.equals(other.revId)) {
      return false;
    }
    if (this.labObjId == null) {
      if (other.labObjId != null) {
        return false;
      }
    }
    else if (!this.labObjId.equals(other.labObjId)) {
      return false;
    }
    if (this.valueId == null) {
      if (other.valueId != null) {
        return false;
      }
    }
    else if (!this.valueId.equals(other.valueId)) {
      return false;
    }
    return true;
  }
}
