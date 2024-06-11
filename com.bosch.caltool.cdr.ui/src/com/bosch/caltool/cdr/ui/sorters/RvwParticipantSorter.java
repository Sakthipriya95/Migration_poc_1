/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.RvwParticipant;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author NIP4COB
 */
public class RvwParticipantSorter extends AbstractViewerSorter {

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

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }


  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    RvwParticipant participant1 = (RvwParticipant) obj1;
    RvwParticipant participant2 = (RvwParticipant) obj2;
    int compareResult;

    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        compareResult = ApicUtil.compare(participant2.getName(), participant1.getName());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        compareResult = ApicUtil.compareBoolean(participant1.isEditFlag(), participant2.isEditFlag());
        break;
      default:
        compareResult = CommonUIConstants.COLUMN_INDEX_0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compareResult = -compareResult;
    }
    return compareResult;
  }
}
