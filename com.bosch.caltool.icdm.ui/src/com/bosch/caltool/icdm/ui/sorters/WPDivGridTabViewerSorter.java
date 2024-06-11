/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author bru2cob
 */
public class WPDivGridTabViewerSorter extends AbstractViewerSorter {


  /**
   * Index
   */
  private int index;
  /**
   * DESCENDING
   */
  private static final int DESCENDING = 1;
  /**
   * ASCENDING
   */
  private static final int ASCENDING = 0;
  /**
   * direction
   */
  private int direction = ASCENDING;

  /**
   * @param index defines grid tableviewercolumn index
   */
  @Override
  public void setColumn(final int index) {
    // set the direction of sorting
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    // Ascending order
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  @Override
  public int getDirection() {
    return this.direction;
  }


  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    WorkPackageDetailsWrapper wp1 = (WorkPackageDetailsWrapper) obj1;
    WorkPackageDetailsWrapper wp2 = (WorkPackageDetailsWrapper) obj2;
    int compare;
    switch (this.index) {
      case 0:
        compare = wp1.compareTo(wp2, WorkPackageDetailsWrapper.SortColumns.SORT_DIVISION);
        break;
      case 1:
        compare = wp1.compareTo(wp2, WorkPackageDetailsWrapper.SortColumns.SORT_RESOURCE);
        break;
      case 2:
        compare = wp1.compareTo(wp2, WorkPackageDetailsWrapper.SortColumns.SORT_MCR);
        break;
      case 3:
        compare = wp1.compareTo(wp2, WorkPackageDetailsWrapper.SortColumns.SORT_PRIMARY_CONTACT);
        break;
      case 4:
        compare = wp1.compareTo(wp2, WorkPackageDetailsWrapper.SortColumns.SORT_SEC_CONTACT);
        break;
      default:
        compare = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }


}
