package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;

/**
 * Primary Key for TempLdb2Feava
 * 
 * @author apl1cob
 */
public class TempLdb2FeavalPK implements Serializable {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  /**
   * Rel ID
   */
  @Column(name = "REL_ID")
  private BigDecimal relId;

  /**
   * Feature id
   */
  @Column(name = "FEATURE_ID")
  private BigDecimal featureId;

  /**
   * @return id
   */
  public BigDecimal getConfigId() {
    return this.relId;
  }

  /**
   * @param configId id
   */
  public void setConfigId(final BigDecimal configId) {
    this.relId = configId;
  }

  /**
   * @return id
   */
  public BigDecimal getConfigSwVersid() {
    return this.featureId;
  }

  /**
   * @param configSwVersid id
   */
  public void setConfigSwVersid(final BigDecimal configSwVersid) {
    this.featureId = configSwVersid;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.relId == null) ? 0 : this.relId.hashCode());
    result = (prime * result) + ((this.featureId == null) ? 0 : this.featureId.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    // if equal, true
    if (this == obj) {
      return true;
    }
    // if null, false
    if (obj == null) {
      return false;
    }
    // if different class, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    TempLdb2FeavalPK other = (TempLdb2FeavalPK) obj;
    // if id null, false
    if (this.relId == null) {
      if (other.relId != null) {
        return false;
      }
    }
    // i id unequal, false
    else if (!this.relId.equals(other.relId)) {
      return false;
    }
    // if id null, false
    if (this.featureId == null) {
      if (other.featureId != null) {
        return false;
      }
    }
    // if id unequal, false
    else if (!this.featureId.equals(other.featureId)) {
      return false;
    }
    return true;
  }
}
