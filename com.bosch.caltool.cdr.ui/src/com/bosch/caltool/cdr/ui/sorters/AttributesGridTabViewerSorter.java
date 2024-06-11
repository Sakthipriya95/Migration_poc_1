package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cdr.QuesDepnAttributeRow;
import com.bosch.caltool.icdm.client.bo.cdr.QuesDepnAttributeRow.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * Attribute table viewer sorter class
 *
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
    // get attribute 1
    QuesDepnAttributeRow attr1 = (QuesDepnAttributeRow) obj1;
    // get attribute 2
    QuesDepnAttributeRow attr2 = (QuesDepnAttributeRow) obj2;
    int compare;
    switch (this.index) {
      // Attr name
      case 0:
        compare = attr1.compareTo(attr2, SortColumns.SORT_ATTR_NAME);
        break;
      // Attr Desc
      case 1:
        compare = attr1.compareTo(attr2, SortColumns.SORT_ATTR_DESC);
        break;
      case 2:
        compare = attr1.compareTo(attr2, SortColumns.SORT_UNIT);
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