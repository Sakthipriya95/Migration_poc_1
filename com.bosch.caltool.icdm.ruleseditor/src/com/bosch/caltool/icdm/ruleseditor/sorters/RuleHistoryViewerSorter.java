/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author mkl2cob
 */
public class RuleHistoryViewerSorter extends AbstractViewerSorter {

  private int index = 1;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = DESCENDING;

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
      this.direction = ASCENDING;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) { // NOPMD by bne4cob on 3/11/14 12:16
                                                                                  // PM
    final ReviewRule rule1 = (ReviewRule) obj1;
    final ReviewRule rule2 = (ReviewRule) obj2;
    int result;
    switch (this.index) {
      case 1:
        result = ApicUtil.compare(rule1.getRevId(), rule2.getRevId());
        break;
      case 2:
        result = ApicUtil.compare(rule1.getRuleCreatedUser(), rule2.getRuleCreatedUser());
        break;
      case 3:
        result = ApicUtil.compare(rule1.getRuleCreatedDate(), rule2.getRuleCreatedDate());
        break;
      default:
        result = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
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
