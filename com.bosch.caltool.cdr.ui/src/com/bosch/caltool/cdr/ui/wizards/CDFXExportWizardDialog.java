/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.cdr.ui.wizards.pages.CDFXExportInputWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;

/**
 * @author say8cob
 */
public class CDFXExportWizardDialog extends AbstractWizardDialog {


  /**
   * @param parentShell as input
   * @param newWizard as input
   */
  public CDFXExportWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    super.createButtonsForButtonBar(parent);
    // set the text for next as 'Export'
    getButton(IDialogConstants.NEXT_ID).setText("Export");
    getButton(IDialogConstants.FINISH_ID).setEnabled(false);
    getButton(IDialogConstants.BACK_ID).setVisible(false);
    getButton(IDialogConstants.CANCEL_ID).moveAbove(getButton(IDialogConstants.FINISH_ID));
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
    super.buttonPressed(buttonId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set minimum page size
    setMinimumPageSize(400, 550);
    // set configure shell
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * @param currentPage
   */
  private void callNextPressed(final IWizardPage currentPage) {
    if (currentPage instanceof CDFXExportInputWizardPage) {
      ((CDFXExportInputWizardPage) currentPage).exportPressed();
      if (currentPage.isPageComplete()) {
        getButton(IDialogConstants.NEXT_ID).setVisible(false);
        getButton(IDialogConstants.CANCEL_ID).setEnabled(false);
        getButton(IDialogConstants.FINISH_ID).setVisible(true);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
    setBlockOnOpen(false);
  }

}
