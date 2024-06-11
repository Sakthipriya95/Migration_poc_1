/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author dja7cob
 */
public class WorkPackageTableSorter extends AbstractViewerSorter {

  /**
   * index - Column number
   */
  private int index;
  /**
   * constant for descending sort
   */
  private static final int DESCENDING = 1;
  /**
   * constant for ascending sort
   */
  private static final int ASCENDING = 0;
  // Default is ascending sort dirextion
  private int direction = ASCENDING;

  /**
   * @param selValidityAttrValues instance
   */
  public WorkPackageTableSorter() {

  }

  /**
   * {@inheritDoc} set the direction
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
   * Compare method for comparing the objects (AttributeValue) equality.{@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int compare;
    WorkPackageDivision wp1 = (WorkPackageDivision) obj1;
    WorkPackageDivision wp2 = (WorkPackageDivision) obj2;
    switch (this.index) {
      // wp name col
      case 0:
        compare = CommonUtils.compareToIgnoreCase(wp1.getWpName(), wp2.getWpName());
        break;
      // wp desc col
      case 1:
        compare = CommonUtils.compareToIgnoreCase(wp1.getWpDesc(), wp2.getWpDesc());

        break;
      // wp resource col
      case 2:
        compare = CommonUtils.compareToIgnoreCase(wp1.getWpResource(), wp2.getWpResource());
        break;
      case 3:
        compare = CommonUtils.compareToIgnoreCase(wp1.getWpGroup(), wp2.getWpGroup());
        break;

      default:
        compare = 0;

    }

    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }

  /**
   * return the direction. {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}
