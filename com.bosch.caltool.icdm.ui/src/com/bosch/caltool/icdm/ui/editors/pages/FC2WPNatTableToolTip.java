/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.icdm.model.a2l.PTType;

/**
 * @author and4cob
 */
class FC2WPNatTableToolTip extends DefaultToolTip {

  private final NatTable natTableObj;
  private final FC2WPNatFormPage fc2wpNatFormPage;
  private static final int PTTYPE_COL_NUMBER = 6;

  /**
   * @param natTable NatTable
   * @param fc2wpNatFormPage FC2WPNatFormPage
   */
  public FC2WPNatTableToolTip(final NatTable natTable, final FC2WPNatFormPage fc2wpNatFormPage) {
    super(natTable, ToolTip.NO_RECREATE, false);
    this.natTableObj = natTable;
    this.fc2wpNatFormPage = fc2wpNatFormPage;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.window.ToolTip#getToolTipArea(org.eclipse.swt.widgets.Event) Implementation here means the
   * tooltip is not redrawn unless mouse hover moves outside of the current cell (the combination of ToolTip.NO_RECREATE
   * style and override of this method).
   */
  @Override
  protected Object getToolTipArea(final Event event) {
    int col = this.natTableObj.getColumnPositionByX(event.x);
    int row = this.natTableObj.getRowPositionByY(event.y);

    return new Point(col, row);
  }

  @Override
  protected String getText(final Event event) {
    int col = this.natTableObj.getColumnPositionByX(event.x);
    int row = this.natTableObj.getRowPositionByY(event.y);
    ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);
    String cellValue = (String) cellByPosition.getDataValue();

    return (cellByPosition.getColumnIndex() == PTTYPE_COL_NUMBER) ? createToolTipForPTType(cellValue) : cellValue;
  }

  /**
   * @param cellValue
   * @return
   */
  private String createToolTipForPTType(final String cellValue) {
    StringBuilder ptTypeTooltipText = new StringBuilder();
    List<String> ptTypeCellValueList = Arrays.asList(cellValue.split(",", -1));
    for (String ptTypeCellValue : ptTypeCellValueList) {
      for (PTType ptType : FC2WPNatTableToolTip.this.fc2wpNatFormPage.getEditorInput().getFC2WPDefBO()
          .getAllPtTypeSet()) {
        if (ptType.getPtTypeName().equalsIgnoreCase(ptTypeCellValue)) {
          ptTypeTooltipText.append(ptTypeCellValue).append(" = ").append(ptType.getPtTypeDesc()).append('\n');
        }
      }
    }
    return ptTypeTooltipText.toString().trim();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean shouldCreateToolTip(final Event event) {
    int col = this.natTableObj.getColumnPositionByX(event.x);
    int row = this.natTableObj.getRowPositionByY(event.y);
    ILayerCell cellByPosition = this.natTableObj.getCellByPosition(col, row);
    if ((cellByPosition != null) && (cellByPosition.getColumnIndex() == PTTYPE_COL_NUMBER)) {
      return true;
    }
    if ((cellByPosition == null) || (cellByPosition.getDataValue() == null) ||
        !(cellByPosition.getDataValue() instanceof String)) {
      return false;
    }
    String cellValue = (String) cellByPosition.getDataValue();
    if ((cellValue == null) || cellValue.isEmpty()) {
      return false;
    }
    Rectangle currentBounds = cellByPosition.getBounds();
    cellByPosition.getLayer().getPreferredWidth();

    GC gcObj = new GC(this.natTableObj);
    Point size = gcObj.stringExtent(cellValue);
    return currentBounds.width < size.x;
  }
}
