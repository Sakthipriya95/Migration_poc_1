/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.OpenPointsData;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;


/**
 * Review Qnaire ans Open points table label provider class
 *
 * @author bru2cob
 */
public class OpenPointsTableLabelProvider extends ColumnLabelProvider {

  /**
   * Col index
   */
  private final int columnIndex;
  /**
   * column index for 0
   */
  public static final int OPEN_POINTS_COL = 0;
  /**
   * column index for 1
   */
  public static final int MEASURES_COL = 1;
  /**
   * column index for 2
   */
  public static final int RESPONSIBLE_COL = 2;
  /**
   * column index for 3
   */
  public static final int DATE_COL = 3;
  /**
   * column index for 4
   */
  public static final int RESULT_COL = 4;


  /**
   * @param columnIndex columnIndex
   * @param isEditRuleDialog
   */
  public OpenPointsTableLabelProvider(final int columnIndex) {
    this.columnIndex = columnIndex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    final Object element = cell.getElement();
    String result;
    // get the open point table values
    if (element instanceof OpenPointsData) {
      final OpenPointsData openPoint = (OpenPointsData) element;
      result = getColumnText(openPoint);
      // set the cell text
      cell.setText(result);
      // set the foreground color
      cell.setForeground(getForeground(openPoint));
    }
  }

  /**
   * @param result
   * @param openPoint
   * @return
   */
  private String getColumnText(final OpenPointsData openPoint) {
    String result = null;
    switch (this.columnIndex) {
      // get the op value
      case OPEN_POINTS_COL:
        result = openPoint.getOpenPoint();

        break;
      // get the measure value
      case MEASURES_COL:
        result = openPoint.getMeasures();

        break;
      // get the responsible value
      case RESPONSIBLE_COL:
        result = openPoint.getResponsibleName();

        break;
      // get the date value
      case DATE_COL:
        result = openPoint.getDateUIString();

        break;
      case RESULT_COL:
        result = openPoint.resultString();
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Color getForeground(final Object element) {

    if (element instanceof OpenPointsData) {
      final OpenPointsData opData = (OpenPointsData) element;
      if (opData.getOprType() == CommonUIConstants.CHAR_CONSTANT_FOR_DELETE) {
        return Display.getDefault().getSystemColor(SWT.COLOR_RED);
      }
    }
    return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    String result = null;
    if (element instanceof OpenPointsData) {
      final OpenPointsData openPoint = (OpenPointsData) element;
      result = getColumnText(openPoint);
    }
    return result;
  }
}
