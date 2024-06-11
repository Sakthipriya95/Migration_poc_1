/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author NIP4COB
 */
public class ResponsibleSorter extends AbstractViewerSorter {

  /**
   * Represents the column number to be sorted
   */
  private int index;

  /**
   * Default direction ascending
   */
  private int direction = ApicConstants.ASCENDING;


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
      this.direction = ApicConstants.ASCENDING;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int result = 0;
    if ((obj1 instanceof A2lResponsibility) && (obj2 instanceof A2lResponsibility)) {
      A2lResponsibility resp1 = (A2lResponsibility) obj1;
      A2lResponsibility resp2 = (A2lResponsibility) obj2;
      switch (this.index) {
        case CommonUIConstants.COLUMN_INDEX_0:
          result = ApicUtil.compare(resp1.getLFirstName(), resp2.getLFirstName());
          break;
        case CommonUIConstants.COLUMN_INDEX_1:
          result = ApicUtil.compare(resp1.getLLastName(), resp2.getLLastName());
          break;
        case CommonUIConstants.COLUMN_INDEX_2:
          result = ApicUtil.compare(resp1.getLDepartment(), resp2.getLDepartment());
          break;
        case CommonUIConstants.COLUMN_INDEX_4:
          result = ApicUtil.compare(resp1.getAliasName(), resp2.getAliasName());
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
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
