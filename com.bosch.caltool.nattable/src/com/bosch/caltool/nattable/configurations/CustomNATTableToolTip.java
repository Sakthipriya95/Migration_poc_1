package com.bosch.caltool.nattable.configurations;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;


/**
 * This class creates customized tooltip for the nat table.<br>
 * Extends jface default tooltip
 */

public class CustomNATTableToolTip extends DefaultToolTip {

  /**
   * NAT table instance
   */
  private final NatTable natTable;

  /**
   * @param natTable the nattable
   */
  public CustomNATTableToolTip(final NatTable natTable) {
    super(natTable, ToolTip.NO_RECREATE, false);
    this.natTable = natTable;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.window.ToolTip#getToolTipArea(org.eclipse.swt.widgets.Event) Implementation here means the
   * tooltip is not redrawn unless mouse hover moves outside of the current cell (the combination of ToolTip.NO_RECREATE
   * style and override of this method).
   */
  @Override
  protected Object getToolTipArea(final Event event) {
    // get the tooltip area
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);
    // return the point
    return new Point(col, row);
  }

  @Override
  protected String getText(final Event event) {
    // get the tooltip area
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);
    ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
    cellByPosition.getBounds();
    return (String) cellByPosition.getDataValue();
  }

  @Override
  protected Composite createToolTipContentArea(final Event event, final Composite parent) {
    return super.createToolTipContentArea(event, parent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean shouldCreateToolTip(final Event event) {
    // get the tooltip area
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);
    ILayerCell cellByPosition = this.natTable.getCellByPosition(col, row);
    // check the cell poisition
    if ((cellByPosition == null) || (cellByPosition.getDataValue() == null) ||
        !(cellByPosition.getDataValue() instanceof String)) {
      return false;
    }
    // check the cell value
    String cellValue = (String) cellByPosition.getDataValue();
    if ((cellValue == null) || cellValue.isEmpty()) {
      return false;
    }
    Rectangle currentBounds = cellByPosition.getBounds();
    cellByPosition.getLayer().getPreferredWidth();
    // create GC for the tooltip
    GC graphCoordinates = new GC(this.natTable);
    Point size = graphCoordinates.stringExtent(cellValue);
    if (currentBounds.width < size.x) {
      return true;
    }
    // by default, no tooltip
    return false;
  }
}
