/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;


/**
 * @author apl1cob
 */
public class VLdb2Ssd2PK implements Serializable {

  /**
   * default serial version id
   */
  private static final long serialVersionUID = 1L;

  /**
   * Rev ID
   */
  @Column(name = "REV_ID")
  private BigDecimal revId;

  /**
   * Lab Obj ID
   */
  @Column(name = "LAB_OBJ_ID")
  private BigDecimal labObjId;

  /**
  *
  */
  public VLdb2Ssd2PK() {
    super();
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
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.labObjId == null) ? 0 : this.labObjId.hashCode());
    result = (prime * result) + ((this.revId == null) ? 0 : this.revId.hashCode());
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
    // if null, false
    if (obj == null) {
      return false;
    }
    // if different, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    VLdb2Ssd2PK other = (VLdb2Ssd2PK) obj;
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
    // if uneqaul, false
    else if (!this.revId.equals(other.revId)) {
      return false;
    }
    return true;
  }


}
