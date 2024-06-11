/*
 * /* Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;
import com.bosch.caltool.icdm.ui.dialogs.WPDivDetailsDialog;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author apj4cob
 */
public class WPDivCdlTableSorter extends AbstractViewerSorter {

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
  private final WPDivDetailsDialog wpDetailsDialog;

  /**
   * @param wpDetailsDialog
   */
  public WPDivCdlTableSorter(final WPDivDetailsDialog wpDetailsDialog) {
    super();
    this.wpDetailsDialog = wpDetailsDialog;
  }

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
    WorkpackageDivisionCdl cdl1 = (WorkpackageDivisionCdl) obj1;
    WorkpackageDivisionCdl cdl2 = (WorkpackageDivisionCdl) obj2;
    int compare = 0;
    switch (this.index) {
      case 0:
        // region column
        String regName1 = this.wpDetailsDialog.getRegionMap().get(cdl1.getRegionId()).getRegionName();
        String regName2 = this.wpDetailsDialog.getRegionMap().get(cdl2.getRegionId()).getRegionName();
        compare = regName1.compareTo(regName2);
        break;
      case 1:
        // cdl column
        String user1 = this.wpDetailsDialog.getUserMap().get(cdl1.getUserId()).getDescription();
        String user2 = this.wpDetailsDialog.getUserMap().get(cdl1.getUserId()).getDescription();
        compare = user1.compareTo(user2);
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

