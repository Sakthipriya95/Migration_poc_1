/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.table.filters.PIDCSearchResultToolBarFilters;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;


/**
 * @author rgo7cob
 */
public class PIDCSearResToolBarActionSet {

  /**
   * Defines the action for PIDC with A2l Files
   */
  private Action hasA2lFilesAction;
  /**
   * Defines the action for PIDC with Review Files
   */
  private Action hasReviewsAction;
  private Action noA2LAction;

  /**
   * Defines the action for PIDC without focus matrix
   */

  /**
   * Icdm-1283
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param resultTable resultTable
   */
  public void pidcA2lFilesFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchResultToolBarFilters toolBarFilters, final CustomGridTableViewer resultTable) {
    this.hasA2lFilesAction = new Action("Show PIDC with A2L files, but no calibration data review", SWT.TOGGLE) {

      @Override
      public void run() {
        // has A2l Files an n review.
        toolBarFilters.setShowPIDCA2lFiles(PIDCSearResToolBarActionSet.this.hasA2lFilesAction.isChecked());
        resultTable.refresh();
      }
    };
    this.hasA2lFilesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.A2LFILE_16X16));
    this.hasA2lFilesAction.setChecked(true);
    toolBarManager.add(this.hasA2lFilesAction);

  }

  /**
   * Icdm-1283
   *
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param resultTable resultTable
   */
  public void pidcReviewFilesFilterAvtion(final ToolBarManager toolBarManager,
      final PIDCSearchResultToolBarFilters toolBarFilters, final CustomGridTableViewer resultTable) {
    // has A2l Files and review.
    this.hasReviewsAction = new Action("Show PIDC with both A2L files and calibration data reviews", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setShowPIDCRrvwFiles(PIDCSearResToolBarActionSet.this.hasReviewsAction.isChecked());
        resultTable.refresh();
      }
    };
    this.hasReviewsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.RVW_RES_CLOSED_16X16));
    this.hasReviewsAction.setChecked(true);
    toolBarManager.add(this.hasReviewsAction);

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param resultTable resultTable
   */
  public void pidcAWithNoA2lFilterAction(final ToolBarManager toolBarManager,
      final PIDCSearchResultToolBarFilters toolBarFilters, final CustomGridTableViewer resultTable) {
    // no a2l File
    this.noA2LAction = new Action("Show PIDC without A2L files and calibration data reviews", SWT.TOGGLE) {

      @Override
      public void run() {
        toolBarFilters.setShowPIDCwithNoA2L(PIDCSearResToolBarActionSet.this.noA2LAction.isChecked());
        resultTable.refresh();
      }
    };
    this.noA2LAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16));
    this.noA2LAction.setChecked(true);
    toolBarManager.add(this.noA2LAction);

  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param resultTable resultTable
   */
  // iCDM-2255
  public void pidcWithNoFocusMatrix(final ToolBarManager toolBarManager,
      final PIDCSearchResultToolBarFilters toolBarFilters, final CustomGridTableViewer resultTable) {
    Action noFocusMatrixAction = new Action("Show PIDC without Focus matrix", SWT.TOGGLE) {

      /**
       * the action after choosing without focus matrix context menu
       */
      @Override
      public void run() {
        toolBarFilters.setShowPIDCwithNoFocusMatrix(isChecked());
        resultTable.refresh();
      }
    };
    noFocusMatrixAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UNMAPPED_FOCUS_MATRIX_ICON_16X16));
    noFocusMatrixAction.setChecked(true);
    toolBarManager.add(noFocusMatrixAction);
  }

  /**
   * @param toolBarManager toolBarManager
   * @param toolBarFilters toolBarFilters
   * @param resultTable resultTable
   */
  // iCDM-2255
  public void pidcWithFocusMatrix(final ToolBarManager toolBarManager,
      final PIDCSearchResultToolBarFilters toolBarFilters, final CustomGridTableViewer resultTable) {
    Action yesfocusMatrixAction = new Action("Show PIDC with Focus matrix", SWT.TOGGLE) {

      /**
       * the action after choosing with focus matrix context menu
       */
      @Override
      public void run() {
        toolBarFilters.setShowPIDCwithFocusMatrix(isChecked());
        resultTable.refresh();
      }
    };
    yesfocusMatrixAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.MAPPED_FOCUS_MATRIX_ICON_16X16));
    yesfocusMatrixAction.setChecked(true);
    toolBarManager.add(yesfocusMatrixAction);
  }


}
