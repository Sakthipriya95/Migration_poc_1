/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author say8cob
 */
public class WorkpackageTableViewerSorter extends AbstractViewerSorter {

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

    // create the compare objs
    A2lWorkPackage wp1 = (A2lWorkPackage) obj1;
    A2lWorkPackage wp2 = (A2lWorkPackage) obj2;
    int compare = 0;
    if (CommonUtils.isEqual(ApicUiConstants.COLUMN_INDEX_0, this.index)) {
      compare = compareWpName(wp1, wp2);
    }
    else {
      compare = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }

  /**
   * @param wp WorkPackage
   * @return compare result based on sort column
   */
  private int compareWpName(final A2lWorkPackage wp1, final A2lWorkPackage wp2) {
    int compareResult;
    compareResult = ModelUtil.compare(wp1.getName(), wp2.getName());
    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(wp1.getId(), wp2.getId());
    }
    return compareResult;
  }

  /**
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
