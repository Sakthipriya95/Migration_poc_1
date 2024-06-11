/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.ui.actions.IImportRefresher;
import com.bosch.caltool.icdm.common.ui.wizards.pages.AttrValueImpWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.CalDataFileImpWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.CompareRuleImpWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.pages.SummaryImpWizardPage;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * Caldata file import wizard dialog ICDM-1293
 *
 * @author bru2cob
 */
public class CalDataFileImpWizardDialog extends AbstractWizardDialog {

  /**
   * Set the height of the wizard
   */
  private static final int WIZARD_HEIGHT = 870;
  /**
   * Set the width of the wizard
   */
  private static final int WIZARD_WIDTH = 800;


  /**
   * @param parentShell parent shell
   * @param newWizard wizard instance
   */
  public CalDataFileImpWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set the wizard size
    newShell.setSize(WIZARD_WIDTH, WIZARD_HEIGHT);
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
      // if it is next button
      callNextPressed(currentPage);
    }
    super.buttonPressed(buttonId);
  }


  /**
   * Call next pressed method
   *
   * @param currentPage current Page
   */
  private void callNextPressed(final IWizardPage currentPage) {
    if (currentPage instanceof CalDataFileImpWizardPage) {
      ((CalDataFileImpWizardPage) currentPage).nextPressed();
    }
    if (currentPage instanceof AttrValueImpWizardPage) {
      ((AttrValueImpWizardPage) currentPage).nextPressed();
    }
    if (currentPage instanceof CompareRuleImpWizardPage) {
      ((CompareRuleImpWizardPage) currentPage).nextPressed();
    }
    // set name as close for cancel button in summary page
    if (currentPage.getNextPage() instanceof SummaryImpWizardPage) {
      getButton(IDialogConstants.CANCEL_ID).setText("Close");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean close() {
    if (!(getCurrentPage() instanceof SummaryImpWizardPage)) {
      // if it is cancel or close button
      boolean confirm = MessageDialogUtils.getConfirmMessageDialogWithYesNo("Close Rule Importer",
          "Do you really want to close the Rule Importer? All changes will be discarded.");
      if (confirm) {
        // if there is no confirmation from the user, do not close the wizard
        return super.close();
      }
      return false;
    }
    // call refresh when wizard is closed
    refresh();
    return super.close();
  }

  /**
   * Refresh the corresponding active editor's
   */
  private void refresh() {
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
      IEditorReference[] editorReferences =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
      for (IEditorReference iEditorReference : editorReferences) {
        IEditorPart editorPart = iEditorReference.getEditor(false);
        if (!(editorPart instanceof IImportRefresher)) {
          continue;
        }
        // refresh if editor is intance of iimportrefresher
        IImportRefresher editor = (IImportRefresher) editorPart;
        editor.doImportRefresh();
      }
    }
  }

}
