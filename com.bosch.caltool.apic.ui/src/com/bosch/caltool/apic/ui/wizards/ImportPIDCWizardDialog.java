/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;


/**
 * @author jvi6cob
 */
public class ImportPIDCWizardDialog extends AbstractWizardDialog {

  /**
   * @param parentShell
   * @param newWizard
   */
  public ImportPIDCWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    // Next Button press
    if (buttonId == IDialogConstants.NEXT_ID) {
      IWizardPage currentPage = getCurrentPage();
      // Check for current page ImportExcelWizardPage
      if (currentPage instanceof ImportExcelWizardPage) {
        ImportExcelWizardPage importExcelWizardPage = (ImportExcelWizardPage) currentPage;
        importExcelWizardPage.nextPressed();
      }
    }
    super.buttonPressed(buttonId);
  }


}
