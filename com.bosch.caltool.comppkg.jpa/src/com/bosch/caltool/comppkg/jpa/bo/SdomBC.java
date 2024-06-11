/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * BC details holder <br>
 * This POJO class holds required details of Base components
 *
 * @author adn1cob
 */
public class SdomBC implements Comparable<SdomBC> {
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  /**
   * Defines BC name
   */
  private final String bcName;
  /**
   * Defines BC description
   */
  private final String bcDescription;

  /**
   * enum for columns
   */
  public enum SortColumns {
                           /**
                            * Function Name
                            */
                           SORT_NAME,
                           /**
                            * Parameter Name
                            */
                           SORT_LONG_NAME,

  }

  /**
   * @param bcName bcName
   * @param bcDescription description
   */
  protected SdomBC(final String bcName, final String bcDescription) {
    this.bcName = bcName;
    this.bcDescription = bcDescription;
  }

  /**
   * Get Description
   *
   * @return description
   */
  public String getDescription() {
    return this.bcDescription;
  }


  /**
   * @return bc name
   */
  public String getName() {
    return this.bcName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final SdomBC other) {
    return getName().compareTo(other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getName() == null) ? 0 : getName().hashCode());
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
    SdomBC other = (SdomBC) obj;
    return CommonUtils.isEqual(getName(), other.getName());
  }

  /**
   * @param param2 parameter to be compared with
   * @param sortColumn name of the sortColumn
   * @return int
   */
  public int compareTo(final SdomBC param2, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_NAME:
        // comparing the BC names
        compareResult = ApicUtil.compare(getName(), param2.getName());
        break;
      case SORT_LONG_NAME:
        compareResult = ApicUtil.compare(getDescription(), param2.getDescription());
        break;
      default:
        // Compare name
        compareResult = compareTo(param2);
        break;
    }

    // additional compare if both the values are same
    if (compareResult == 0) {
      // compare result is equal, compare the parameter name
      compareResult = compareTo(param2);
    }

    return compareResult;
  }

}
