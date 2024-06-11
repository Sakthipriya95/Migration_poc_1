/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import com.bosch.caltool.icdm.model.a2l.WpRespType;


/**
 * Review score combo box edit configuration
 *
 * @author bru2cob
 */
public class WPRespComboBoxEditConfiguration extends AbstractRegistryConfiguration {


  /**
   * @param
   */
  public WPRespComboBoxEditConfiguration() {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // set the style for editable cell
    Style style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    style.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);

    // combo box values
    List<String> comboList = new ArrayList<>();
    // get the scores to be set in combo
    List<WpRespType> wpRespList = Arrays.asList(WpRespType.values());
    for (WpRespType wpResp : wpRespList) {
      comboList.add(wpResp.getDispName());
    }
    // set the combobox config for score
    getComboStyle(configRegistry, "COMBO_RESP", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    getComboStyle(configRegistry, "COMBO_RESP_READ_ONLY", IEditableRule.NEVER_EDITABLE, comboList, style);


  }

  /**
   * Registers the combo box style
   *
   * @param configRegistry
   * @param comboList2
   */
  private void getComboStyle(final IConfigRegistry configRegistry, final String label, final IEditableRule editRule,
      final List<String> comboList, final Style style) {
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, label);

    ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList, 10);
    // register for editable or not editable
    comboBoxCellEditor.setFreeEdit(editRule == IEditableRule.ALWAYS_EDITABLE);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        return super.canonicalToDisplayValue(canonicalValue);
      }
    }, DisplayMode.NORMAL, label);
    // register the cell properties
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
        label);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(), DisplayMode.NORMAL,
        label);

    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.NORMAL,
        label);
  }

}
