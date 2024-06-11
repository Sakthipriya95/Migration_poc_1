/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author mga1cob
 */
public class PIDCAttrValGridTabViewerSorter extends AbstractViewerSorter {

  /**
   * Index
   */
  private int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * Default ascending
   */
  private int direction = ASCENDING;

  /**
   * @param index defines tableviewer column index
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
   * {@inheritDoc} Compare Method for Attr Value
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // Get the Attr Value objects.
    AttributeValue attrVal1 = (AttributeValue) obj1;
    AttributeValue attrVal2 = (AttributeValue) obj2;
    int compare;
    switch (this.index) {
      // Sort Attr Val.
      case 0:
        compare = attrVal1.compareTo(attrVal2, SortColumns.SORT_ATTR_VAL);
        break;
      // Sort Attr Val Desc.
      case 1:
        compare = attrVal1.compareTo(attrVal2, SortColumns.SORT_ATTR_VAL_DESC);
        break;
      // Icdm-832 new column added to the table
      case 2:
        compare = attrVal1.compareTo(attrVal2, SortColumns.SORT_ATTR_CLEAR_STATUS);
        break;
      // Sort Val Class .
      case ApicUiConstants.COLUMN_INDEX_3:
        compare = attrVal1.compareTo(attrVal2, SortColumns.SORT_CHAR_VALUE);
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
   * @return int defines sorting direction of tableviewercolumn
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
