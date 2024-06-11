/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.bosch.caltool.apic.ui.editors.compare.PIDCCompareNatTableCheckBoxCellEditor;
import com.bosch.caltool.apic.ui.editors.pages.ResponsibilityPage;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;

/**
 * @author mkl2cob
 */
public class RespComboEditConfiguration extends AbstractRegistryConfiguration {


  private final ResponsibilityPage respPage;

  /**
   * @param responsibilityPage ResponsibilityPage
   */
  public RespComboEditConfiguration(final ResponsibilityPage responsibilityPage) {
    this.respPage = responsibilityPage;
  }


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
    // Add all the values to comboList

    for (WpRespType respType : WpRespType.values()) {
      comboList.add(respType.getDispName());
    }
    // set the combobox config for score
    getComboStyle(configRegistry, "RESP_TYPE", IEditableRule.ALWAYS_EDITABLE, comboList, style);

    // configuration for default workpackage in a2l view
    IStyle defaultRespStyle = new Style();
    defaultRespStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    defaultRespStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, defaultRespStyle, DisplayMode.NORMAL,
        ApicUiConstants.CONFIG_LABEL_DISABLE);

    // configuration to grey out background for non editable cell in deleted column
    IStyle styleForNotDeletable = new Style();
    styleForNotDeletable.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    styleForNotDeletable.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, styleForNotDeletable, DisplayMode.NORMAL,
        ApicUiConstants.CONFIG_LABEL_NOT_DELETABLE);

    // set check box configuration for deleted column
    registerCheckBoxEditor(configRegistry);

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

    // reister for cell painter and cell editor
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
        label);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(), DisplayMode.NORMAL,
        label);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.NORMAL,
        label);
  }

  /**
   * Registering check box editor
   *
   * @param configRegistry
   */
  private void registerCheckBoxEditor(final IConfigRegistry configRegistry) {
    // register checkbox painter for editable checkbox
    Style cellStyleUC = new Style();
    cellStyleUC.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyleUC, NORMAL,
        ApicUiConstants.CHECK_BOX_CONFIG_LABEL);
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
        ApicUiConstants.CHECK_BOX_CONFIG_LABEL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(),
        NORMAL, ApicUiConstants.CHECK_BOX_CONFIG_LABEL);

    // register Background painter to hide check box
    configRegistry.registerConfigAttribute(CELL_PAINTER, new BackgroundPainter(), NORMAL, ApicUiConstants.EMPTY_LABEL);

    // register checkbox painter for not editable checkbox
    configRegistry.registerConfigAttribute(CELL_PAINTER, new CheckBoxPainter(), NORMAL,
        ApicUiConstants.CONFIG_LABEL_NOT_DELETABLE);

    // checkbox cell editor
    PIDCCompareNatTableCheckBoxCellEditor checkBoxCellEditor = new PIDCCompareNatTableCheckBoxCellEditor() {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Control activateCell(final Composite parent, final Object originalCanonicalValue) {

        // get the selection
        IStructuredSelection selection =
            (IStructuredSelection) RespComboEditConfiguration.this.respPage.getSelectionProvider().getSelection();
        if ((null != selection) && (null != selection.getFirstElement())) {
          A2lResponsibility selA2lRespToDel = (A2lResponsibility) selection.getFirstElement();
          // Update deleted flag in selected responsibility
          A2lResponsibility respToEdit = new A2lResponsibility();
          // Shallow copy the selA2lRespToDel to respToEdit
          CommonUtils.shallowCopy(respToEdit, selA2lRespToDel);
          // set deleted if respToEdit is not deleted
          respToEdit.setDeleted(!respToEdit.isDeleted());
          RespComboEditConfiguration.this.respPage.getWorkPkgResponsibilityBO().editA2lResp(respToEdit);
        }
        return super.activateCell(parent, originalCanonicalValue);
      }
    };
    // register check box cell editor
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE,
        DisplayMode.EDIT, ApicUiConstants.CHECK_BOX_EDITOR_CNG_LBL);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkBoxCellEditor, DisplayMode.EDIT,
        ApicUiConstants.CHECK_BOX_EDITOR_CNG_LBL);
  }

}
