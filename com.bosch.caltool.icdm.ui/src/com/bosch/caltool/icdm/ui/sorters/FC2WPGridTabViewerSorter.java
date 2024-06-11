/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author bru2cob
 *
 */
public class FC2WPGridTabViewerSorter extends AbstractViewerSorter {


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
    FC2WPDef fc2wpDef1 = (FC2WPDef) obj1;
    FC2WPDef fc2wpDef2 = (FC2WPDef) obj2;
    int compare;
    switch (this.index) {
      case 0:
        compare = fc2wpDef1.compareTo(fc2wpDef2,FC2WPDef.SortColumns.SORT_NAME);
        break;
      case 1:
        compare = fc2wpDef1.compareTo(fc2wpDef2,FC2WPDef.SortColumns.SORT_DIV_NAME);
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
