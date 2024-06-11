/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResult;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.data.ReviewResultTableData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * iCDM-713 <br>
 *
 * @author adn1cob
 */
public class ReviewDataResultTableSorter extends AbstractViewerSorter {

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
    // get the review detail object
    final ReviewResult rvwRes1 = ((ReviewResultTableData) obj1).getReviewResult();
    final ReviewResult rvwRes2 = ((ReviewResultTableData) obj2).getReviewResult();
    // enable sorting of columns
    int colIndex;
    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // sort by serial index
        colIndex = ApicUtil.compareInt(((ReviewResultTableData) obj2).getSerialIndex(),
            ((ReviewResultTableData) obj1).getSerialIndex());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        // sort by number of records
        colIndex = ApicUtil.compare(rvwRes1.getNumberOfRecords(), rvwRes2.getNumberOfRecords());
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        // sort by check value STRING
        colIndex = ApicUtil.compare(rvwRes1.getCheckedValueString(), rvwRes2.getCheckedValueString());
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        // sort by unit
        colIndex = ApicUtil.compare(rvwRes1.getUnit(), rvwRes2.getUnit());
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        // sort by in graph flag
        colIndex = ApicUtil.compareBoolean(((ReviewResultTableData) obj1).isInGraph(),
            ((ReviewResultTableData) obj2).isInGraph());
        break;

      default:
        colIndex = CommonUIConstants.COLUMN_INDEX_0;
    }
    // Second level sorting
    if (colIndex == CommonUIConstants.COLUMN_INDEX_0) {
      // sort by number of records
      colIndex = ApicUtil.compare(rvwRes1.getNumberOfRecords(), rvwRes2.getNumberOfRecords());
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      colIndex = -colIndex;
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
