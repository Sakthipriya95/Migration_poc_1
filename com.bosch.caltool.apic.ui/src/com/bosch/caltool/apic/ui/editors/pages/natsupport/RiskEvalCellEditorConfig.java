/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.pages.RiskEvalNatTableSection;
import com.bosch.caltool.apic.ui.editors.pages.natsupport.RiskEvalCellStyleConfig.RM_STYLE_CODE;
import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;
import com.bosch.caltool.icdm.client.bo.apic.PidcRiskResultHandler;
import com.bosch.caltool.icdm.model.rm.RmCategory;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcRmProjCharClient;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * The Class RiskEvalChkBoxConfig.
 *
 * @author gge6cob
 */
public class RiskEvalCellEditorConfig extends AbstractRegistryConfiguration {


  /** The custom filter grid layer. */
  private final CustomFilterGridLayer<PidcRMCharacterMapping> customFilterGridLayer;

  /** The nat section. */
  private final RiskEvalNatTableSection natSection;

  /** The client. */
  PidcRmProjCharClient client = null;
  PidcRmProjCharClient matrixClient = null;

  private final PidcRiskResultHandler resultHandler;

  private final boolean isEditable;


  /**
   * Instantiates a new risk eval chk box config.
   *
   * @param natSection {@link RiskEvalNatTableSection}
   */
  public RiskEvalCellEditorConfig(final RiskEvalNatTableSection natSection) {
    this.natSection = natSection;
    this.isEditable = natSection.isModifiable();
    this.customFilterGridLayer = natSection.getCustomFilterGridLayer();
    this.resultHandler = natSection.getResultHandler();
    this.client = new PidcRmProjCharClient();
    this.matrixClient = new PidcRmProjCharClient();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // register columns as checkbox
    registerConfigLabelsOnColumns(this.customFilterGridLayer.getBodyLabelAccumulator());

    // register columns as checkbox (disabled)
    registerConfigLabelsOnColumnsDisabled(this.customFilterGridLayer.getBodyLabelAccumulator());

    // register checkbox editors
    registerCheckBoxEditor(configRegistry);
    // register combo box
    registerRBInputDataComboBoxEditor(configRegistry);

    // register edit rules and comparator
    registerEditableRules(configRegistry);

    // register combo box
    registerComboBoxEditor(configRegistry);

    // Refresh toolbar filters
    refreshToolbarFilters();
  }

  /**
   * @param bodyLabelAccumulator
   */
  private void registerConfigLabelsOnColumnsDisabled(final ColumnOverrideLabelAccumulator columnLabelAccumulator) {
    // If Disabled
    columnLabelAccumulator.registerColumnOverrides(RiskEvalNatTableSection.RELEVANT_YES_COLNUM,
        RM_STYLE_CODE.RELEVANT_YES_DISABLED.getEditorCode(), RM_STYLE_CODE.RELEVANT_YES_DISABLED.getStyleCode());
    columnLabelAccumulator.registerColumnOverrides(RiskEvalNatTableSection.RELEVANT_NO_COLNUM,
        RM_STYLE_CODE.RELEVANT_NO_DISABLED.getEditorCode(), RM_STYLE_CODE.RELEVANT_NO_DISABLED.getStyleCode());
    columnLabelAccumulator.registerColumnOverrides(RiskEvalNatTableSection.RELEVANT_NA_COLNUM,
        RM_STYLE_CODE.RELEVANT_NA_DISABLED.getEditorCode(), RM_STYLE_CODE.RELEVANT_NA_DISABLED.getStyleCode());

  }

