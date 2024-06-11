/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the V_LDB2_MATURITY_LEVEL database table.
 */
public class VLdb2MaturityLevelPK implements Serializable {

  /**
   * Serial Id
   */
  private static final long serialVersionUID = 1L;
  /**
   * Lab Obj ID
   */
  private BigDecimal labObjId;
  /**
   * Rev id
   */
  private BigDecimal revId;

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.labObjId == null) ? 0 : this.labObjId.hashCode());
    result = (prime * result) + ((this.revId == null) ? 0 : this.revId.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    // if equal. true
    if (this == obj) {
      return true;
    }
    // if null, false
    if (obj == null) {
      return false;
    }
    // if different, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    VLdb2MaturityLevelPK other = (VLdb2MaturityLevelPK) obj;
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
    // if unqual, false
    else if (!this.revId.equals(other.revId)) {
      return false;
    }
    return true;
  }
}
