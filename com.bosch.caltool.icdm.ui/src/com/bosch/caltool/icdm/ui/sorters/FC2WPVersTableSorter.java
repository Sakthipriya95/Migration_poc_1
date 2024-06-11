/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.dialogs.FC2WPVersionSelDialog;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author bru2cob
 */
public class FC2WPVersTableSorter extends AbstractViewerSorter {


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
   * instance of FC2WPVersionSelDialog
   */
  private final FC2WPVersionSelDialog fc2wpVersionSelDialog;

  /**
   * @param fc2wpVersionSelDialog
   */
  public FC2WPVersTableSorter(final FC2WPVersionSelDialog fc2wpVersionSelDialog) {
    this.fc2wpVersionSelDialog = fc2wpVersionSelDialog;
  }

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
    FC2WPVersion fc2wp1 = (FC2WPVersion) obj1;
    FC2WPVersion fc2wp2 = (FC2WPVersion) obj2;
    int compare;
    switch (this.index) {
      // compare the name
      case 0:
        compare = ApicUtil.compare(fc2wp1.getName().substring(0, fc2wp1.getName().indexOf('(')),
            fc2wp2.getName().substring(0, fc2wp2.getName().indexOf('(')));
        break;
      // compare the division name
      case 1:
        compare = ApicUtil.compare(
            FC2WPVersTableSorter.this.fc2wpVersionSelDialog.getAllVersionsMap().get(fc2wp1).getDivisionName(),
            FC2WPVersTableSorter.this.fc2wpVersionSelDialog.getAllVersionsMap().get(fc2wp2).getDivisionName());
        break;
      // compare the version name
      case 2:
        compare = ApicUtil.compare(fc2wp1.getVersionName(), fc2wp2.getVersionName());
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
