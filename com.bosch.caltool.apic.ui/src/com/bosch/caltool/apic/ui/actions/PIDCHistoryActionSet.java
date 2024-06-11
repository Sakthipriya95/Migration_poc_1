/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.table.filters.PIDCHistoryToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.PIDCHistoryViewPart;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;


/**
 * @author dmo5cob
 */
public class PIDCHistoryActionSet {

  // ICDM-2614
  /**
   * the pidc attr changes action
   */
  private Action pidcAttrsChangesAction;

  // ICDM-2614
  /**
   * the pidc changes action
   */
  private Action pidcChangesAction;

  /**
   * @param mgr instance
   * @param pidcHistoryViewPart instance
   * @param pidcHistoryToolBarFilter instance
   */
  public void createPIDCHistoryChangesAction(final IToolBarManager mgr, final PIDCHistoryViewPart pidcHistoryViewPart,
      final PIDCHistoryToolBarFilters pidcHistoryToolBarFilter) {

    this.pidcChangesAction = new Action("Changes on PIDC/Variants/Sub-Variants", SWT.TOGGLE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        pidcHistoryToolBarFilter.setPidcChangesFlag(isChecked());
        pidcHistoryViewPart.getHistoryTableViewer().refresh();
      }
    };
    this.pidcChangesAction.setChecked(true);
    // Set the image for Structured attr filter action
    this.pidcChangesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_NODE_28X30));
    mgr.add(this.pidcChangesAction);
  }

  /**
   * @param mgr instance
   * @param pidcHistoryToolBarFilter instance
   * @param pidcHistoryViewPart instance
   */
  public void createPIDCAttrsHistoryChangesAction(final IToolBarManager mgr,
      final PIDCHistoryViewPart pidcHistoryViewPart, final PIDCHistoryToolBarFilters pidcHistoryToolBarFilter) {

    this.pidcAttrsChangesAction = new Action("Changes on Attributes", SWT.TOGGLE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        pidcHistoryToolBarFilter.setAttrChangesFlag(isChecked());
        pidcHistoryViewPart.getHistoryTableViewer().refresh();
      }
    };
    this.pidcAttrsChangesAction.setChecked(true);
    // Set the image for Structured attr filter action
    this.pidcAttrsChangesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ATTRIBUTES_EDITOR_16X16));
    mgr.add(this.pidcAttrsChangesAction);
  }

  // iCDM-2614
  /**
   * @param mgr instance
   * @param pidcHistoryToolBarFilter instance
   * @param pidcHistoryViewPart instance
   */
  public void createFocusMatrixHistoryChangesAction(final IToolBarManager mgr,
      final PIDCHistoryViewPart pidcHistoryViewPart, final PIDCHistoryToolBarFilters pidcHistoryToolBarFilter) {

    Action pidcfocusMatrixChangesAction = new Action("Changes on Focus Matrix Attributes", SWT.TOGGLE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        pidcHistoryToolBarFilter.setFmChangesFlag(isChecked());
        pidcHistoryViewPart.getHistoryTableViewer().refresh();
      }
    };
    pidcfocusMatrixChangesAction.setChecked(true);
    // Set the image for Structured attr filter action
    pidcfocusMatrixChangesAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FOCUS_MATRIX_16X16));
    mgr.add(pidcfocusMatrixChangesAction);
  }

  /**
   * @param mgr instance
   * @param pidcHistoryToolBarFilter instance
   * @param pidcHistoryViewPart instance
   */
  public void createAttrsSyncAction(final IToolBarManager mgr, final PIDCHistoryViewPart pidcHistoryViewPart,
      final PIDCHistoryToolBarFilters pidcHistoryToolBarFilter) {

    Action syncAction = new Action(ApicUiConstants.SYNCHRONIZE_ATTRIBUTE, SWT.TOGGLE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        pidcHistoryToolBarFilter.setAttrSyncFlag(isChecked());
        pidcHistoryViewPart.getHistoryTableViewer().refresh();
        pidcHistoryViewPart.setTitleDescription();
      }


    };
    syncAction.setId(ApicUiConstants.SYNCHRONIZE_ATTRIBUTE);
    // Set the image for Structured attr filter action
    syncAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.LINK_EDITOR_16X16));
    mgr.add(syncAction);
  }

  /**
   * @param mgr instance
   * @param pidcHistoryToolBarFilter instance
   * @param pidcHistoryViewPart instance
   */
  public void createLevelsSyncAction(final IToolBarManager mgr, final PIDCHistoryViewPart pidcHistoryViewPart,
      final PIDCHistoryToolBarFilters pidcHistoryToolBarFilter) {

    Action syncLevelsAction = new Action(ApicUiConstants.SYNCHRONIZE_PIDC_LEVELS, SWT.TOGGLE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        pidcHistoryToolBarFilter.setLevelSyncFlag(isChecked());
        pidcHistoryViewPart.getHistoryTableViewer().refresh();
        pidcHistoryViewPart.setTitleDescription();
      }
    };
    syncLevelsAction.setId(ApicUiConstants.SYNCHRONIZE_PIDC_LEVELS);
    // Set the image for Structured attr filter action
    syncLevelsAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PIDC_HISTORY_LINK_LEVELS_16X16));
    mgr.add(syncLevelsAction);
  }

  /**
   * @param mgr instance
   * @param pidcHistoryViewPart instance
   */
  public void createRefreshAction(final IToolBarManager mgr, final PIDCHistoryViewPart pidcHistoryViewPart) {

    Action refreshAction = new Action(ApicUiConstants.REFRESH, SWT.PUSH) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        pidcHistoryViewPart.populateHistoryTable();
        pidcHistoryViewPart.getHistoryTableViewer().refresh();
        setChecked(false);
      }
    };

    // Set the image for Structured attr filter action
    refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REFRESH_16X16));
    mgr.add(refreshAction);
  }


  // ICDM-2614
  /**
   * @return the pidcAttrsChangesAction
   */
  public Action getPidcAttrsChangesAction() {
    return this.pidcAttrsChangesAction;
  }


  // ICDM-2614
  /**
   * @return the pidcChangesAction
   */
  public Action getPidcChangesAction() {
    return this.pidcChangesAction;
  }


}