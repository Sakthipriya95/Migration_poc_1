/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author dja7cob Class for Risk Evaluation page Tree viewer Toolbar actions
 */
public class RiskEvalPageToolBarActionSet {

  private Action addRiskEvalAction;

  private Action riskEvalTreeSettings;

  /**
   * @param leftSectiontoolBarManager Toolbar manager instance
   */
  public void addNewRiskEvalAction(final ToolBarManager leftSectiontoolBarManager) {
    this.addRiskEvalAction = new Action(CommonUIConstants.ADD_RISK_EVAL, SWT.TOGGLE) {

      @Override
      public void run() {
        // TO-DO
      }
    };
    // Set the image for Add action
    this.addRiskEvalAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.addRiskEvalAction.setEnabled(false);
    leftSectiontoolBarManager.add(this.addRiskEvalAction);
  }

  /**
   * @param leftSectiontoolBarManager Toolbar manager instance
   */
  public void riskEvalSettingsAction(final ToolBarManager leftSectiontoolBarManager) {
    this.riskEvalTreeSettings = new Action(CommonUIConstants.RISK_EVAL_TREE_SETTINGS, SWT.TOGGLE) {

      @Override
      public void run() {
        // TO-DO
      }
    };
    // Set the image for tree viewer settings action
    this.riskEvalTreeSettings.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SETTINGS_16X16));
    this.riskEvalTreeSettings.setEnabled(false);
    leftSectiontoolBarManager.add(this.riskEvalTreeSettings);
  }

  /**
   * @return the addRiskEvalAction
   */
  public Action getAddRiskEvalAction() {
    return this.addRiskEvalAction;
  }


  /**
   * @param addRiskEvalAction the addRiskEvalAction to set
   */
  public void setAddRiskEvalAction(final Action addRiskEvalAction) {
    this.addRiskEvalAction = addRiskEvalAction;
  }

  /**
   * @return the riskEvalTreeSettings
   */
  public Action getRiskEvalTreeSettings() {
    return this.riskEvalTreeSettings;
  }

  /**
   * @param riskEvalTreeSettings the riskEvalTreeSettings to set
   */
  public void setRiskEvalTreeSettings(final Action riskEvalTreeSettings) {
    this.riskEvalTreeSettings = riskEvalTreeSettings;
  }
}
