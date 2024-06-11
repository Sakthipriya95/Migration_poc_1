/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalAttrValWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalCDRFltrSltnPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalRecommendedValuesPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalRuleSetSltnPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalSeriesFltrSltnPage;


/**
 * @author rgo7cob Icdm-697 Export CDF dialog
 */
public class PreCalDataExportWizardDialog extends AbstractWizardDialog {

  /**
   * Width hint
   */
  private static final int HEIGHT_HINT = 700;
  /**
   * Height hint
   */
  private static final int WIDTH_HINT = 600;

  /**
   * @param parentShell parentShell
   * @param newWizard newWizard
   */
  public PreCalDataExportWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    return super.createContents(parent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set size of shell
    newShell.setSize(WIDTH_HINT, HEIGHT_HINT);
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    // Next pressed
    if (buttonId == IDialogConstants.NEXT_ID) {
      // Get the current page
      final IWizardPage currentPage = getCurrentPage();
      if (currentPage instanceof PreCalCDRFltrSltnPage) {
        final PreCalCDRFltrSltnPage filSelWizardPage = (PreCalCDRFltrSltnPage) currentPage;
        // ICdm-976 pass the shell Object
        PreCalDataExportWizard expWizard = filSelWizardPage.getWizard();
        expWizard.getDataHandler().setExactMatchOnly(filSelWizardPage.getExactMatchOnlySelectionState());
        filSelWizardPage.nextPressed(getShell());
      }
      if (currentPage instanceof PreCalRuleSetSltnPage) {
        final PreCalRuleSetSltnPage filSelWizardPage = (PreCalRuleSetSltnPage) currentPage;
        // ICdm-976 pass the shell Object

        filSelWizardPage.nextPressed(getShell());
      }
      if (currentPage instanceof PreCalAttrValWizardPage) {
        final PreCalAttrValWizardPage filSelWizardPage = (PreCalAttrValWizardPage) currentPage;
        // Minimise the dialog
        getShell().pack();
        getShell().setMinimized(true);
        // ICdm-976 pass the shell Object
        filSelWizardPage.nextPressed(getShell());
      }
      if (currentPage instanceof PreCalSeriesFltrSltnPage) {
        final PreCalSeriesFltrSltnPage filSelWizardPage = (PreCalSeriesFltrSltnPage) currentPage;
        if (filSelWizardPage.checkMinMaxVal()) {
          // Minimise the dialog
          getShell().pack();
          getShell().setMinimized(true);
          // ICdm-976 pass the shell Object
          filSelWizardPage.nextPressed(getShell());
        }
      }
    }
    // If the back button is pressed from the last page.
    else if (buttonId == IDialogConstants.BACK_ID) {
      // Get the current page
      final IWizardPage currentPage = getCurrentPage();
      if (currentPage instanceof PreCalRecommendedValuesPage) {
        final PreCalRecommendedValuesPage filSelWizardPage = (PreCalRecommendedValuesPage) currentPage;
        filSelWizardPage.setPageComplete(false);
        filSelWizardPage.enableExportUI();
        // ICdm-976 remove the Exception Occured on Back Pressed
        getWizard().getDataHandler().setWsException(null);
      }
      else if (currentPage instanceof PreCalRuleSetSltnPage) {
        final PreCalRuleSetSltnPage ruleSetSelWizPage = (PreCalRuleSetSltnPage) currentPage;
        ruleSetSelWizPage.getRuleSetTabViewer().setSelection(null);
        getWizard().getDataHandler().setSelectedRuleSet(null);
      }

    }

    super.buttonPressed(buttonId);
  }

  // Icdm-758 make the style such that the application is not blocked when the dialog is open. Disable the Maximize
  // Button also.
  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE);
    setBlockOnOpen(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PreCalDataExportWizard getWizard() {
    return (PreCalDataExportWizard) super.getWizard();
  }

}
