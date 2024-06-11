package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.config.AggregateConfiguration;
import org.eclipse.nebula.widgets.nattable.resize.config.DefaultColumnResizeBindings;


/**
 * @author jvi6cob
 */
public class CustomColumnHeaderLayerConfiguration extends AggregateConfiguration {

  private final CustomColumnHeaderStyleConfiguration customColumnHeaderStyleConfiguration;

  /**
   * colum hearder config
   * 
   * @param customColumnHeaderStyleConfiguration CustomColumnHeaderStyleConfiguration
   */
  public CustomColumnHeaderLayerConfiguration(
      final CustomColumnHeaderStyleConfiguration customColumnHeaderStyleConfiguration) {
    this.customColumnHeaderStyleConfiguration = customColumnHeaderStyleConfiguration;
    addColumnHeaderStyleConfig();
    addColumnHeaderUIBindings();
  }

  /**
   * column header ui bindings
   */
  protected void addColumnHeaderUIBindings() {
    addConfiguration(new DefaultColumnResizeBindings());
  }

  /**
   * column header style config
   */
  protected void addColumnHeaderStyleConfig() {
    addConfiguration(this.customColumnHeaderStyleConfiguration);
  }

}
