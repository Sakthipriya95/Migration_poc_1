/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author mkl2cob
 */
public class EMRErrorTableSorter extends AbstractViewerSorter {

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
    Entry<EmrFile, EmrUploadError> error1 = (Entry<EmrFile, EmrUploadError>) object1;
    // initialize second A2LFile instance
    Entry<EmrFile, EmrUploadError> error2 = (Entry<EmrFile, EmrUploadError>) object2;

    int compareResult;

    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // file name
        compareResult = ApicUtil.compare(error1.getKey().getName(), error2.getKey().getName());
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        // row number
        compareResult = ApicUtil.compare(error1.getValue().getRowNumber(), error2.getValue().getRowNumber());
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        // error category
        compareResult = ApicUtil.compare(error1.getValue().getErrorCategory(), error2.getValue().getErrorCategory());
        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        // error data
        compareResult = ApicUtil.compare(error1.getValue().getErrorData(), error2.getValue().getErrorData());
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        // error message
        compareResult = ApicUtil.compare(error1.getValue().getErrorMessage(), error2.getValue().getErrorMessage());
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
