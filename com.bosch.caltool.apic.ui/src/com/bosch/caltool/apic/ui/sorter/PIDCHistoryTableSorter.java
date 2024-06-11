/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author dmo5cob
 */
public class PIDCHistoryTableSorter extends AbstractViewerSorter {

  // index
  private int index;
  // declare descending
  private static final int DESCENDING = 1;
  // declare ascending
  private static final int ASCENDING = 0;
  // default ascending
  private int direction = DESCENDING;

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
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // get the AttrDiffType object
    final AttrDiffType rvwRes1 = (AttrDiffType) obj1;
    final AttrDiffType rvwRes2 = (AttrDiffType) obj2;
    // enable sorting of columns
    int colIndex = 0;
    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // sort by no:
        colIndex = ApicUtil.compare(((AttrDiffType) obj2).getVersionId(), ((AttrDiffType) obj1).getVersionId());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        // sort by pidc version
        colIndex = ApicUtil.compare(((AttrDiffType) obj2).getPidcversion(), ((AttrDiffType) obj1).getPidcversion());
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        // sort by level
        colIndex = ApicUtil.compare(rvwRes1.getLevel(), rvwRes2.getLevel());
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        // sort by attr name
        colIndex = ApicUtil.compare(rvwRes1.getAttribute().getNameEng(), rvwRes2.getAttribute().getNameEng());
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        // sort by change item
        colIndex = ApicUtil.compare(rvwRes1.getChangedItem(), rvwRes2.getChangedItem());
        break;
      case CommonUIConstants.COLUMN_INDEX_5:
        // sort by change item
        colIndex = ApicUtil.compare(rvwRes1.getOldValue(), rvwRes2.getOldValue());
        break;
      case CommonUIConstants.COLUMN_INDEX_6:
        // sort by change item
        colIndex = ApicUtil.compare(rvwRes1.getNewValue(), rvwRes2.getNewValue());
        break;
      case CommonUIConstants.COLUMN_INDEX_7:
        // sort by change item
        colIndex = ApicUtil.compare(rvwRes1.getModifiedName(), rvwRes2.getModifiedName());
        break;
      case CommonUIConstants.COLUMN_INDEX_8:
        // sort by change item
        colIndex = doSortingByModifiedDate(rvwRes1, rvwRes2);
        break;
      default:
        colIndex = CommonUIConstants.COLUMN_INDEX_0;
    }
    // Second level sorting
    if (colIndex == CommonUIConstants.COLUMN_INDEX_0) {
      // sort by number
      colIndex = ApicUtil.compare(((AttrDiffType) obj2).getVersionId(), ((AttrDiffType) obj1).getVersionId());
    }
    // If ascending order, flip the direction
    if (this.direction == ASCENDING) {
      colIndex = -colIndex;
    }
    return colIndex;
  }

  /**
   * @param rvwRes1
   * @param rvwRes2
   * @param colIndex
   * @return
   */
  private int doSortingByModifiedDate(final AttrDiffType rvwRes1, final AttrDiffType rvwRes2) {
    int colIndex = 0;
    try {
      colIndex = ApicUtil.compareCalendar(
          DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, rvwRes1.getModifiedDate()),
          DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, rvwRes2.getModifiedDate()));
    }
    catch (IcdmException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return colIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
