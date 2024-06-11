/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter for IBasicObject
 *
 * @author rgo7cob
 * @param <D> IBasicObject
 */
public class BasicObjectViewerSorter<D extends IBasicObject> extends AbstractViewerSorter {

  /**
   * Represents the column number to be sorted
   */
  protected int index;

  /**
   * Default diraction ascending
   */
  protected int direction = ApicConstants.ASCENDING;


  /**
   * index defines grid tableviewercolumn index {@inheritDoc}
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
   * Compares two <code>IBasicObject</code> instances
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int result = 0;

    // both objects being compared should be of type IBasicObject
    if ((obj1 instanceof IBasicObject) && (obj2 instanceof IBasicObject)) {
      @SuppressWarnings("unchecked")
      D basObj1 = (D) obj1;
      @SuppressWarnings("unchecked")
      D basObj2 = (D) obj2;

      switch (this.index) {
        case CommonUIConstants.COLUMN_INDEX_0:
          // Compare name
          result = ApicUtil.compare(basObj1.getName(), basObj2.getName());
          break;

        case CommonUIConstants.COLUMN_INDEX_1:
          // Compare description
          result = ApicUtil.compare(basObj1.getDescription(), basObj2.getDescription());
          break;

        default:
          // if no column available
          result = 0;
          break;
      }
      // If descending order, flip the direction
      if (this.direction == ApicConstants.DESCENDING) {
        result = -result;
      }
    }
    return result;
  }


  /**
   * @return defines direction of sort
   */
  @Override
  public int getDirection() {
    return this.direction;
  }


}
