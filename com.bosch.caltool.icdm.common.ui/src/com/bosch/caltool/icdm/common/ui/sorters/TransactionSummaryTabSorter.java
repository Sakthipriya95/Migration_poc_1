/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cns.CnsChangeEventSummary;
import com.bosch.caltool.icdm.common.ui.views.TransactionSummaryViewPart;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author svj7cob
 */
public class TransactionSummaryTabSorter extends AbstractViewerSorter {

  /**
   * the index
   */
  private int index;
  /**
   * the ascending order
   */
  private static final int ASCENDING = 0;
  /**
   * the descending order
   */
  private static final int DESCENDING = 1;
  /**
   * the default direction given as Descending
   */
  private int direction = DESCENDING;

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
   * default compare method
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

    // get the CnsChangeEventSummary object
    final CnsChangeEventSummary evntSumryObj1 = (CnsChangeEventSummary) obj1;
    final CnsChangeEventSummary evntSumryObj2 = (CnsChangeEventSummary) obj2;

    // enable sorting of columns
    int colIndex;
    switch (this.index) {
      case TransactionSummaryViewPart.COLUMN_EVENT_ID:
        colIndex = ApicUtil.compare(evntSumryObj2.getEventID(), evntSumryObj1.getEventID());
        break;
      case TransactionSummaryViewPart.COLUMN_SERVICE_ID:
        colIndex = ApicUtil.compare(evntSumryObj2.getServiceID(), evntSumryObj1.getServiceID());
        break;
      case TransactionSummaryViewPart.COLUMN_CREATED_DATE:
        colIndex = ApicUtil.compare(evntSumryObj2.getCreatedDate(), evntSumryObj1.getCreatedDate());
        break;
      case TransactionSummaryViewPart.COLUMN_DATA_SIZE:
        colIndex = ApicUtil.compare(evntSumryObj1.getSize(), evntSumryObj2.getSize());
        break;
      case TransactionSummaryViewPart.COLUMN_CHANGE_COUNT:
        colIndex = ApicUtil.compare(evntSumryObj1.getChangeCount(), evntSumryObj2.getChangeCount());
        break;
      case TransactionSummaryViewPart.COLUMN_EVENT_SUMMARY:
        colIndex = ApicUtil.compare(evntSumryObj1.getSummary(), evntSumryObj2.getSummary());
        break;
      default:
        colIndex = TransactionSummaryViewPart.COLUMN_EVENT_ID;
    }

    // Second level sorting
    if (colIndex == TransactionSummaryViewPart.COLUMN_EVENT_ID) {
      colIndex = ApicUtil.compare(evntSumryObj2.getEventID(), evntSumryObj1.getEventID());
    }
    // If ascending order, flip the direction
    if (this.direction == ASCENDING) {
      colIndex = -colIndex;
    }
    return colIndex;
  }

  /**
   * gets the direction i.e ascending or descending
   *
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
