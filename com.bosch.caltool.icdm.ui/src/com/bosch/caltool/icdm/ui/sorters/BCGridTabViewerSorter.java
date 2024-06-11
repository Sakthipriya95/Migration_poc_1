package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

public class BCGridTabViewerSorter extends AbstractViewerSorter {

  private int index;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
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

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    A2LBaseComponents bcElementOne = (A2LBaseComponents) obj1;
    A2LBaseComponents bcElementTwo = (A2LBaseComponents) obj2;
    int compare;
    switch (this.index) {
      case 0:
        compare = bcElementOne.compareTo(bcElementTwo, SortColumns.SORT_NAME);
        break;
      case 1:
        compare = bcElementOne.compareTo(bcElementTwo, SortColumns.SORT_VERSION);
        break;
      case 2:
        compare = bcElementOne.compareTo(bcElementTwo, SortColumns.SORT_REVISION);
        break;
      case 3:
        compare = bcElementOne.compareTo(bcElementTwo, SortColumns.SORT_STATE);
        break;
      case 4:
        compare = bcElementOne.compareTo(bcElementTwo, SortColumns.SORT_LONG_NAME);
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