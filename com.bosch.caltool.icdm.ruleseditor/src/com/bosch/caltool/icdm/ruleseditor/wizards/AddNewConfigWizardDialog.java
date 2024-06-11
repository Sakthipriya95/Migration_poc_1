/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.wizards;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleActionSet;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.ChooseRuleWizardPage;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.CreateEditRuleWizardPage;
import com.bosch.caltool.icdm.ruleseditor.wizards.pages.SelectAttrValWizardPage;


/**
 * ICDM-1081
 *
 * @author mkl2cob
 */
public class AddNewConfigWizardDialog extends AbstractWizardDialog {

  /**
   * height of rule wizard
   */
  private static final int RULE_WIZARD_HEIGHT = 700;
  /**
   * width of rule wizard
   */
  private static final int RULE_WIZARD_WIDTH = 800;
  /**
   * height of rule wizard for update
   */
  private static final int RULE_UPDATE_WIZARD_HEIGHT = 600;
  /**
   * width of rule wizard for update
   */
  private static final int RULE_UPDATE_WIZARD_WIDTH = 800;
  /**
   * wizard corresponding to this dialog
   */
  private final AddNewConfigWizard wizard;


  /**
   * @return the wizard
   */
  @Override
  public AddNewConfigWizard getWizard() {
    return this.wizard;
  }

  /**
   * @param parentShell parent shell
   * @param newWizard Wizard
   */
  public AddNewConfigWizardDialog(final Shell parentShell, final AddNewConfigWizard newWizard) {
    super(parentShell, newWizard);
    this.wizard = newWizard;
    // ICDM-1244
    this.wizard.setWizardDialog(this);
  }

  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE);
    setBlockOnOpen(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    if (this.wizard.isUpdate()) {
      newShell.setSize(RULE_UPDATE_WIZARD_WIDTH, RULE_UPDATE_WIZARD_HEIGHT);
    }
    else {
      newShell.setSize(RULE_WIZARD_WIDTH, RULE_WIZARD_HEIGHT);
    }
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    final IWizardPage currentPage = getCurrentPage();

    if (buttonId == IDialogConstants.NEXT_ID) {
      callNextPressed(currentPage);

    }
    else if (buttonId == IDialogConstants.BACK_ID) {
      callBackPressed(currentPage);
    }
    else if (buttonId == IDialogConstants.FINISH_ID) {
      callFinishPressed(currentPage);
    }
    super.buttonPressed(buttonId);
  }

  /**
   * call finish pressed in different pages
   *
   * @param currentPage IWizardPage
   */
  private void callFinishPressed(final IWizardPage currentPage) {
    if (currentPage instanceof SelectAttrValWizardPage) {
      ((SelectAttrValWizardPage) currentPage).nextPressed();
      this.wizard.setCopyRuleWithoutChange(true);
    }

  }

  /**
   * Call back pressed method
   *
   * @param currentPage current Page
   */
  private void callBackPressed(final IWizardPage currentPage) {
    // NA for FileSelectionWizardPage since there no back button for the first page

    if (currentPage instanceof ChooseRuleWizardPage) {
      ((ChooseRuleWizardPage) currentPage).backPressed();
    }
    else if (currentPage instanceof CreateEditRuleWizardPage) {
      getShell().pack();
      ((CreateEditRuleWizardPage) currentPage).backPressed();
    }
  }

  /**
   * Call next pressed method
   *
   * @param currentPage current Page
   */
  private void callNextPressed(final IWizardPage currentPage) {
    if (currentPage instanceof SelectAttrValWizardPage) {
      ((SelectAttrValWizardPage) currentPage).nextPressed();
    }
    else if (currentPage instanceof ChooseRuleWizardPage) {
      getShell().pack();
      ((ChooseRuleWizardPage) currentPage).nextPressed();
    }
  }

  /**
   * {@inheritDoc} ICDM-1244
   */
  @Override
  public boolean close() {
    ReviewRuleActionSet reviewParamActionSet = new ReviewRuleActionSet();
    reviewParamActionSet.refreshParamPropInOtherDialogs(this.wizard.getRuleInfoSection().getParamEditSection(),
        this.wizard.getCdrFunc(), this.wizard.getWizardData().getCdrParameter(), false,
        this.wizard.getRuleInfoSection());
    return super.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void cancelPressed() {
    ReviewRuleActionSet reviewParamActionSet = new ReviewRuleActionSet();
    reviewParamActionSet.refreshParamPropInOtherDialogs(this.wizard.getRuleInfoSection().getParamEditSection(),
        this.wizard.getCdrFunc(), this.wizard.getWizardData().getCdrParameter(), false,
        this.wizard.getRuleInfoSection());
    super.cancelPressed();
  }


}
