/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Icdm-521 Moved the class to common UI
 *
 * @author jvi6cob
 */
public class FCBCUsageGridTabViewerSorter extends AbstractViewerSorter {

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
   * {@inheritDoc} returns the compare result as integer value
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // initialise FCBCUsage
    FCBCUsage fcBC1 = (FCBCUsage) obj1;
    FCBCUsage fcBC2 = (FCBCUsage) obj2;
    int compare;
    switch (this.index) {
      case 0:
        // compare names
        compare = fcBC1.getName().compareTo(fcBC2.getName());
        break;
      case 1:


        // compare versions
        compare = fcBC1.getFuncVersion().compareTo(fcBC2.getFuncVersion());
        break;
      case 2:

        // compare customer name
        compare = fcBC1.getCustomerName().compareTo(fcBC2.getCustomerName());
        break;
      case 3:
        // compare vcdm aprj
        compare = fcBC1.getVcdmAprj().compareTo(fcBC2.getVcdmAprj());
        break;
      case 4:
        // compare created user
        compare = fcBC1.getCreatedUser().compareTo(fcBC2.getCreatedUser());
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
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
