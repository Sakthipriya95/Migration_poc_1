/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.views.data.LinkData;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * This class serves as a sorter to the link table
 *
 * @author mkl2cob
 */
public class LinkTableSorter extends AbstractViewerSorter {

  /**
   * column index
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
   * @param index defines tableviewercolumn index
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
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    LinkData link1 = (LinkData) obj1;
    LinkData link2 = (LinkData) obj2;
    int compare;
    switch (this.index) {
      case 0:
        // link column
        compare = link1.getNewLink().compareTo(link2.getNewLink());
        break;
      case 1:
        // description English column
        compare = link1.getNewDescEng().compareTo(link2.getNewDescEng());
        break;
      case 2:
        // description German column
        compare = link1.getNewDescGer().compareTo(link2.getNewDescGer());
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

  /**
   * @return int defines direction
   */
  @Override
  public int getDirection() {
    return this.direction;
  }


}
