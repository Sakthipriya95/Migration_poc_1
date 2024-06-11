/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import java.util.Calendar;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewDetail;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author adn1cob
 */
public class ReviewPIDCDetailsTableSorter extends AbstractViewerSorter {

  // index
  private int index;
  // declare descending
  private static final int DESCENDING = 1;
  // declare ascending
  private static final int ASCENDING = 0;
  // default ascending
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
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // get the review detail object
    final ReviewDetail rvwRes1 = (ReviewDetail) obj1;
    final ReviewDetail rvwRes2 = (ReviewDetail) obj2;
    // Convert to calender for comparison
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(rvwRes1.getDateOfReview());

    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(rvwRes2.getDateOfReview());
    // enable sorting of columns
    int colIndex;
    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // compare pidc name
        colIndex = ApicUtil.compare(rvwRes1.getPidcName(), rvwRes2.getPidcName());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        // compare variant name
        colIndex = ApicUtil.compare(rvwRes1.getVariantName(), rvwRes2.getVariantName());
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        // compare date of review
        colIndex = ApicUtil.compare(cal1, cal2);
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        // compare review result
        colIndex = ApicUtil.compare(rvwRes1.getReviewResult(), rvwRes2.getReviewResult());
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        // compare review comment
        colIndex = ApicUtil.compare(rvwRes1.getReviewComment(), rvwRes2.getReviewComment());
        break;

      default:
        colIndex = 0;
    }
    // Second level sorting with variant name
    if (colIndex == 0) {
      colIndex = ApicUtil.compare(rvwRes1.getVariantName(), rvwRes2.getVariantName());
    }
    // Third level sorting with date of review
    if (colIndex == 0) {
      colIndex = ApicUtil.compare(cal2, cal1);
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
