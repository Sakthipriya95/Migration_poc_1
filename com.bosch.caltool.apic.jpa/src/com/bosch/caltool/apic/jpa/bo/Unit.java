/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * Unit.java, This class is the business object of the Units
 *
 * @author rgo7cob
 */
// iCDM-513
public class Unit implements Comparable<Unit> {

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  private final String unit;

  /**
   * @param apicDataProvider apicDataProvider
   * @param unit will be unit from the table
   */
  protected Unit(final String unit) {

    this.unit = unit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Unit other) {
    return ApicUtil.compare(getUnit(), other.getUnit());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getUnit() == null) ? 0 : getUnit().hashCode());
   return result;
  }

  /**
   * {@inheritDoc}
   */
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
    Unit other = (Unit) obj;
    return CommonUtils.isEqual(getUnit(), other.getUnit());
  }

  /**
   * @return the Unit Value
   */
  public String getUnit() {

    return this.unit;
  }

}
