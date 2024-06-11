/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.calcomp.caldataanalyzer.vo.LabelValueInfoVO;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.calmodel.caldataphy.CalDataPhy.SortColumns;
import com.bosch.calmodel.caldataphy.util.CalDataPhyUtil;
import com.bosch.caltool.icdm.common.ui.views.data.SeriesStatisticsTableData;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * Icdm-521 Moved the class to common UI
 *
 * @author mga1cob
 */
public class SeriesStatisticsViewerSorter extends AbstractViewerSorter {

  /**
   * index
   */
  private int index;
  /**
   * constant for descending direction
   */
  private static final int DESCENDING = 1;
  /**
   * constant for ascending direction
   */
  private static final int ASCENDING = 0;
  /**
   * direction by default set to DESCENDING
   */
  private int direction = DESCENDING;

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

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    // initialise LabelValueInfoVO
    final LabelValueInfoVO lblValueInfo1 = ((SeriesStatisticsTableData) obj1).getLabelValueInfo();
    final LabelValueInfoVO lblValueInfo2 = ((SeriesStatisticsTableData) obj2).getLabelValueInfo();

    // initialise CalDataPhy objects
    final CalDataPhy calDataPhy1 = lblValueInfo1.getCalDataPhy();
    final CalDataPhy calDataPhy2 = lblValueInfo2.getCalDataPhy();

    int colIndex;
    switch (this.index) {
      // Icdm-674 create the Serial Index Column for the series statistics view part table
      case 0:
        colIndex = ApicUtil.compareInt(((SeriesStatisticsTableData) obj2).getSerialIndex(),
            ((SeriesStatisticsTableData) obj1).getSerialIndex());
        break;
      case 1:
        // compare file id
        colIndex = ApicUtil.compareLong(lblValueInfo1.getFileIDList().size(), lblValueInfo2.getFileIDList().size());
        break;
      case 2:
        // compare unit
        colIndex = calDataPhy1.compareTo(calDataPhy2, SortColumns.UNIT);
        break;
      case 3:
        // compare display value
        colIndex = calDataPhy1.compareTo(calDataPhy2, SortColumns.SIMPLE_DISPLAY_VALUE);
        break;

      default:
        // by default , return comapre result as equal
        colIndex = 0;
    }
    // Second level sorting with dataset
    if (colIndex == 0) {
      colIndex = CalDataPhyUtil.compareLong(lblValueInfo1.getFileIDList().size(), lblValueInfo2.getFileIDList().size());
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      colIndex = -colIndex;
    }
    return colIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }


}
