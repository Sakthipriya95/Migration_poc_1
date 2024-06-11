/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.nebula.gridviewer;

import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;


/**
 * @author mga1cob
 */
public final class GridViewerColumnUtil {


  /**
   * GridViewerColumnUtil instance
   */
  private static GridViewerColumnUtil gridViewerColUtil;

  /**
   * The private constructor
   */
  private GridViewerColumnUtil() {
    // The private constructor
  }

  /**
   * This method returns GridViewerColumnUtil instance
   * 
   * @return GridViewerColumnUtil
   */
  public static GridViewerColumnUtil getInstance() {
    if (gridViewerColUtil == null) {
      gridViewerColUtil = new GridViewerColumnUtil();
    }
    return gridViewerColUtil;
  }


  /**
   * This method will creates GridViewer column with column name and width
   * 
   * @param gridTabViewer instance
   * @param colName defines gridviewercolumn name
   * @param colWidth defines gridviewercolumn width
   * @return GridViewerColumn
   */
  public GridViewerColumn createGridViewerColumn(GridTableViewer gridTabViewer, String colName, int colWidth) {
    GridViewerColumn gridViewerColumn = new GridViewerColumn(gridTabViewer, SWT.NONE);
    gridViewerColumn.getColumn().setWidth(colWidth);
    gridViewerColumn.getColumn().setText(colName);
    return gridViewerColumn;
  }

  /**
   * @param gridTabViewer instance
   * @param colName defines GridViewerColumn name
   * @param colWidth defines GridViewerColumn width
   * @param headerToolTip defines toolTip for col header
   * @return GridViewerColumn
   */
  public GridViewerColumn createGridViewerCheckStyleColumn(GridTableViewer gridTabViewer, String colName, int colWidth,
      String headerToolTip) {
    GridViewerColumn gridViewerColumn = createGridViewerCheckStyleColumn(gridTabViewer, colName, colWidth);
    gridViewerColumn.getColumn().setHeaderTooltip(headerToolTip);
    return gridViewerColumn;
  }

  /**
   * @param gridTabViewer instance
   * @param colName defines GridViewerColumn name
   * @param colWidth defines GridViewerColumn width
   * @return GridViewerColumn
   */
  public GridViewerColumn createGridViewerCheckStyleColumn(GridTableViewer gridTabViewer, String colName, int colWidth) {
    GridViewerColumn gridViewerColumn = new GridViewerColumn(gridTabViewer, SWT.CHECK);
    gridViewerColumn.getColumn().setWidth(colWidth);
    gridViewerColumn.getColumn().setText(colName);
    return gridViewerColumn;
  }

  /**
   * This method will creates GridViewer column with column name, column width and column alignment
   * 
   * @param gridTabViewer instance
   * @param colName defines gridviewercolumn name
   * @param colWidth defines gridviewercolumn width
   * @param columnAlignnent defines column data alignment
   * @return GridViewerColumn
   */
  public GridViewerColumn createGridViewerColumn(GridTableViewer gridTabViewer, String colName, int colWidth,
      int columnAlignnent) {
    GridViewerColumn gridViewerColumn = new GridViewerColumn(gridTabViewer, columnAlignnent);
    gridViewerColumn.getColumn().setWidth(colWidth);
    gridViewerColumn.getColumn().setText(colName);
    return gridViewerColumn;
  }

  /**
   * This method will creates GridViewer column without a column name and with column width
   * 
   * @param gridTabViewer instance
   * @param colWidth defines gridviewercolumn width
   * @return GridViewerColumn
   */
  public GridViewerColumn createGridViewerColumn(GridTableViewer gridTabViewer, int colWidth) {
    GridViewerColumn gridViewerColumn = new GridViewerColumn(gridTabViewer, SWT.NONE);
    gridViewerColumn.getColumn().setWidth(colWidth);
    return gridViewerColumn;
  }

  /**
   * This method will creates GridViewerColumn without a column name and with column width
   * 
   * @param gridTabViewer instance
   * @param colWidth defines gridviewercolumn width
   * @param headerToolTip defines GridViewerColumn header tooltip
   * @return GridViewerColumn
   */
  public GridViewerColumn createGridViewerColumn(GridTableViewer gridTabViewer, int colWidth, String headerToolTip) {
    GridViewerColumn gridViewerColumn = new GridViewerColumn(gridTabViewer, SWT.NONE);
    gridViewerColumn.getColumn().setWidth(colWidth);
    gridViewerColumn.getColumn().setHeaderTooltip(headerToolTip);
    return gridViewerColumn;
  }

}
