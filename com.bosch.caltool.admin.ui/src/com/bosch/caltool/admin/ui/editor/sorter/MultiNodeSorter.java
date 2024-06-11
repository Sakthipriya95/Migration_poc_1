package com.bosch.caltool.admin.ui.editor.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter Class for Multi Node access Admin page
 *
 * @author say8cob
 */
public class MultiNodeSorter extends AbstractViewerSorter {

  /**
   *
   */
  private static final int OWNER_ACCESS_COLUMN_INDEX = 6;

  /**
   *
   */
  private static final int GRANT_ACCESS_COLUMN_INDEX = 5;

  /**
   *
   */
  private static final int WRITE_ACCESS_COLUMN_INDEX = 4;

  /**
   *
   */
  private static final int READ_ACCESS_COLUMN_INDEX = 3;

  /**
   *
   */
  private static final int NODE_DESC_COLUMN_INDEX = 2;

  /**
   *
   */
  private static final int NODE_TYPE_COLUMN_INDEX = 0;

  /**
   *
   */
  private static final int NODE_NAME_COLUMN_INDEX = 1;

  /**
   * index for Sorting
   */
  private int index;

  /**
   * descending constant
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
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
      // to flip the sorting direction, when the same column is sorted more than once
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  /**
   * compare the objects based on column index {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    NodeAccessInfo nodeAccess1 = (NodeAccessInfo) obj1;
    NodeAccessInfo nodeAccess2 = (NodeAccessInfo) obj2;

    int compare;

    switch (this.index) {
      // compare node name
      case NODE_NAME_COLUMN_INDEX:
        compare = ApicUtil.compare(nodeAccess1.getNodeName(), nodeAccess2.getNodeName());
        break;
      // compare node type
      case NODE_TYPE_COLUMN_INDEX:
        compare = ApicUtil.compare(nodeAccess1.getAccess().getNodeType(), nodeAccess2.getAccess().getNodeType());
        break;
      // compare description
      case NODE_DESC_COLUMN_INDEX:
        compare = ApicUtil.compare(nodeAccess1.getNodeDesc(), nodeAccess2.getNodeDesc());
        break;
      // compare read access
      case READ_ACCESS_COLUMN_INDEX:
        compare = ApicUtil.compareBoolean(nodeAccess1.getAccess().isRead(), nodeAccess2.getAccess().isRead());
        break;
      // compare write access
      case WRITE_ACCESS_COLUMN_INDEX:
        compare = ApicUtil.compareBoolean(nodeAccess1.getAccess().isWrite(), nodeAccess2.getAccess().isWrite());
        break;
      // compare grant access
      case GRANT_ACCESS_COLUMN_INDEX:
        compare = ApicUtil.compareBoolean(nodeAccess1.getAccess().isGrant(), nodeAccess2.getAccess().isGrant());
        break;
      // compare owner access
      case OWNER_ACCESS_COLUMN_INDEX:
        compare = ApicUtil.compareBoolean(nodeAccess1.getAccess().isOwner(), nodeAccess2.getAccess().isOwner());
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
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
