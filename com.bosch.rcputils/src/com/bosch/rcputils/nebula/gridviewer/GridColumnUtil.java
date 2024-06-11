/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.nebula.gridviewer;

import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;


/**
 * @author mga1cob
 */
public final class GridColumnUtil {

  /**
   * GridColumnUtil instance
   */
  private static GridColumnUtil gridColumnUtil;

  /**
   * private constructor
   */
  private GridColumnUtil() {
    // Singleton
  }

  /**
   * Get Instance method.
   * 
   * @return instance of this class
   */
  public static GridColumnUtil getInstance() {
    if (gridColumnUtil == null) {
      gridColumnUtil = new GridColumnUtil();
    }
    return gridColumnUtil;
  }

  /**
   * This method creates GridColumn and return its instance
   * 
   * @param columnGroup instance
   * @param columnStyle defines column style
   * @param columnWidth defines column width
   * @param columnName defines GridColumn name
   * @param summaryRequired defines whether summary is required or not
   * @return GridColumn instance
   */
  public GridColumn createGridColumn(final GridColumnGroup columnGroup, final int columnStyle, final int columnWidth,
      final String columnName, final boolean summaryRequired) {
    final GridColumn gridColumn = new GridColumn(columnGroup, columnStyle);
    gridColumn.setWidth(columnWidth);
    gridColumn.setText(columnName);
    gridColumn.setSummary(summaryRequired);
    return gridColumn;
  }


  /**
   * This method creates GridColumn and return its instance
   * 
   * @param columnGroup instance
   * @param columnStyle defines column style
   * @param columnWidth defines column width
   * @param columnName defines GridColumn name
   * @param isDetailRequired defines whether detail is required or not
   * @param isSummaryRequired defines whether summary is required or not
   * @return GridColumn instance
   */
  public GridColumn createGridColumn(GridColumnGroup columnGroup, int columnStyle, int columnWidth, String columnName,
      boolean isDetailRequired, boolean isSummaryRequired) {
    GridColumn gridColumn = new GridColumn(columnGroup, columnStyle);
    gridColumn.setWidth(columnWidth);
    gridColumn.setText(columnName);
    gridColumn.setDetail(isDetailRequired);
    gridColumn.setSummary(isSummaryRequired);
    return gridColumn;
  }
}
