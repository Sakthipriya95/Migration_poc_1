/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.dialogs.OpenPointsData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * This class serves as a sorter to the open points table
 *
 * @author bru2cob
 */
public class OpenPointsTableViewerSorter extends AbstractViewerSorter {

  /**
   * index
   */
  private int index = 1;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
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
    // set the direction
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
    OpenPointsData opData1 = (OpenPointsData) obj1;
    OpenPointsData opData2 = (OpenPointsData) obj2;
    int compare;
    switch (this.index) {
      // Sort open point
      case ApicUiConstants.COLUMN_INDEX_0:
        compare = ApicUtil.compare(opData1.getOpenPoint(), opData2.getOpenPoint());
        break;
      // Sort measure
      case ApicUiConstants.COLUMN_INDEX_1:
        compare = ApicUtil.compare(opData1.getMeasures(), opData2.getMeasures());
        break;
      // Sort responsible
      case ApicUiConstants.COLUMN_INDEX_2:
        compare = ApicUtil.compare(opData1.getResponsibleName(), opData2.getResponsibleName());
        break;
      // Sort completion date
      case ApicUiConstants.COLUMN_INDEX_3:
        compare = ApicUtil.compare(opData1.getDate(), opData2.getDate());
        break;
      // Sort result
      case ApicUiConstants.COLUMN_INDEX_4:
        compare = ApicUtil.compare(opData1.resultString(), opData2.resultString());
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
