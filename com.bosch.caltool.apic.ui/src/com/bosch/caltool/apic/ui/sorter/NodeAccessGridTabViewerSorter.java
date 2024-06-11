package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * Sorter class for Access rights grid table viewer
 *
 * @author apj4cob
 */
public class NodeAccessGridTabViewerSorter extends AbstractViewerSorter {

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
   * node access data handler instance
   */
  private final NodeAccessPageDataHandler dataHandler;

  /**
   * @param handler data handler
   */
  public NodeAccessGridTabViewerSorter(final NodeAccessPageDataHandler handler) {
    this.dataHandler = handler;
  }

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
    NodeAccess nodeAccess1 = (NodeAccess) obj1;
    NodeAccess nodeAccess2 = (NodeAccess) obj2;

    int compare;

    switch (this.index) {
      // compare full name
      case 0:
        compare = ApicUtil.compare(this.dataHandler.getUserFullName(nodeAccess1.getId()),
            this.dataHandler.getUserFullName(nodeAccess2.getId()));
        break;
      // compare user name
      case 1:
        compare = ApicUtil.compare(this.dataHandler.getUserName(nodeAccess1.getId()),
            this.dataHandler.getUserName(nodeAccess2.getId()));
        break;
      // compare user department
      case 2:
        compare = ApicUtil.compare(this.dataHandler.getUserDepartment(nodeAccess1.getId()),
            this.dataHandler.getUserDepartment(nodeAccess2.getId()));
        break;
      // compare read access
      case 3:
        compare = ApicUtil.compareBoolean(nodeAccess1.isRead(), nodeAccess2.isRead());
        break;
      // compare write access
      case 4:
        compare = ApicUtil.compareBoolean(nodeAccess1.isWrite(), nodeAccess2.isWrite());
        break;
      // compare grant access
      case 5:
        compare = ApicUtil.compareBoolean(nodeAccess1.isGrant(), nodeAccess2.isGrant());
        break;
      // compare owner access
      case 6:
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
