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
 * @author pdh2cob
 */
public class A2lWPDefinitionNattoolTip extends NatTableContentTooltip {


  private static final String USER_COL_TOOLTIP = "Click to set responsible";

  /**
   * @param nattable - nattable instance
   * @param tooltipRegions - tooltip region
   */
  public A2lWPDefinitionNattoolTip(final NatTable nattable, final String[] tooltipRegions) {
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
        return getTooltipText(cell);
      }
    }
    return null;
  }


  private String getTooltipText(final ILayerCell cell) {
    if (cell.getColumnIndex() == 3) {
      return USER_COL_TOOLTIP;
    }
    String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
    if (tooltipValue.length() > 0) {
      return tooltipValue;
    }
    return null;
  }

}
