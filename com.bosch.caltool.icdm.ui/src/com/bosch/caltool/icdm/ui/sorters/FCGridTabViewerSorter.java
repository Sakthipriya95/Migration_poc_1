package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function.SortColumns;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

public class FCGridTabViewerSorter extends AbstractViewerSorter {

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
    Function funcElementOne = (Function) obj1;
    Function funcElementTwo = (Function) obj2;
    int compare;
    switch (this.index) {
      case 0:
        compare = funcElementOne.compareTo(funcElementTwo, SortColumns.SORT_NAME);
        break;
      case 1:
        compare = funcElementOne.compareTo(funcElementTwo, SortColumns.SORT_VERSION);
        break;
      case 2:
        compare = funcElementOne.compareTo(funcElementTwo, SortColumns.SORT_LONGNAME);
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