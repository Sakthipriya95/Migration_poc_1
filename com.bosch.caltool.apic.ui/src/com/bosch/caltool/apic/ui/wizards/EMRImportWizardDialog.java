/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;

/**
 * This class is for the wizard dialog to import CODEX measurement program
 *
 * @author mkl2cob
 */
public class EMRImportWizardDialog extends AbstractWizardDialog {

  /**
   * @param parentShell Shell
   * @param newWizard IWizard
   */
  public EMRImportWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
  }


  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    super.createButtonsForButtonBar(parent);
    Button nextButton = getButton(IDialogConstants.NEXT_ID);
    // Set name of button as upload
    nextButton.setText("Upload");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    final IWizardPage currentPage = getCurrentPage();
    // Call next pressed on button press
    if (buttonId == IDialogConstants.NEXT_ID) {
      callNextPressed(currentPage);

    }
    super.buttonPressed(buttonId);
  }

  /**
   * @param currentPage
   */
  private void callNextPressed(final IWizardPage currentPage) {
    if (currentPage instanceof EMRFileImportWizardPage) {
      EMRFileImportWizardPage fileImprtPage = (EMRFileImportWizardPage) currentPage;
      fileImprtPage.nextPressed();
      // On upload success getNext button
      if (fileImprtPage.uploadSuccess()) {
        // change the text from 'Upload' to 'Next >'
        getButton(IDialogConstants.NEXT_ID).setText("Next >");

      }
    }

    if (currentPage instanceof EMRResultWizardPage) {
      EMRResultWizardPage resultPage = (EMRResultWizardPage) currentPage;
      // get the next page of EMRResultWizardPage
      if (resultPage.getNextPage() instanceof EMRVariantSelectionWizardPage) {
        EMRVariantSelectionWizardPage varPage = (EMRVariantSelectionWizardPage) resultPage.getNextPage();
        varPage.setPageComplete(true);
      }
    }

  }

}