  /**
   * @param configRegistry
   */
  private void registerComboBoxEditor(final IConfigRegistry configRegistry) {

    String label = RiskEvalCellStyleConfig.RB_SHARE_COMBO;
    // combo box values
    List<String> comboList = new ArrayList<String>();
    // get the share to be set in combo
    if (this.resultHandler != null) {
      Map<Long, RmCategory> shareMap = this.resultHandler.getAllRBSwShareCategoryMap();
      for (RmCategory share : shareMap.values()) {
        comboList.add(share.getValue());
      }
    }
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        if (canonicalValue instanceof PidcRMCharacterMapping) {
          return "";
        }
        return super.canonicalToDisplayValue(canonicalValue);
      }
    }, DisplayMode.NORMAL, label);

    ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList, comboList.size());
    // register the cell properties
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
        label);
  }

  /**
   * @param configRegistry
   */
  private void registerRBInputDataComboBoxEditor(final IConfigRegistry configRegistry) {

    String label = RiskEvalCellStyleConfig.RB_INPUT_DATA_COMBO;
    // combo box values
    List<String> comboList = new ArrayList<String>();
    SortedSet<String> vals = new TreeSet<String>();
    // get the share to be set in combo
    if (this.resultHandler != null) {
      Map<Long, RmCategory> inputDataMap = this.resultHandler.getAllRBInputDataMap();

      for (RmCategory input : inputDataMap.values()) {
        vals.add(input.getName());
      }
      for (String string : vals) {
        comboList.add(string);
      }
    }
    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public Object canonicalToDisplayValue(final Object canonicalValue) {
        if (canonicalValue instanceof PidcRMCharacterMapping) {
          return "";
        }
        return super.canonicalToDisplayValue(canonicalValue);
      }
    }, DisplayMode.NORMAL, label);

    ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(comboList, comboList.size());
    // register the cell properties
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
        label);
  }

  /**
   * Register label on columns.
   *
   * @param columnLabelAccumulator the column label accumulator
   */
  private void registerConfigLabelsOnColumns(final ColumnOverrideLabelAccumulator columnLabelAccumulator) {
    columnLabelAccumulator.registerColumnOverrides(RiskEvalNatTableSection.RELEVANT_YES_COLNUM,
        RM_STYLE_CODE.RELEVANT_YES.getEditorCode(), RM_STYLE_CODE.RELEVANT_YES.getStyleCode());
    columnLabelAccumulator.registerColumnOverrides(RiskEvalNatTableSection.RELEVANT_NO_COLNUM,
        RM_STYLE_CODE.RELEVANT_NO.getEditorCode(), RM_STYLE_CODE.RELEVANT_NO.getStyleCode());
    columnLabelAccumulator.registerColumnOverrides(RiskEvalNatTableSection.RELEVANT_NA_COLNUM,
        RM_STYLE_CODE.RELEVANT_NA.getEditorCode(), RM_STYLE_CODE.RELEVANT_NA.getStyleCode());

  }

  /**
   * Refresh toolbar filters.
   */
  public void refreshToolbarFilters() {
    /*
     * Trigger Natpage toolbar-filter event
     */
    if (RiskEvalCellEditorConfig.this.natSection.getToolBarAction() != null) {
      RiskEvalCellEditorConfig.this.natSection.getToolBarAction().getShowAllEntriesAction().runWithEvent(new Event());
    }
  }

  /**
   * Register check box editor.
   *
   * @param configRegistry the config registry
   */
  private void registerCheckBoxEditor(final IConfigRegistry configRegistry) {

    // YES column
    CheckBoxCellEditor checkbox_yes = new CheckBoxCellEditor() {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Control activateCell(final Composite parent1, final Object originalCanonicalValue) {
        Control control = super.activateCell(parent1, originalCanonicalValue);
        Runnable busyRunnable = new Runnable() {

          @Override
          public void run() {
            refreshToolbarFilters();
          }
        };
        BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
        return control;
      }
    };
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkbox_yes, DisplayMode.NORMAL,
        RM_STYLE_CODE.RELEVANT_YES.getEditorCode());


    // NO column
    CheckBoxCellEditor checkbox_no = new CheckBoxCellEditor() {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Control activateCell(final Composite parent1, final Object originalCanonicalValue) {
        Control control = super.activateCell(parent1, originalCanonicalValue);
        Runnable busyRunnable = new Runnable() {

          @Override
          public void run() {
            refreshToolbarFilters();
          }
        };
        BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
        return control;
      }
    };
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkbox_no, DisplayMode.NORMAL,
        RM_STYLE_CODE.RELEVANT_NO.getEditorCode());


    // NA column
    CheckBoxCellEditor checkbox_na = new CheckBoxCellEditor() {

      /**
       * {@inheritDoc}
       */
      @Override
      protected Control activateCell(final Composite parent1, final Object originalCanonicalValue) {
        Control control = super.activateCell(parent1, originalCanonicalValue);
        Runnable busyRunnable = new Runnable() {

          @Override
          public void run() {
            refreshToolbarFilters();
          }
        };
        BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), busyRunnable);
        return control;
      }
    };
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, checkbox_na, DisplayMode.NORMAL,
        RM_STYLE_CODE.RELEVANT_NA.getEditorCode());


  }


  /**
   * Register editable rules.
   *
   * @param configRegistry the config registry
   */
  private void registerEditableRules(final IConfigRegistry configRegistry) {

    IEditableRule editRule = IEditableRule.NEVER_EDITABLE;
    if (this.isEditable) {
      editRule = IEditableRule.ALWAYS_EDITABLE;
    }
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.EDIT,
        RM_STYLE_CODE.RELEVANT_YES.getStyleCode());
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.EDIT,
        RM_STYLE_CODE.RELEVANT_NO.getStyleCode());
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.EDIT,
        RM_STYLE_CODE.RELEVANT_NA.getStyleCode());
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.NORMAL,
        RiskEvalCellStyleConfig.RB_INPUT_DATA_COMBO);
    configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, editRule, DisplayMode.NORMAL,
        RiskEvalCellStyleConfig.RB_SHARE_COMBO);
  }
}
