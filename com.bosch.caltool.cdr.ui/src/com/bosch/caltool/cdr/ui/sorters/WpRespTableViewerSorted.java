/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author DMR1COB
 */
public class WpRespTableViewerSorted extends AbstractViewerSorter {

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
  public int compare(final Viewer viewer, final Object objOne, final Object objTwo) {
    // initialise the wp resp name
    String wpRespName1 = (String) objOne;
    String wpRespName2 = (String) objTwo;
    int result;
    if (this.index == 0) {
      // comparing the variants
      result = wpRespName1.compareTo(wpRespName2);
    }
    else {
      result = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      result = -result;
    }
    return result;
  }

  /**
   * @return int defines direction
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
