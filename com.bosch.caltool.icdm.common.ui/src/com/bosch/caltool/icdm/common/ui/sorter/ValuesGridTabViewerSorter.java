package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author dmo5cob Sorter for Values Grid table
 */
public class ValuesGridTabViewerSorter extends AbstractViewerSorter {

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
    // get the objects to be compared
    AttributeValue attr1 = (AttributeValue) obj1;
    AttributeValue attr2 = (AttributeValue) obj2;
    AttributeValueClientBO bo1 = new AttributeValueClientBO(attr1);
    AttributeValueClientBO bo2 = new AttributeValueClientBO(attr2);
    int compare;
    switch (this.index) {
      // Attr Value col
      case 0:
        compare = attr1.compareTo(attr2, SortColumns.SORT_ATTR_VAL);
        break;
      // Value Unit col
      case 1:
        compare = attr1.compareTo(attr2, SortColumns.SORT_ATTR_VAL_UNIT);
        break;
      // Val Desc col
      case 2:
        compare = attr1.compareTo(attr2, SortColumns.SORT_ATTR_VAL_DESC);
        break;
      // Icdm-831 New Value for Clearing status
      case 3:
        // Sort For attr Char Value
      case CommonUIConstants.COLUMN_INDEX_4:
        compare = ApicUtil.compare(bo1.getClearingStatus().getUiText(), bo2.getClearingStatus().getUiText());
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