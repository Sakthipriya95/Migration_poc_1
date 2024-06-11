/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import java.util.Map;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * ICDM-2593 Sorter for Predefined attribute value table
 *
 * @author dja7cob
 */
public class PredefinedAttrValTabSorter extends AbstractViewerSorter {

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
   * selPredefinedAttrValMap instance
   */
  private final Map<Attribute, AttributeValue> selPredefinedAttrValMap;

  /**
   * @param selPredefinedAttrValMap instance
   */
  public PredefinedAttrValTabSorter(final Map<Attribute, AttributeValue> selPredefinedAttrValMap) {
    this.selPredefinedAttrValMap = selPredefinedAttrValMap;
  }

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

    Attribute predefAttr1 = (Attribute) obj1;
    Attribute predefAttr2 = (Attribute) obj2;

    switch (this.index) {
      // Attr col
      case 0:
        compare = CommonUtils.compareToIgnoreCase(predefAttr1.getName(), predefAttr2.getName());
        break;
      // Attr Value col
      case 1:
        if (null != this.selPredefinedAttrValMap) {
          compare = CommonUtils.compareToIgnoreCase(this.selPredefinedAttrValMap.get(predefAttr1).getName(),
              this.selPredefinedAttrValMap.get(predefAttr2).getName());
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
