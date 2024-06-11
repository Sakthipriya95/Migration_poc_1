/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import java.util.SortedSet;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * ICDM-2593 Table sorter for validity attribute value selection table in AddValidityAttrValDialog
 *
 * @author dja7cob
 */
public class ValidityAttrValTableSorter extends AbstractViewerSorter {

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
   * Set of selected validity attribute values
   */
  private final SortedSet<AttributeValue> selValidityAttrValues;


  /**
   * @param selValidityAttrValues instance
   */
  public ValidityAttrValTableSorter(final SortedSet<AttributeValue> selValidityAttrValues) {
    this.selValidityAttrValues = selValidityAttrValues;
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
   * Compare method for comparing the objects (AttributeValue) equality.{@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    int compare = 0;
    AttributeValue validityVal1 = (AttributeValue) obj1;
    AttributeValue validityVal2 = (AttributeValue) obj2;
    switch (this.index) {
      // Attr Value col
      case 0:
        compare = CommonUtils.compareToIgnoreCase(validityVal1.getName(), validityVal2.getName());
        break;
      // Attr Value desc col
      case 1:
        compare = compareDesc(compare, validityVal1, validityVal2);
        break;
      // Attr Value check box col
      case 2:
        compare = compVal(compare, validityVal1, validityVal2);
        break;
      default:
        compare = 0;

    }

    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }

  /**
   * @param compare
   * @param validityVal1
   * @param validityVal2
   * @return
   */
  private int compVal(int compare, final AttributeValue validityVal1, final AttributeValue validityVal2) {
    if (null != this.selValidityAttrValues) {
      // Compare whether validity value is present in the selected values list
      // checked or unchecked
      compare = ApicUtil.compareBoolean(this.selValidityAttrValues.contains(validityVal1),
          this.selValidityAttrValues.contains(validityVal2));
    }
    return compare;
  }

  /**
   * @param compare
   * @param validityVal1
   * @param validityVal2
   * @return
   */
  private int compareDesc(int compare, final AttributeValue validityVal1, final AttributeValue validityVal2) {
    if (!CommonUtils.isEmptyString(validityVal1.getDescription()) &&
        !CommonUtils.isEmptyString(validityVal2.getDescription())) {
      compare = CommonUtils.compareToIgnoreCase(validityVal1.getDescription(), validityVal2.getDescription());
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
