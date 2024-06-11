package com.bosch.caltool.icdm.ruleseditor.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * Icdm-1088 Sorter for Add attr Dialog
 *
 * @author rgo7cob
 */
public class AddAttrTabViewerSorter extends AbstractViewerSorter {

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
    int compRes;
    switch (this.index) {
      // Attr name
      case 0:
        compRes = attr1.compareTo(attr2, ApicConstants.SORT_ATTRNAME);
        break;
      // Attr Desc
      case 1:
        compRes = attr1.compareTo(attr2, ApicConstants.SORT_ATTRDESCR);
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