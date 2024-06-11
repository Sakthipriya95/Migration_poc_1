package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;

/**
 * Entity implementation class for Entity: VLdb2ConfigRelease
 *
 * @author SSN9COB
 */
//@Embeddable

public class VLdb2ConfigReleasePK implements Serializable {


  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * Pro Rel ID
   */
  @Column(name = "PRO_REL_ID")
  private BigDecimal proRelId;

  /**
   * SW Pro Rev ID
   */
  @Column(name = "SW_PRO_REV_ID")
  private BigDecimal swProRevId;

  /**
   * @param proRelId id
   */
  public void setProRelId(final BigDecimal proRelId) {
    this.proRelId = proRelId;
  }

  /**
   * @return id
   */
  public BigDecimal getProRelId() {
    return this.proRelId;
  }

  /**
   * @return id
   */
  public BigDecimal getSwProRevId() {
    return this.swProRevId;
  }

  /**
   * @param swProRevId id
   */
  public void setSwProRevId(final BigDecimal swProRevId) {
    this.swProRevId = swProRevId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.proRelId == null) ? 0 : this.proRelId.hashCode());
    result = (prime * result) + ((this.swProRevId == null) ? 0 : this.swProRevId.hashCode());
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
    VLdb2ConfigReleasePK other = (VLdb2ConfigReleasePK) obj;
    // if null, false
    if (this.proRelId == null) {
      if (other.proRelId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.proRelId.equals(other.proRelId)) {
      return false;
    }
    // if null false
    if (this.swProRevId == null) {
      if (other.swProRevId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.swProRevId.equals(other.swProRevId)) {
      return false;
    }
    return true;
  }

}

