/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import com.bosch.calmodel.a2ldata.AbstractA2LObject;
import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author rgo7cob Icdm-857 new Modal class for CDFX export parameter
 */
@Deprecated
public class CdfxExportParameter extends AbstractA2LObject {

  /**
   * @author rgo7cob for Sort the Parametres
   */
  public enum SortColumns {
                           /**
                            * Sort Based On the Name
                            */
                           SORT_CHAR_NAME,
                           /**
                            * Sort Based on Long name
                            */
                           SORT_FILTERED,

  }


  /**
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return the Comparsion Value
   */
  public int compareTo(final CdfxExportParameter param2, final SortColumns sortColumn) {
    int compareResult;
    switch (sortColumn) {
      case SORT_CHAR_NAME: // use compare method for Strings compareResult =
        compareResult = ApicUtil.compare(getA2lparam().getName(), param2.getA2lparam().getName());
        break;
      case SORT_FILTERED:
        compareResult = AbstractA2LObject.compareBoolean(isFiltered(), param2.isFiltered());
        break;
      default:
        compareResult = 0;
        break;
    }
    return compareResult;
  }


  private A2LParameter a2lparam;


  /**
   * @return the a2lparam
   */
  public A2LParameter getA2lparam() {
    return this.a2lparam;
  }


  /**
   * @param a2lparam the a2lparam to set
   */
  public void setA2lparam(final A2LParameter a2lparam) {
    this.a2lparam = a2lparam;
  }


  /**
   * @return the filtered
   */
  public String isFilteredAsStr() {
    if ("true".equalsIgnoreCase(String.valueOf(this.filtered))) {
      return "Yes";
    }
    return "No";
  }

  /**
   * @return the filtered
   */
  public boolean isFiltered() {
    return this.filtered;
  }


  /**
   * @param filtered the filtered to set
   */
  public void setFiltered(final boolean filtered) {
    this.filtered = filtered;
  }


  private boolean filtered;


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.a2lparam.getName();
  }

}
