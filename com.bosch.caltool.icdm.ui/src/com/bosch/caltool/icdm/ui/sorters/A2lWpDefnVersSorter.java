package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter Class
 *
 * @author pdh2cob
 */
public class A2lWpDefnVersSorter extends AbstractViewerSorter {

  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;

  /**
   * index for Sorting
   */
  private int index;

  /**
   * Default ascending
   */
  private int direction = ASCENDING;

  /**
   * {@inheritDoc}
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
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

    // initialize first A2LFile instance
    A2lWpDefnVersion a2lWpDefnVers1 = (A2lWpDefnVersion) obj1;
    // initialize second A2LFile instance
    A2lWpDefnVersion a2lWpDefnVers2 = (A2lWpDefnVersion) obj2;

    int compareResult = 0;

    switch (this.index) {
      case 0:
        compareResult = a2lWpDefnVers1.getVersionNumber().compareTo(a2lWpDefnVers2.getVersionNumber());
        break;
      case 1:
        compareResult = a2lWpDefnVers1.getName().compareTo(a2lWpDefnVers2.getName());
        break;
      case 2:
        compareResult = a2lWpDefnVers1.getDescription().compareTo(a2lWpDefnVers2.getDescription());
        break;
      case 4:
        compareResult = ApicUtil.compareBoolean(a2lWpDefnVers1.isActive(), a2lWpDefnVers2.isActive());
        break;
      case 5:
        compareResult = a2lWpDefnVers1.getCreatedDate().compareTo(a2lWpDefnVers2.getCreatedDate());
        break;
      case 6:
        compareResult = a2lWpDefnVers1.getCreatedUser().compareTo(a2lWpDefnVers2.getCreatedUser());
        break;
      default:
        compareResult = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compareResult = -compareResult;
    }
    return compareResult;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
