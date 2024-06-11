/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter class for selection Grid table viewer
 *
 * @author bru2cob
 */
public class SelectionGridTabViewerSorter extends AbstractViewerSorter {

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
    // initialise the variant objects
    PidcVariant variant1 = (PidcVariant) objOne;
    PidcVariant variant2 = (PidcVariant) objTwo;
    int result;
    if (this.index == 0) {
      // comparing the variants
      result = variant1.compareTo(variant2);
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
