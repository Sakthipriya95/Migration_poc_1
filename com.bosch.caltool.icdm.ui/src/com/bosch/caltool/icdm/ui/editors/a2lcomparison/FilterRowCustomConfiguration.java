/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.grid.GridRegion.FILTER_ROW;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterIconPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowPainter;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

/**
 * @author bru2cob
 */
class FilterRowCustomConfiguration extends AbstractRegistryConfiguration {


  final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

  public void configureRegistry(final IConfigRegistry configRegistry) {
    // override the default filter row configuration for painter
    configRegistry.registerConfigAttribute(CELL_PAINTER,
        new FilterRowPainter(new FilterIconPainter(GUIHelper.getImage("filter"))), NORMAL, FILTER_ROW);


    // TODO: Below four lines can be removed. To be checked
    configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
        NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
    configRegistry.registerConfigAttribute(FilterRowConfigAttributes.FILTER_COMPARATOR, getIgnorecaseComparator(),
        NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);


    configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
        TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);
    configRegistry.registerConfigAttribute(FilterRowConfigAttributes.TEXT_MATCHING_MODE,
        TextMatchingMode.REGULAR_EXPRESSION, NORMAL, FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);

    List<String> comboList = Arrays.asList("True", "False");
    // register a combo box cell editor for the Diff column in the filter row
    // the label is set automatically to the value of
    // FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + column position
    ICellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, NORMAL,
        FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);

  }

  private static Comparator<?> getIgnorecaseComparator() {
    return new Comparator<String>() {

      public int compare(final String str1, final String str2) {

        return str1.compareToIgnoreCase(str2);
      }
    };
  }

}
