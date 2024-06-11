/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterBaseComponents;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author bru2cob
 */
// ICDM-1011
public class CDFXportBCTabSorter extends AbstractViewerSorter {

  /**
   * Represents the column number to be sorted
   */
  private int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * Default diraction ascending
   */
  private int direction = ASCENDING;

  /**
   * @param index defines grid tableviewercolumn index
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
   * Compares two bc's
   */
  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    int compareResult = 0;
    /**
     * Objects to be sorted
     */
    A2LFilterBaseComponents a2lBC1;
    A2LFilterBaseComponents a2lBC2;

    if ((object1 instanceof A2LFilterBaseComponents) && (object2 instanceof A2LFilterBaseComponents)) {
      a2lBC1 = (A2LFilterBaseComponents) object1;
      a2lBC2 = (A2LFilterBaseComponents) object2;

      switch (this.index) {
        case 0:
          // bc full name
          compareResult = a2lBC1.compareTo(a2lBC2, A2LFilterBaseComponents.SortColumns.SORT_CHAR_NAME);
          break;
        case 1:
          // bc version
          compareResult = a2lBC1.compareTo(a2lBC2, A2LFilterBaseComponents.SortColumns.SORT_CHAR_VERSION);
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
   * @return direction of sort
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
