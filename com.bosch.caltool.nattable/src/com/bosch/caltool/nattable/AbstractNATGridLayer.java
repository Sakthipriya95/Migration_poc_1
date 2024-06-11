package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;
import org.eclipse.ui.IEditorInput;


/**
 * @author adn1cob
 * @param <T> object
 */
public abstract class AbstractNATGridLayer<T> extends GridLayer {

  /**
   * The main body data provider for NAT table
   */
  public CustomGlazedListsDataProvider<T> bodyDataProvider;
  /**
   * Support for Filters in NAT table
   */
  public CustomGlazedListsFilterStrategy<T> filterStrategy;
  /**
   * Support for sorting in NAT table
   */
  public SortHeaderLayer<T> sortableColumnHeaderLayer;
  /**
   * Column header layer
   */
  public DefaultColumnHeaderDataLayer columnHeaderDataLayer;
  /**
   * Row header layer
   */
  public FilterRowHeaderComposite<T> filterRowHeaderLayer;
  /**
   * Support for column grouping
   */
  public ColumnGroupHeaderLayer columnGroupHeaderLayer;
  /**
   * Labels and tooltips
   */
  public ColumnOverrideLabelAccumulator bodyLabelAccumulator;
  /**
   * Coulmn header data provider
   */
  public CustomColumnHeaderDataProvider columnHeaderDataProvider;
  /**
   * GroupBY data layer
   */
  public CustomGroupByDataLayer<T> bodyDataLayer;
  /**
   * Default NAT table layer stack
   */
  public CustomDefaultBodyLayerStack bodyLayer;
  /**
   * Column header layer
   */
  public ColumnHeaderLayer columnHeaderLayer;
  /**
   * Dummy data layer
   */
  public DataLayer dummyDataLayer;
  /**
   * Column group model
   */
  public ColumnGroupModel columnGroupModel;


  /**
   * @param useDefaultConfiguration true/false
   */
  protected AbstractNATGridLayer(final boolean useDefaultConfiguration) {
    super(useDefaultConfiguration);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void setClientAreaProvider(final IClientAreaProvider clientAreaProvider) {
    super.setClientAreaProvider(clientAreaProvider);
  }


  /**
   * @return the bodyDataProvider
   */
  public CustomGlazedListsDataProvider<T> getBodyDataProvider() {
    return this.bodyDataProvider;
  }


  /**
   * @return the filterStrategy
   */
  public CustomGlazedListsFilterStrategy<T> getFilterStrategy() {
    return this.filterStrategy;
  }


  /**
   * @return the sortableColumnHeaderLayer
   */
  public SortHeaderLayer<T> getSortableColumnHeaderLayer() {
    return this.sortableColumnHeaderLayer;
  }


  /**
   * @return the columnHeaderDataLayer
   */
  public DefaultColumnHeaderDataLayer getColumnHeaderDataLayer() {
    return this.columnHeaderDataLayer;
  }


  /**
   * @return the filterRowHeaderLayer
   */
  public FilterRowHeaderComposite<T> getFilterRowHeaderLayer() {
    return this.filterRowHeaderLayer;
  }


  /**
   * @return the columnGroupHeaderLayer
   */
  public ColumnGroupHeaderLayer getColumnGroupHeaderLayer() {
    return this.columnGroupHeaderLayer;
  }


  /**
   * @return the bodyLabelAccumulator
   */
  public ColumnOverrideLabelAccumulator getBodyLabelAccumulator() {
    return this.bodyLabelAccumulator;
  }


  /**
   * @return the columnHeaderDataProvider
   */
  public CustomColumnHeaderDataProvider getColumnHeaderDataProvider() {
    return this.columnHeaderDataProvider;
  }


  /**
   * @return the bodyDataLayer
   */
  public CustomGroupByDataLayer<T> getBodyDataLayer() {
    return this.bodyDataLayer;
  }


  /**
   * @return the bodyLayer
   */
  @Override
  public CustomDefaultBodyLayerStack getBodyLayer() {
    return this.bodyLayer;
  }


  /**
   * @return the columnHeaderLayer
   */
  @Override
  public ColumnHeaderLayer getColumnHeaderLayer() {
    return this.columnHeaderLayer;
  }


  /**
   * @return the dummyDataLayer
   */
  public DataLayer getDummyDataLayer() {
    return this.dummyDataLayer;
  }


  /**
   * @return the columnGroupModel
   */
  public ColumnGroupModel getColumnGroupModel() {
    return this.columnGroupModel;
  }

  /**
   * @return
   */
  public abstract IEditorInput getEditorInput();


}
