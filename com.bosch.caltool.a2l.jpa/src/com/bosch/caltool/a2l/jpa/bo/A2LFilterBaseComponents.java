/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.calmodel.a2ldata.AbstractA2LObject;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * @author bru2cob
 */
// ICDM-1011
@Deprecated
public class A2LFilterBaseComponents implements Comparable<A2LFilterBaseComponents> {

  /**
   * constant for string compare true
   */
  private static final int EQUAL = 0;

  /**
   * @author bru2cob
   */
  public enum SortColumns {
                           /**
                            * Sort Based On the Name
                            */
                           SORT_CHAR_NAME,
                           /**
                            * Sort Based on Unit
                            */
                           SORT_CHAR_VERSION,
  }

  /**
   * A2LBaseComponent instance
   */
  private final A2LBaseComponents a2lBC;
  /**
   * BC name
   */
  private final String bcName;
  /**
   * BC Long name
   */
  private final String bcLongNmae;
  /**
   * BC state
   */
  private final String bcState;

  /**
   * BC version- By default *
   */
  private String version = "*";

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @param a2lBC A2LBaseComponent instance
   */
  public A2LFilterBaseComponents(final A2LBaseComponents a2lBC) {
    this.a2lBC = a2lBC;
    this.bcName = a2lBC.getBcName();
    this.bcLongNmae = a2lBC.getLongName();
    this.bcState = a2lBC.getState();
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final String version) {
    this.version = version;
  }

  /**
   * @return the bcName
   */
  public String getBcName() {
    return this.bcName;
  }

  /**
   * @return the bcLongNmae
   */
  public String getBcLongNmae() {
    return this.bcLongNmae;
  }


  /**
   * @return the bcState
   */
  public String getBcState() {
    return this.bcState;
  }

  /**
   * @return the a2lBC
   */
  public A2LBaseComponents getA2lBC() {
    return this.a2lBC;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LFilterBaseComponents arg0) {
    return ApicUtil.compare(getBcName(), arg0.getBcName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getBcName() == null) ? 0 : getBcName().hashCode());
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
    A2LFilterBaseComponents other = (A2LFilterBaseComponents) obj;
    return CommonUtils.isEqualIgnoreCase(getBcName(), other.getBcName());
  }

  /**
   * @param bc2 bc2
   * @param sortColumn sortColumn
   * @return the Compare Int
   */
  public int compareTo(final A2LFilterBaseComponents bc2, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {

      case SORT_CHAR_NAME:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getBcName(), bc2.getBcName());
        break;

      case SORT_CHAR_VERSION:
        // use compare method for Strings
        compareResult = AbstractA2LObject.compare(getVersion(), bc2.getVersion());
        break;

      default:
        compareResult = 0;
        break;
    }

    // additional compare column is the name of the bc
    if (compareResult == EQUAL) {
      // compare result is equal, compare the bc name
      compareResult = AbstractA2LObject.compare(getBcName(), bc2.getBcName());
    }
    return compareResult;
  }


}
