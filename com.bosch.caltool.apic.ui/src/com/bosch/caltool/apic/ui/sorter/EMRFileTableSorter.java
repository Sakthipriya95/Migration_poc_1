/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.emr.EMRWizardData;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author mkl2cob
 */
public class EMRFileTableSorter extends AbstractViewerSorter {

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

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {
    // initialize first EMRWizardData instance
    EMRWizardData file1 = (EMRWizardData) object1;
    // initialize second A2LFile instance
    EMRWizardData file2 = (EMRWizardData) object2;

    int compareResult;

    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // file name
        compareResult = ApicUtil.compare(file1.getFileName(), file2.getFileName());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        // file description
        compareResult = ApicUtil.compare(file1.getDescripton(), file2.getDescripton());
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        // comapre the validity
        compareResult = ApicUtil.compareBoolean(file1.isPartialPIDCScope(), file2.isPartialPIDCScope());
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
