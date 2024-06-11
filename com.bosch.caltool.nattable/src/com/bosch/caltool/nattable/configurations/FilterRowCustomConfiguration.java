package com.bosch.caltool.nattable.configurations;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.Comparator;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;


/**
 * This configuration is for the NAT table. <br>
 * Pass number of columns with the constructor<br>
 */
public class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {

  /**
   * Define number of columns
   */
  private final long numCols;

  /**
   * @param numCols number of columns
   */
  public FilterRowCustomConfiguration(final long numCols) {
    // set number of cols
    this.numCols = numCols;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // override the default filter row configuration for painter
    configRegistry.registerConfigAttribute(CELL_PAINTER,
        new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);
    // enable filter comparator for cols
    for (int index = 0; index <= this.numCols; index++) {
      // register config attr for each col
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
          NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + index);
    }
    // enable text matcher for cols
    for (int index = 0; index <= this.numCols; index++) {
      // register config attr for each col
      configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
          TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + index);
    }

  }

  private static Comparator<?> getIgnorecaseComparator() {
    // compare objects
    return new Comparator<String>() {

      @Override
      public int compare(final String obj1, final String obj2) {
        // case in-sensitive
        return obj1.compareToIgnoreCase(obj2);
      }
    };
  }

}
