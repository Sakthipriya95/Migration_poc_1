/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author rgo7cob
 */
// ICDM-1371
public class RuleSetTabSorter extends AbstractViewerSorter {

  /**
   * Represents the column number to be sorted
   */
  private transient int index;

  /**
   * Default diraction ascending
   */
  private transient int direction = ApicConstants.ASCENDING;

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
      this.direction = ApicConstants.ASCENDING;
    }
  }

  /**
   * Compares two Rule Sets based on the name
   */
  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    int compareResult = 0;
    /**
     * rule set obj1
     */
    RuleSet ruleSet1;
    /**
     * rule set obj2
     */
    RuleSet ruleSet2;

    if ((object1 instanceof RuleSet) && (object2 instanceof RuleSet)) {
      ruleSet1 = (RuleSet) object1;
      ruleSet2 = (RuleSet) object2;

      switch (this.index) {
        case 0:
          // bc full name
          compareResult = ruleSet1.compareTo(ruleSet2);
          break;

        default:
          compareResult = 0;
      }
      // If descending order, flip the direction
      if (this.direction == ApicConstants.DESCENDING) {
        compareResult = -compareResult;
      }

    }

    return compareResult;
  }

  /**
   * @return direction of sort
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
