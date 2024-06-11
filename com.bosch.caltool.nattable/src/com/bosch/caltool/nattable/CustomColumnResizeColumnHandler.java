package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.MultiColumnResizeCommand;


/**
 * Command Handler class for column Resize </br></br> <b>This class on the first resize by user does the
 * following</b></br> <b><i>1. Retrieves the current pixel size (calculated by percentage if
 * setColumnWidthPercentageByPosition is used)</i></b></br> <b><i>2. Clears the percentagesizing in all
 * columns</i></b></br> <b><i>3. Sets the retrieved pixel size for all columns by invoking
 * {@link MultiColumnResizeCommand}</i></b></br></br> <b>The above three steps are done only on the first column
 * resize</i></b></b>
 * 
 * @author jvi6cob
 */
public class CustomColumnResizeColumnHandler extends AbstractLayerCommandHandler<ColumnResizeCommand> {

  /**
   * DataLayer
   */
  private final DataLayer dataLayer;
  /**
   * resized by user or not
   */
  private boolean userResized;

  /**
   * Constructor
   * 
   * @param dataLayer DataLayer
   */
  public CustomColumnResizeColumnHandler(final DataLayer dataLayer) {
    this.dataLayer = dataLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<ColumnResizeCommand> getCommandClass() {
    return ColumnResizeCommand.class;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean doCommand(final ColumnResizeCommand command) {
    if (!this.userResized) {
      int[] columns = new int[this.dataLayer.getColumnCount()];
      int[] widths = new int[this.dataLayer.getColumnCount()];
      this.userResized = true;
      this.dataLayer.setColumnPercentageSizing(false);
      for (int i = 0; i < this.dataLayer.getColumnCount(); i++) {
        if (i == command.getColumnPosition()) {
          columns[i] = i;
          widths[i] = command.getNewColumnWidth();
        }
        else {
          columns[i] = i;
          widths[i] = this.dataLayer.getColumnWidthByPosition(i);
        }
      }
      for (int i = 0; i < this.dataLayer.getColumnCount(); i++) {
        this.dataLayer.setColumnPercentageSizing(i, false);
      }
      this.dataLayer.doCommand(new MultiColumnResizeCommand(this.dataLayer, columns, widths));
      return true;
    }
    final int newColumnWidth = command.getNewColumnWidth();
    this.dataLayer.setColumnWidthByPosition(command.getColumnPosition(), newColumnWidth);
    return true;
  }
}
