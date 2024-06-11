/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * ICDM-2593 Sorter for adding Predefined attribute dialog table
 *
 * @author dja7cob
 */
public class PredefinedAttrListTabSorter extends AbstractViewerSorter {

  /**
   * index - Column number
   */
  private int index;
  /**
   * constant for descending sort
   */
  private static final int DESCENDING = 1;
  /**
   * constant for ascending sort
   */
  private static final int ASCENDING = 0;
  // Default is ascending sort dirextion
  private int direction = ASCENDING;

  /**
   * {@inheritDoc} set the direction
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
   * Compare method for comparing the objects (AttributeValue) equality. {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int compare = 0;

    Attribute attr1 = (Attribute) obj1;
    Attribute attr2 = (Attribute) obj2;

    switch (this.index) {
      // Attr Value col
      case 0:
        compare = CommonUtils.compareToIgnoreCase(attr1.getName(), attr2.getName());
        break;
      // Attr Value col
      case 1:
        if (!CommonUtils.isEmptyString(attr1.getDescription()) && !CommonUtils.isEmptyString(attr2.getDescription())) {
          compare = CommonUtils.compareToIgnoreCase(attr1.getDescription(), attr2.getDescription());
        }
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

  /**
   * return the direction. {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}
