/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridColumn;

import com.bosch.caltool.icdm.client.bo.apic.PIDCScoutResult;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author bru2cob
 */
public class PIDCResultSorter extends AbstractViewerSorter {

  /**
   * Represents the column number to be sorted
   */
  private int index;

  /**
   * Default diraction ascending
   */
  private int direction = ApicConstants.ASCENDING;
  /**
   * Selected column
   */
  private GridColumn column;

  /**
   * index defines grid tableviewercolumn index {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ApicConstants.ASCENDING;
    }

  }

  /**
   * Compares two pid's details {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) { // NOPMD by bne4cob on 3/11/14 12:16
    GridTableViewer resultTable = (GridTableViewer) viewer;
    this.column = resultTable.getGrid().getColumn(this.index);// PM
    int result = 0;
    // check pidc instance
    if ((obj1 instanceof PIDCScoutResult) && (obj2 instanceof PIDCScoutResult)) {
      PIDCScoutResult result1 = (PIDCScoutResult) obj1;
      PIDCScoutResult result2 = (PIDCScoutResult) obj2;

      switch (this.index) {
        // compare pidc's name
        case CommonUIConstants.COLUMN_INDEX_1:
          result = ApicUtil.compare(result1.getPidcVersion().getName(), result2.getPidcVersion().getName());
          break;
        default:
          // if no column available
          result = defaultCompare(result1, result2);
          break;
      }
      // If descending order, flip the direction
      if (this.direction == ApicConstants.DESCENDING) {
        result = -result;
      }
    }
    return result;
  }

  /**
   * Compare two pidc Result objects.
   *
   * @param result1 result1
   * @param result2 result2
   * @return
   */
  private int defaultCompare(final PIDCScoutResult result1, final PIDCScoutResult result2) {
    int result;
    if (this.column == null) {
      result = 0;
    }
    // other columns name compare
    else {
      result = compareOtherColumns(result1, result2);

    }
    return result;
  }

  /**
   * @param result1 pidc result1
   * @param result2 pidc result2
   * @return the compare result of the two pidc node name
   */
  private int compareOtherColumns(final PIDCScoutResult result1, final PIDCScoutResult result2) {

    Long level = (Long) this.column.getData("level");
    if (level == null) {
      return 0;
    }

    // compate the two node names
    return ApicUtil.compare(result1.getLevelValueText(level), result2.getLevelValueText(level));
  }

  /**
   * @return defines direction of sort
   */
  @Override
  public int getDirection() {
    return this.direction;
  }


}
