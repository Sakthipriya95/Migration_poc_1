/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterFunction;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * This class enables sorter for FC table in CDFX Export Dialog
 *
 * @author dmo5cob
 */
public class CDFXportFCTabSorter extends AbstractViewerSorter {

  /**
   * counter
   */
  private transient int index;
  /**
   * direction descending
   */
  private static final int DESCENDING = 1;
  /**
   * direction ascending
   */
  private static final int ASCENDING = 0;
  /**
   * direction
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

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    int compareResult = 0;
    A2LFilterFunction a2lfc1;
    A2LFilterFunction a2lfc2;

    if ((object1 instanceof A2LFilterFunction) && (object2 instanceof A2LFilterFunction)) {
      a2lfc1 = (A2LFilterFunction) object1;
      a2lfc2 = (A2LFilterFunction) object2;

      switch (this.index) {
        case 0:
          // A2l FC full name
          compareResult = a2lfc1.compareTo(a2lfc2, A2LFilterFunction.SortColumns.SORT_FUNC_NAME);
          break;
        case 1:
          // A2l FC version
          compareResult = a2lfc1.compareTo(a2lfc2, A2LFilterFunction.SortColumns.SORT_FUNC_VRSN);
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
