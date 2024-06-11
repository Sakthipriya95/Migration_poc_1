/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author dmo5cob
 */
public class AttributesGridTabViewerSorter extends AbstractViewerSorter {

  /**
   * index for Sorting
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
  // Default ascending
  private int direction = ASCENDING;

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
   * {@inheritDoc} Compare for Attribute.
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

    Attribute attr1 = (Attribute) obj1;
    Attribute attr2 = (Attribute) obj2;
    AttributeClientBO attrBO1 = new AttributeClientBO(attr1);
    AttributeClientBO attrBO2 = new AttributeClientBO(attr2);
    int compare;
    switch (this.index) {
      // Attr name
      case 0:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_ATTRNAME);
        break;
      // Attr Desc
      case 1:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_ATTRDESCR);
        break;
      // Attr Value type.
      case 2:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_VALUETYPE);
        break;
      // ICDM-860
      // Attr Normalized.
      case 3:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_NORMALIZED_FLAG);
        break;
      // Attr Mandatory.
      case 4:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_MANDATORY);
        break;
      // Attr Unit.
      case 5:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_UNIT);
        break;
      // Attr format.
      case 6:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_FORMAT);
        break;
      // Attr part number.
      case 7:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_PART_NUMBER);
        break;
      // Attr Spec Link.
      case 8:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_SPEC_LINK);
        break;
      // Attr Attr class.
      case ApicUiConstants.COLUMN_INDEX_9:
        // Icdm-955 new Compare for Char name
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_CHAR);
        break;
      // Attr Internal.
      case ApicUiConstants.COLUMN_INDEX_10:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_ATTR_SEC);
        break;
      // Attr Value Internal.
      case ApicUiConstants.COLUMN_INDEX_11:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_ATTR_VAL_SEC);
        break;
      // ICDM-1560
      case ApicUiConstants.COLUMN_INDEX_12:
        compare = attrBO1.compareTo(attrBO2, ApicConstants.SORT_ATTR_EADM_NAME);
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

  @Override
  public int getDirection() {
    return this.direction;
  }

}