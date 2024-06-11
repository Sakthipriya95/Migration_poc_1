/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author bru2cob
 */
public class PverVariantSorter extends AbstractViewerSorter {

  /**
   *
   */
  private int index;
  /**
   *
   */
  private static final int DESCENDING = 1;
  /**
   *
   */
  private static final int ASCENDING = 0;
  /**
   *
   */
  private int direction = ASCENDING;

  /**
   * {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    // Set the type of sort
    // Ascending or Descending
    if (index == this.index) {

      // Descending sort
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }

  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // Pver variants
    final String variant1 = (String) obj1;
    final String variant2 = (String) obj2;
    int compare;

    // Compare two pver variants
    if (this.index == 0) {
      compare = variant1.compareTo(variant2);
    }
    else {
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
