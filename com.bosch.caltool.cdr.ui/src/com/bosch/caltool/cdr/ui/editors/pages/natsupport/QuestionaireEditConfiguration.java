/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

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
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.icdm.ruleseditor.utils.CdrUIUtils;

/**
 * combo box cell editor for questionare details page <br>
 * <br>
 */
public class QuestionaireEditConfiguration extends AbstractRegistryConfiguration {


  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {


    CdrUIUtils.getQuestionHeadingStyle(configRegistry); // apply
                                                        // for
                                                        // all
                                                        // cells
                                                        // with
                                                        // this
                                                        // label


    CommonUiUtils.getNatTableHeaderStyle(configRegistry);
    // create a input list
    List<String> comboList = Arrays.asList(CDRConstants.QUESTION_CONFIG_TYPE.OPTIONAL.getUiType(),
        CDRConstants.QUESTION_CONFIG_TYPE.MANDATORY.getUiType(),
        CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType());
    // create list without mandatory option
    List<String> opComboList = Arrays.asList(CDRConstants.QUESTION_CONFIG_TYPE.OPTIONAL.getUiType(),
        CDRConstants.QUESTION_CONFIG_TYPE.NOT_RELEVANT.getUiType());
    // create style for cell
    Style style = new Style();
    style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    style.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);
    // create style for read only cell
    Style style_readonly = new Style();

    style_readonly.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(204, 198, 176));
    style_readonly.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);


    getComboStyle(configRegistry, "COMBO_MEASUREMENT_READONLY", IEditableRule.NEVER_EDITABLE, comboList,
        style_readonly);
    getComboStyle(configRegistry, "COMBO_MEASUREMENT", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    getComboStyle(configRegistry, "COMBO_SERIES_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style_readonly);
    getComboStyle(configRegistry, "COMBO_SERIES", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    getComboStyle(configRegistry, "COMBO_LINK_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style_readonly);
    getComboStyle(configRegistry, "COMBO_LINK", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    getComboStyle(configRegistry, "COMBO_REMARK_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style_readonly);
    getComboStyle(configRegistry, "COMBO_REMARK", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    getComboStyle(configRegistry, "COMBO_OP_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style_readonly);
    getComboStyle(configRegistry, "COMBO_OP", IEditableRule.ALWAYS_EDITABLE, opComboList, style);
    // ICDM-
    getComboStyle(configRegistry, "COMBO_MEASURE_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style_readonly);
    getComboStyle(configRegistry, "COMBO_MEASURE", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    getComboStyle(configRegistry, "COMBO_RESPONSIBLE_READONLY", IEditableRule.NEVER_EDITABLE, comboList,
        style_readonly);
    getComboStyle(configRegistry, "COMBO_RESPONSIBLE", IEditableRule.ALWAYS_EDITABLE, comboList, style);
    getComboStyle(configRegistry, "COMBO_DATE_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style_readonly);
    getComboStyle(configRegistry, "COMBO_DATE", IEditableRule.ALWAYS_EDITABLE, comboList, style);

    getComboStyle(configRegistry, "COMBO_RESULT_READONLY", IEditableRule.NEVER_EDITABLE, comboList, style_readonly);
    getComboStyle(configRegistry, "COMBO_RESULT", IEditableRule.ALWAYS_EDITABLE, comboList, style);

    // Style for deleted attribute Values
    IStyle validationErrorStyle4 = new Style();
    validationErrorStyle4.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationErrorStyle4.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle4, DisplayMode.NORMAL,
        "DELVALUE");
  }

  /**
   * Adds combo box with specifed cell
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
        if (canonicalValue instanceof Question) {
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