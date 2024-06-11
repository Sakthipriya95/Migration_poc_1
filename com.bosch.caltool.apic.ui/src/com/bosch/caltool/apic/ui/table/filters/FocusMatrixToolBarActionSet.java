/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.nebula.widgets.nattable.filterrow.event.FilterAppliedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.bosch.caltool.apic.ui.jobs.FocusMatrixExportJob;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.excel.ExcelConstants;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixDataHandler;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * This class is for toolbar actions in focus matrix page
 *
 * @author mkl2cob
 */
public class FocusMatrixToolBarActionSet {


  /**
   * CustomFilterGridLayer
   */
  private final CustomFilterGridLayer<FocusMatrixAttributeClientBO> customFilterGridLayer;
  private final FocusMatrixDataHandler focusMatrixDataHandler;

  /**
   * @param customFilterGridLayer2 CustomFilterGridLayer<FocusMatrixAttribute>
   * @param focusMatrixDataHandler
   */
  public FocusMatrixToolBarActionSet(final CustomFilterGridLayer<FocusMatrixAttributeClientBO> customFilterGridLayer2,
      final FocusMatrixDataHandler focusMatrixDataHandler) {
    this.customFilterGridLayer = customFilterGridLayer2;
    this.focusMatrixDataHandler = focusMatrixDataHandler;
  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FocusMatrixToolBarFilters
   */
  public void showAllAttrAction(final ToolBarManager toolBarManager, final FocusMatrixToolBarFilters toolBarFilters) {
    // get the tooltip from the T_MESSAGES table
    final Action invisibleAttrAction =
        new Action(CommonUiUtils.getMessage("PIDC_EDITOR", "MISSING_DEP_TOOLBAR_FILTER"), SWT.TOGGLE) {

          @Override
          public void run() {
            // set the boolean in toolbar filters class based on the toggle button
            toolBarFilters.setAttrVisibility(isChecked());
            FocusMatrixToolBarActionSet.this.focusMatrixDataHandler.setAllAttrNeededForExport(isChecked());

            // fire the filter event in all columns
            FocusMatrixToolBarActionSet.this.customFilterGridLayer.getFilterStrategy()
                .applyToolBarFilterInAllColumns(false);
            FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()
                .fireLayerEvent(new FilterAppliedEvent(
                    FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()));
          }


        };
    // Set the image for PIDC Not Dependent Filter
    invisibleAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ALL_ATTR_16X16));
    // keep the action unpressed for the first time
    invisibleAttrAction.setChecked(false);
    toolBarManager.add(invisibleAttrAction);
    // triggering the filter externally for the first time
    if (!invisibleAttrAction.isChecked()) {
      invisibleAttrAction.run();
    }


  }

  // ICDM-1624
  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FocusMatrixToolBarFilters
   */
  public void showMappedAttrAction(final ToolBarManager toolBarManager,
      final FocusMatrixToolBarFilters toolBarFilters) {
    Action mappedAttributeAction = new Action("Attributes mapped to usecase item", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in toolbar filters class based on the toggle button
        toolBarFilters.setMappedAttrFilter(isChecked());
        // fire the filter event in all columns
        FocusMatrixToolBarActionSet.this.customFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()));


      }
    };
    mappedAttributeAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UC_PROPOSAL_ICON_16X16));
    // keep the action pressed for the first time
    mappedAttributeAction.setChecked(true);
    toolBarManager.add(mappedAttributeAction);
    // triggering the filter externally for the first time
    if (mappedAttributeAction.isChecked()) {
      mappedAttributeAction.run();
    }

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FocusMatrixToolBarFilters
   */
  public void showUnMappedAttrAction(final ToolBarManager toolBarManager,
      final FocusMatrixToolBarFilters toolBarFilters) {
    Action unMappedAttrAction = new Action("Unmapped attributes", SWT.TOGGLE) {

      @Override
      public void run() {
        // set the boolean in toolbar filters class based on the toggle button
        toolBarFilters.setUnMappedAttrFilter(isChecked());
        // fire the filter event in all columns
        FocusMatrixToolBarActionSet.this.customFilterGridLayer.getFilterStrategy()
            .applyToolBarFilterInAllColumns(false);
        FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()
            .fireLayerEvent(new FilterAppliedEvent(
                FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()));


      }
    };
    unMappedAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.NONE_16X16));
    // keep the action pressed for the first time
    unMappedAttrAction.setChecked(true);
    toolBarManager.add(unMappedAttrAction);
    // triggering the filter externally for the first time
    if (unMappedAttrAction.isChecked()) {
      unMappedAttrAction.run();
    }

  }

  /**
   * @param toolBarManager ToolBarManager
   * @param toolBarFilters FocusMatrixToolBarFilters
   */
  public void showRelavantFMAction(final ToolBarManager toolBarManager,
      final FocusMatrixToolBarFilters toolBarFilters) {
    Action relavantAttrAction =
        new Action("Attributes relavant to Focus Matrix only in this PIDC Version", SWT.TOGGLE) {

          @Override
          public void run() {
            // set the boolean in toolbar filters class based on the toggle button
            toolBarFilters.setRelavantAttrFilter(isChecked());
            // fire the filter event in all columns
            FocusMatrixToolBarActionSet.this.customFilterGridLayer.getFilterStrategy()
                .applyToolBarFilterInAllColumns(false);
            FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()
                .fireLayerEvent(new FilterAppliedEvent(
                    FocusMatrixToolBarActionSet.this.customFilterGridLayer.getSortableColumnHeaderLayer()));


          }
        };
    relavantAttrAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_COLUMN_16X16));
    // keep the action pressed for the first time
    relavantAttrAction.setChecked(true);
    toolBarManager.add(relavantAttrAction);
    // triggering the filter externally for the first time
    if (relavantAttrAction.isChecked()) {
      relavantAttrAction.run();
    }

  }

  // ICDM-2614
  /**
   * @param toolBarManager toolBarManager
   * @param pidcVersion the pidc version
   */
  public void createFocusMatrixHistoryAction(final ToolBarManager toolBarManager, final PidcVersion pidcVersion) {

    Action focusMatrixHistoryAction = new Action(ApicUiConstants.FOCUS_MATRIX_HISTORY_TITLE, SWT.BUTTON1) {

      @Override
      public void run() {
        // TODO
      }
    };
    // Set the image for pidc history action
    focusMatrixHistoryAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FOCUS_MATRIX_HISTORY_16X16));
    toolBarManager.add(focusMatrixHistoryAction);
  }

  /**
   * @param fmDataHandler FocusMatrixDataHandler
   */
  public void exportFocusMatrixAction(final FocusMatrixDataHandler fmDataHandler) {
    StringBuilder fileName = new StringBuilder();
    fileName.append(fmDataHandler.getPidcVersion().getName()).append("_Focus Matrix");
    String formatFileName = FileNameUtil.formatFileName(fileName.toString(), ApicConstants.INVALID_CHAR_PTRN);
    FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
    fileDialog.setText("Save Excel Report");
    fileDialog.setFilterExtensions(ExcelConstants.FILTER_EXCEL_EXTN_WITH_STAR);
    fileDialog.setFilterNames(ExcelConstants.FILTER_NAMES);
    fileDialog.setFilterIndex(0);
    fileDialog.setFileName(formatFileName);
    fileDialog.setOverwrite(true);
    String fileSelected = fileDialog.open();

    if (fileSelected != null) {
      String fileExtn = ExcelConstants.FILTER_EXTNS[fileDialog.getFilterIndex()];
      // Call for export the Usecase
      FocusMatrixExportJob job = new FocusMatrixExportJob(new MutexRule(), fileSelected, fileExtn, fmDataHandler);
      com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance()
          .showView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROGRESS_VIEW);
      job.schedule();
    }
  }
}
