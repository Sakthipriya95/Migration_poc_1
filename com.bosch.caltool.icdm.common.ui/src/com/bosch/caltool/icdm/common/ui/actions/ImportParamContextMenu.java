/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.wizards.pages.CompareRuleImpWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.RuleDetailsSection;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;


/**
 * @author mkl2cob
 */
public class ImportParamContextMenu {

  /**
   * CompareRuleImpWizardPage
   */
  private final CompareRuleImpWizardPage compRuleImpWizardPage;

  /**
   * @param compareRuleImpWizardPage CompareRuleImpWizardPage
   */
  public ImportParamContextMenu(final CompareRuleImpWizardPage compRuleImpWizardPage) {
    this.compRuleImpWizardPage = compRuleImpWizardPage;
  }

  /**
   * Method to add mark as done menu item
   *
   * @param menuMgr MenuManager
   * @param selection IStructuredSelection
   * @param compareRuleImpWizardPage CompareRuleImpWizardPage
   */
  public void addMarkAsDoneMenu(final MenuManager menuMgr, final IStructuredSelection selection,
      final CompareRuleImpWizardPage compareRuleImpWizardPage) {
    Action markAsDoneAction = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        markUnmarkDoneFlag(selection, true, compareRuleImpWizardPage.getCompareGridTableViewer());
        // populate the rule details section to show the change in done flag
        compareRuleImpWizardPage.getRuleDtlsSection().populateRuleDetails();
      }

    };
    markAsDoneAction.setText("Mark selected parameter rules as 'Done'");
    markAsDoneAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.TICK_16X16));
    menuMgr.add(markAsDoneAction);
  }

  /**
   * Method to add unmark as done menu item
   *
   * @param selection
   * @param markFlag
   * @param compareGridTableViewer GridTableViewer
   */
  private void markUnmarkDoneFlag(final IStructuredSelection selection, final boolean markFlag,
      final GridTableViewer compareGridTableViewer) {
    List selectedObjsList = selection.toList();
    for (Object compObj : selectedObjsList) {
      if (compObj instanceof CalDataImportComparisonModel) {
        CalDataImportComparisonModel calDataCompObj = (CalDataImportComparisonModel) compObj;
        boolean unitValid = true;
        if (markFlag) {
          // do unit validation only for 'Check All'
          unitValid = this.compRuleImpWizardPage.validateUnit(calDataCompObj);
        }
        if (unitValid) {
          calDataCompObj.setUpdateInDB(markFlag);


          CompareRuleImpWizardPage compRuleImpWizardPage2 = ImportParamContextMenu.this.compRuleImpWizardPage;
          RuleDetailsSection ruleDetailsSection = compRuleImpWizardPage2.getRuleDtlsSection();
          ruleDetailsSection.getRefVaImpValue().setSelection(true);
          ruleDetailsSection.getUseImpValueMaturityLvl().setSelection(true);
          ruleDetailsSection.getLowLimitImpValue().setSelection(true);
          ruleDetailsSection.getUseUnitBtn().setSelection(true);
          ruleDetailsSection.getUpperLimitImpValue().setSelection(true);


        }
      }
    }
    compareGridTableViewer.refresh();
  }

  /**
   * Method to add mark as not done menu item
   * 
   * @param menuMgr MenuManager
   * @param selection IStructuredSelection
   * @param compareRuleImpWizardPage CompareRuleImpWizardPage
   */
  public void addMarkAsNotDoneMenu(final MenuManager menuMgr, final IStructuredSelection selection,
      final CompareRuleImpWizardPage compareRuleImpWizardPage) {
    Action markAsNotDoneAction = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        markUnmarkDoneFlag(selection, false, compareRuleImpWizardPage.getCompareGridTableViewer());
        // populate the rule details section to show the change in done flag
        compareRuleImpWizardPage.getRuleDtlsSection().populateRuleDetails();
        CompareRuleImpWizardPage compRuleImpWizardPage2 = ImportParamContextMenu.this.compRuleImpWizardPage;
        RuleDetailsSection ruleDetailsSection = compRuleImpWizardPage2.getRuleDtlsSection();


        ruleDetailsSection.getRefVaImpValue().setSelection(false);
        ruleDetailsSection.getUseImpValueMaturityLvl().setSelection(false);
        ruleDetailsSection.getLowLimitImpValue().setSelection(false);
        ruleDetailsSection.getUseUnitBtn().setSelection(false);
        ruleDetailsSection.getUpperLimitImpValue().setSelection(false);
      }
    };
    markAsNotDoneAction.setText("Mark selected parameter rules as 'Not Done'");
    markAsNotDoneAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    menuMgr.add(markAsNotDoneAction);
  }

}
