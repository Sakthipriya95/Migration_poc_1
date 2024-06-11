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
 * ICDM-2362
 *
 * @author mkl2cob
 */
public class PIDCCreationWizardDialog extends AbstractWizardDialog {

  /**
   *
   */
  private static final int DIALOG_HEIGHT = 600;
  /**
   *
   */
  private static final int DIALOG_WIDTH = 700;

  /**
   * @param parentShell Shell
   * @param newWizard IWizard
   */
  public PIDCCreationWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set the wizard size
    int frameX = newShell.getSize().x - newShell.getClientArea().width;
    int frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.setSize(DIALOG_WIDTH + frameX, DIALOG_HEIGHT + frameY);
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    final IWizardPage currentPage = getCurrentPage();
    // call next pressed
    if (buttonId == IDialogConstants.NEXT_ID) {
      callNextPressed(currentPage);

    }
    super.buttonPressed(buttonId);
  }

  /**
   * @param currentPage
   */
  private void callNextPressed(final IWizardPage currentPage) {
    // PIDCDetailsWizardPage next press
    if (currentPage instanceof PIDCDetailsWizardPage) {
      ((PIDCDetailsWizardPage) currentPage).nextPressed();
    }
    // VersionsDetailsWizardPage next press
    if (currentPage instanceof VersionsDetailsWizardPage) {
      ((VersionsDetailsWizardPage) currentPage).nextPressed();
    }

  }
}
