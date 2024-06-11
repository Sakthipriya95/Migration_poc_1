package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * icdm-1215 Sorter used in the Review Info page for Review attr Values
 *
 * @author rgo7cob
 */
public class RvwAttrValTabSorter extends AbstractViewerSorter {

  /**
   * Constant for Attr name Col
   */
  private static final int ATTR_NAME_COL_IDX = 0;

  /**
   * Constant for Attr desc Col
   */
  private static final int ATTR_DESC_COL_IDX = 1;
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

  /**
   * index for Sorting
   */
  private int index;


  private final ReviewResultClientBO resultData;

  /**
   * @param resultData Review Result Client BO
   */
  public RvwAttrValTabSorter(final ReviewResultClientBO resultData) {
    this.resultData = resultData;
  }

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

    RvwAttrValue rvwAttrVal1 = (RvwAttrValue) obj1;
    RvwAttrValue rvwAttrVal2 = (RvwAttrValue) obj2;
    int compRes;
    switch (this.index) {
      // Attr name
      case ATTR_NAME_COL_IDX:
        compRes = this.resultData.compareTo(rvwAttrVal1, rvwAttrVal2, ApicConstants.SORT_ATTRNAME);
        break;
      // Attr Desc
      case ATTR_DESC_COL_IDX:
        compRes = this.resultData.compareTo(rvwAttrVal1, rvwAttrVal2, ApicConstants.SORT_ATTR_VAL_SEC);
        break;

      default:
        compRes = 0;

    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compRes = -compRes;
    }
    return compRes;
  }

  @Override
  public int getDirection() {
    return this.direction;
  }

}