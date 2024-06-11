/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterParameter;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * ICDM-879 This class enables sorter for Paramater table in CDFX Export Dialog
 *
 * @author mkl2cob
 */
public class CDFXportParamTabSorter extends AbstractViewerSorter {

  /**
   * index
   */
  private transient int index;
  /**
   * constant for descending direction
   */
  private static final int DESCENDING = 1;
  /**
   * constant for ascending direction
   */
  private static final int ASCENDING = 0;
  /**
   * direction by default set to ascending
   */
  private transient int direction = ASCENDING;

  /**
   * {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    int compareResult = 0;
    A2LFilterParameter a2lParam1;
    A2LFilterParameter a2lParam2;

    if ((object1 instanceof A2LFilterParameter) && (object2 instanceof A2LFilterParameter)) {
      // get the first A2LFilterParameter object
      a2lParam1 = (A2LFilterParameter) object1;
      // get the second A2LFilterParameter object
      a2lParam2 = (A2LFilterParameter) object2;

      switch (this.index) {
        case 0:
          // A2l parameter full name
          compareResult = a2lParam1.compareTo(a2lParam2, A2LFilterParameter.SortColumns.SORT_CHAR_NAME);
          break;
        case 1:
          // A2l parameter unit
          compareResult = a2lParam1.compareTo(a2lParam2, A2LFilterParameter.SortColumns.SORT_CHAR_UNIT);
          break;
        case 2:
          // A2l parameter class
          compareResult = a2lParam1.compareTo(a2lParam2, A2LFilterParameter.SortColumns.SORT_CHAR_CLASS);
          break;
        case 3:
          // Min value
          compareResult = a2lParam1.compareTo(a2lParam2, A2LFilterParameter.SortColumns.SORT_MIN_VAL);
          break;
        case 4:
          // Max Value
          compareResult = a2lParam1.compareTo(a2lParam2, A2LFilterParameter.SortColumns.SORT_MAX_VAL);
          break;
        default:
          compareResult = 0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        compareResult = -compareResult;
      }

    }

    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
