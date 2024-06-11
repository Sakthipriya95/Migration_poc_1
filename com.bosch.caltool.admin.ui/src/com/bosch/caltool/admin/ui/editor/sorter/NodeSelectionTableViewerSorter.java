/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.admin.ui.editor.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.user.NodeAccessInfo;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author say8cob
 */
public class NodeSelectionTableViewerSorter extends AbstractViewerSorter {

  /**
   *
   */
  private static final int NODE_DESC_COLUMN_INDEX = 1;

  /**
   *
   */
  private static final int NODE_NAME_COLUMN_INDEX = 0;

  /**
   * index for Sorting
   */
  private int index;

  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * Default ascending
   */
  private int direction = ASCENDING;

  /**
   * descending constant
   */
  private static final int DESCENDING = 1;

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
      // compare description
      case NODE_DESC_COLUMN_INDEX:
        compare = ApicUtil.compare(nodeAccess1.getNodeDesc(), nodeAccess2.getNodeDesc());
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
