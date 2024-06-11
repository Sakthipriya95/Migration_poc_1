package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Primary Key for VLdb2DcmData
 *
 * @author SSN9COB
 */
public class VLdb2DcmDataPK implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Lab Obj ID
   */
  private BigDecimal labObjId;
  /**
   * Rev ID
   */
  private BigDecimal revId;
  /**
   * Row Number
   */
  private BigDecimal rowNumber;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.labObjId == null) ? 0 : this.labObjId.hashCode());
    result = (prime * result) + ((this.revId == null) ? 0 : this.revId.hashCode());
    result = (prime * result) + ((this.rowNumber == null) ? 0 : this.rowNumber.hashCode());
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
    VLdb2DcmDataPK other = (VLdb2DcmDataPK) obj;
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
    // if nulll, false
    if (this.revId == null) {
      if (other.revId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.revId.equals(other.revId)) {
      return false;
    }
    // if null, false
    if (this.rowNumber == null) {
      if (other.rowNumber != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.rowNumber.equals(other.rowNumber)) {
      return false;
    }
    return true;
  }


}
