/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.comppkg.ui.wizards.ReleaseErrorModel;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * Error viewer sorter <br>
 * This class is to sort the table in <br>
 * Summary page of Get component package data wizard
 */
public class ReleaseErrorViewSorter extends AbstractViewerSorter {

  /**
   * Column index
   */
  private int index;
  /**
   * Define default order as ascending
   */
  private int direction = ApicConstants.SORT_ORDER_ASC;

  /**
   * {@inheritDoc}
   *
   * @param index defines grid tableviewercolumn index
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ApicConstants.SORT_ORDER_ASC;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // get the error model
    final ReleaseErrorModel errObj1 = (ReleaseErrorModel) obj1;
    final ReleaseErrorModel errObj2 = (ReleaseErrorModel) obj2;
    int result;
    switch (this.index) {
      case 0:
        // compares with error message description
        result = errObj1.compareTo(errObj2);
        break;
      case 1:
        // compare with label count
        result = ApicUtil.compare(Long.valueOf(errObj1.getLabelCount()), Long.valueOf(errObj2.getLabelCount()));
        break;
      default:
        result = 0;
    }
    // If descending order, flip the direction
    if (this.direction == ApicConstants.SORT_ORDER_DESC) {
      result = -result;
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
