/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author hnu1cob
 */
public class FunctionGridTabViewerSorter extends AbstractViewerSorter {


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
    Function function1 = (Function) obj1;
    Function function2 = (Function) obj2;
    int compare;
    switch (this.index) {
      case 0:
        compare = ModelUtil.compare(function1.getName(), function2.getName());
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
