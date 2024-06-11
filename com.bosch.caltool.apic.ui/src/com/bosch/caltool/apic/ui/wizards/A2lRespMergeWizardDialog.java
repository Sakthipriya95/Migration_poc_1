/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;

/**
 * @author dmr1cob
 */
public class A2lRespMergeWizardDialog extends AbstractWizardDialog {

  private final boolean isAdminPage;

  /**
   * dialog height
   */
  private static final int DIALOG_HEIGHT = 900;
  /**
   * dialog width
   */
  private static final int DIALOG_WIDTH = 900;

  /**
   * @param parentShell shell
   * @param newWizard wizard
   * @param isAdminPage
   */
  public A2lRespMergeWizardDialog(final Shell parentShell, final IWizard newWizard, final boolean isAdminPage) {
    super(parentShell, newWizard);
    this.isAdminPage = isAdminPage;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    super.createButtonsForButtonBar(parent);

    String finishText = this.isAdminPage ? "Merge" : "Send mail";
    getButton(IDialogConstants.FINISH_ID).setText(finishText);
    getButton(IDialogConstants.FINISH_ID).setEnabled(false);
    if (!this.isAdminPage) {
      getButton(IDialogConstants.NEXT_ID).setEnabled(false);
      getButton(IDialogConstants.BACK_ID).setVisible(false);
    }
  }

  /**
   * Actions to be done on button click {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    final IWizardPage currentPage = getCurrentPage();
    // call next pressed method
    if (buttonId == IDialogConstants.NEXT_ID) {
      callNextPressed(currentPage);

    }
    // call back pressed
    else if (buttonId == IDialogConstants.BACK_ID) {
      callBackPressed(currentPage);
    }

    super.buttonPressed(buttonId);
  }

  /**
   * @param currentPage page
   */
  private void callNextPressed(final IWizardPage currentPage) {
    if ((currentPage instanceof QnaireRespSelectionWizardPage)) {
      ((A2lRespMergeWizard) getWizard()).getQnaireRespStatisticsWizardPage().getRvwQnaireRespStatisticsTabViewer()
          .setInput(((A2lRespMergeWizard) getWizard()).getRetainedQnaireRespDetailsSet());
      // Disable next button and enable finish button
      getButton(IDialogConstants.BACK_ID).setVisible(true);
      getButton(IDialogConstants.NEXT_ID).setEnabled(false);
      getButton(IDialogConstants.FINISH_ID).setVisible(true);
      getButton(IDialogConstants.FINISH_ID).setEnabled(true);
    }
  }

  private void callBackPressed(final IWizardPage currentPage) {
    if (currentPage instanceof QnaireRespStatisticsWizardPage) {
      getButton(IDialogConstants.BACK_ID).setVisible(false);
      // Disable Finish button
      getButton(IDialogConstants.FINISH_ID).setEnabled(false);
      getButton(IDialogConstants.NEXT_ID).setVisible(true);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set the wizard size
    // frame size calculated to adapt >100% text size in windows 10
    int frameX = newShell.getSize().x - newShell.getClientArea().width;
    int frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.setSize(DIALOG_WIDTH + frameX, DIALOG_HEIGHT + frameY);
    // set configure shell
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

}
