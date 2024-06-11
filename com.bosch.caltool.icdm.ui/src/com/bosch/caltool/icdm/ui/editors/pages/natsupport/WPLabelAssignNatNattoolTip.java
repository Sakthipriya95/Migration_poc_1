/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages.natsupport;


import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;


/**
 * Tooltip class for nattable
 *
 * @author apj4cob
 */
public class WPLabelAssignNatNattoolTip extends NatTableContentTooltip {


  /**
   * 
   */
  public WPLabelAssignNatNattoolTip(final NatTable nattable, final String[] tooltipRegions) {
    super(nattable, tooltipRegions);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Event event) {
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);
    ILayerCell cell = this.natTable.getCellByPosition(col, row);
    if (cell != null) {
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
      if (isVisibleContentPainter(painter)) {
        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
        if (tooltipValue.length() > 0) {
          return tooltipValue;
        }
      }
    }
    return null;
  }

}
