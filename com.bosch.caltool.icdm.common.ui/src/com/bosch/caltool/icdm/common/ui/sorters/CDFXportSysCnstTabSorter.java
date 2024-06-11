/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues;
import com.bosch.caltool.icdm.model.a2l.A2LSystemConstantValues.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * This class enables sorter for System constant table in CDFX Export Dialog
 *
 * @author mkl2cob
 */
public class CDFXportSysCnstTabSorter extends AbstractViewerSorter {

  /**
   * index
   */
  private int index;
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
  private int direction = ASCENDING;

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
    A2LSystemConstantValues a2lSysCnst1;
    A2LSystemConstantValues a2lSysCnst2;

    if ((object1 instanceof A2LSystemConstantValues) && (object2 instanceof A2LSystemConstantValues)) {
      // initialising A2LSystemConstantValues
      a2lSysCnst1 = (A2LSystemConstantValues) object1;
      a2lSysCnst2 = (A2LSystemConstantValues) object2;

      switch (this.index) {
        case 0:
          // A2l system constant name
          compareResult = a2lSysCnst1.getSysconName().compareTo(a2lSysCnst2.getSysconName());
          break;
        case 1:
          // A2l system constant value
          compareResult = a2lSysCnst1.compareTo(a2lSysCnst2, SortColumns.SORT_VALUE);
          break;
        default:
          // by default, return compare value as equal
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
