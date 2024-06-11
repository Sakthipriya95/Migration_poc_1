package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * Sorter class for Access rights grid table viewer
 *
 * @author apj4cob
 */
public class GroupNodeAccessGridTabViewerSorter extends AbstractViewerSorter {

  /**
   * table index
   */
  private transient int index;
  /**
   * descending constant
   */
  private static final int DESCENDING = 1;
  /**
   * ascending constant
   */
  private static final int ASCENDING = 0;
  /**
   * direction
   */
  private transient int direction = ASCENDING;

  /**
   */
  public GroupNodeAccessGridTabViewerSorter() {}

  /**
   * @param index int
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


  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    ActiveDirectoryGroupNodeAccess nodeAccess1 = (ActiveDirectoryGroupNodeAccess) obj1;
    ActiveDirectoryGroupNodeAccess nodeAccess2 = (ActiveDirectoryGroupNodeAccess) obj2;

    int compare;

    switch (this.index) {
      // compare full name
      case 0:
        compare = ApicUtil.compare(nodeAccess1.getAdGroup().getGroupName(), nodeAccess2.getAdGroup().getGroupName());
        break;
      // compare read access
      case 1:
        compare = ApicUtil.compareBoolean(nodeAccess1.isRead(), nodeAccess2.isRead());
        break;
      // compare write access
      case 2:
        compare = ApicUtil.compareBoolean(nodeAccess1.isWrite(), nodeAccess2.isWrite());
        break;
      // compare grant access
      case 3:
        compare = ApicUtil.compareBoolean(nodeAccess1.isGrant(), nodeAccess2.isGrant());
        break;
      // compare owner access
      case 4:
        compare = ApicUtil.compareBoolean(nodeAccess1.isOwner(), nodeAccess2.isOwner());
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
    // get didrection integer
    return this.direction;
  }

}
