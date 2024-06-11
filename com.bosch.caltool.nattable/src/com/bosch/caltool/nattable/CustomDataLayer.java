package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.command.StructuralRefreshCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommandHandler;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.resize.command.MultiColumnResizeCommandHandler;
import org.eclipse.nebula.widgets.nattable.resize.command.MultiRowResizeCommandHandler;
import org.eclipse.nebula.widgets.nattable.resize.command.RowResizeCommandHandler;


/**
 * Class which overrides registerCommandHandlers method of DataLayer class for custom column resizing</br></br> <b>TODO:
 * This class to be replaced with CustomGroupDataLayer</b>
 * 
 * @author jvi6cob
 */
public class CustomDataLayer extends DataLayer {

  /**
   * 
   */
  public CustomDataLayer() {
    super();
  }

  /**
   * @param dataProvider IDataProvider
   * @param defaultColumnWidth int
   * @param defaultRowHeight int
   */
  public CustomDataLayer(final IDataProvider dataProvider, final int defaultColumnWidth, final int defaultRowHeight) {
    super(dataProvider, defaultColumnWidth, defaultRowHeight);
  }

  /**
   * @param dataProvider IDataProvider
   */
  public CustomDataLayer(final IDataProvider dataProvider) {
    super(dataProvider);
  }

  /**
   * @param defaultColumnWidth int
   * @param defaultRowHeight int
   */
  public CustomDataLayer(final int defaultColumnWidth, final int defaultRowHeight) {
    super(defaultColumnWidth, defaultRowHeight);
  }

  /**
   * {@inheritDoc}</br></br> This method override implements a {@link CustomColumnResizeColumnHandler} which removes
   * percentage columnsizing when the user resizes any column
   */
  @Override
  protected void registerCommandHandlers() {
    registerCommandHandler(new CustomColumnResizeColumnHandler(this));
    registerCommandHandler(new MultiColumnResizeCommandHandler(this));
    registerCommandHandler(new RowResizeCommandHandler(this));
    registerCommandHandler(new MultiRowResizeCommandHandler(this));
    registerCommandHandler(new UpdateDataCommandHandler(this));
    registerCommandHandler(new StructuralRefreshCommandHandler());
    registerCommandHandler(new VisualRefreshCommandHandler());
  }

}
