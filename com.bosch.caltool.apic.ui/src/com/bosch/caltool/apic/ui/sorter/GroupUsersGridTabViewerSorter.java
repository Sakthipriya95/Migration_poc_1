package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * Sorter class for Access rights grid table viewer
 *
 * @author apj4cob
 */
public class GroupUsersGridTabViewerSorter extends AbstractViewerSorter {

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
  public GroupUsersGridTabViewerSorter() {}

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
    ActiveDirectoryGroupUser grpUser1 = (ActiveDirectoryGroupUser) obj1;
    ActiveDirectoryGroupUser grpUser2 = (ActiveDirectoryGroupUser) obj2;

    int compare;

    switch (this.index) {
      // compare full name
      case 0:
        compare = ApicUtil.compare(grpUser1.getGroupUserName(), grpUser2.getGroupUserName());
        break;
      // compare NT ID
      case 1:
        compare = ApicUtil.compare(grpUser1.getUsername(), grpUser2.getUsername());
        break;
      // compare Department
      case 2:
        compare = ApicUtil.compare(grpUser1.getGroupUserDept(), grpUser2.getGroupUserDept());
        break;
      // compare isICDM User
      case 3:
        compare = ApicUtil.compare(grpUser1.getIsIcdmUser(), grpUser2.getIsIcdmUser());
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
