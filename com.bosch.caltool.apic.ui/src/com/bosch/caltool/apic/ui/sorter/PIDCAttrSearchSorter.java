/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * PIDC sout attributes tree sorter
 *
 * @author bru2cob
 */
public class PIDCAttrSearchSorter extends AbstractViewerSorter {

  /**
   * Represents the column number to be sorted
   */
  private transient int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * Default diraction ascending
   */
  private transient int direction = ASCENDING;

  /**
   * Ascending or descending sorting based on direction {@inheritDoc}
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
   * Compare the attributes {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) { // NOPMD by bne4cob on 3/11/14 12:16
                                                                                  // PM
    int result = 0;
    if ((obj1 instanceof Attribute) && (obj2 instanceof Attribute)) {
      Attribute attr1 = (Attribute) obj1;
      Attribute attr2 = (Attribute) obj2;

      switch (this.index) {
        case 0:
          // first column attributes are sorted using name
          result = attr1.compareTo(attr2, ApicConstants.SORT_ATTRNAME);
          break;
        default:
          result = 0;
      }
      // If descending order, flip the direction
      if (this.direction == DESCENDING) {
        result = -result;
      }
    }
    return result;
  }

  /**
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}
