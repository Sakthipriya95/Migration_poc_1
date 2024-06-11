/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

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

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireConstants.QUESTION_RESP_SERIES_MEASURE;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.ruleseditor.utils.CdrUIUtils;


/**
 * combo box cell editor for questionare resp details page
 *
 * @author bru2cob
 */
public class QnaireRespComboBoxEditConfiguration extends AbstractRegistryConfiguration {


  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // set the question heading style
    CdrUIUtils.getQuestionHeadingStyle(configRegistry);
    // set the nat table header style
    CommonUiUtils.getNatTableHeaderStyle(configRegistry);


    // set the style
    Style style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    style.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);


    // combobox values for result field ( instead of not finsihed , not defined value is used)
    List<String> comboList2 = Arrays.asList(QUESTION_RESP_SERIES_MEASURE.NOT_DEFINED.getUiType(),
        QUESTION_RESP_SERIES_MEASURE.YES.getUiType(), QUESTION_RESP_SERIES_MEASURE.NO.getUiType());

    // set the combobox config for measurement
    getComboStyle(configRegistry, "COMBO_MEASUREMENT", IEditableRule.ALWAYS_EDITABLE, comboList2, style);

    // set the combobox config for series
    getComboStyle(configRegistry, "COMBO_SERIES", IEditableRule.ALWAYS_EDITABLE, comboList2, style);

    // set style for readonly where background color is set to gray
    Style validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "INVISIBLE");

    // set style for NA cells where background color is set to gray
    Style notApplicableCellStyle = new Style();
    notApplicableCellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, notApplicableCellStyle, DisplayMode.NORMAL,
        "NOT_APPLICABLE");
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

    ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList);
    // register for editable or not editable

    comboBoxCellEditor.setFreeEdit(editRule == IEditableRule.ALWAYS_EDITABLE);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        if (canonicalValue instanceof RvwQnaireAnswer) {
          return "";
        }
        return super.canonicalToDisplayValue(canonicalValue);
      }
    }, DisplayMode.NORMAL, label);
    // register cell editor
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
        label);
    // register cell painter
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(), DisplayMode.NORMAL,
        label);
    // register cell editable rule
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.NORMAL,
        label);
  }


}