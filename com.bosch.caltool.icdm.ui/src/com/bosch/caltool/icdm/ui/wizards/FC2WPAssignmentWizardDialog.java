/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;
import com.bosch.caltool.icdm.ui.wizards.pages.FC2WPCreationWizPage;
import com.bosch.caltool.icdm.ui.wizards.pages.FC2WPSelectionWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.FC2WPWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.FCWorkPackageCreationWizPage;

/**
 * @author bru2cob
 */
public class FC2WPAssignmentWizardDialog extends AbstractWizardDialog {

  /**
   * @param parentShell
   * @param newWizard
   */
  public FC2WPAssignmentWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
    // TODO Auto-generated constructor stub
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
    super.buttonPressed(buttonId);
  }

  /**
   * @param currentPage
   */
  private void callBackPressed(final IWizardPage currentPage) {
    if (currentPage instanceof FCWorkPackageCreationWizPage) {
      ((FCWorkPackageCreationWizPage) currentPage).backPressed();
    }
    else if (currentPage instanceof FC2WPCreationWizPage) {
      ((FC2WPCreationWizPage) currentPage).backPressed();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE);
    setBlockOnOpen(false);
  }

  /**
   * Call next pressed method
   *
   * @param currentPage current Page
   */
  private void callNextPressed(final IWizardPage currentPage) {
    if (currentPage instanceof FC2WPWizardPage) {
      ((FC2WPWizardPage) currentPage).nextPressed();
    }
    else if (currentPage instanceof FC2WPSelectionWizardPage) {
      ((FC2WPSelectionWizardPage) currentPage).nextPressed();
    }
    else if (currentPage instanceof FCWorkPackageCreationWizPage) {
      ((FCWorkPackageCreationWizPage) currentPage).nextPressed();
    }
  }

}
