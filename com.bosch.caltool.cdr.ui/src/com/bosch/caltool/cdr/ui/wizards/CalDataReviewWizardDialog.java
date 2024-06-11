/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.cdr.ui.wizards.pages.FileSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.ProjectDataSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewCalDataWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.ReviewFilesSelectionWizardPage;
import com.bosch.caltool.cdr.ui.wizards.pages.RuleSetSltnPage;
import com.bosch.caltool.cdr.ui.wizards.pages.SSDRuleSelectionPage;
import com.bosch.caltool.cdr.ui.wizards.pages.WorkpackageSelectionWizardPage;
import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;


/**
 * Review wizard dialog
 *
 * @author adn1cob
 */
public class CalDataReviewWizardDialog extends AbstractWizardDialog {

  /**
   * dialog height
   */
  private static final int DIALOG_HEIGHT = 900;
  /**
   * dialog width
   */
  private static final int DIALOG_WIDTH = 750;

  /**
   * @param parentShell parent shell
   * @param newWizard wizard object
   */
  public CalDataReviewWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
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

  /**
   * Call back pressed method
   *
   * @param currentPage current Page
   */
  private void callBackPressed(final IWizardPage currentPage) {
    // NA for FileSelectionWizardPage since there no back button for the first page

    // Applicable since this page can be first or second page
    if (currentPage instanceof ProjectDataSelectionWizardPage) {
      ((ProjectDataSelectionWizardPage) currentPage).backPressed();
    }
    else if (currentPage instanceof WorkpackageSelectionWizardPage) {
      ((WorkpackageSelectionWizardPage) currentPage).backPressed();
    }
    else if (currentPage instanceof ReviewFilesSelectionWizardPage) {
      ((ReviewFilesSelectionWizardPage) currentPage).backPressed();
    }
    else if (currentPage instanceof ReviewCalDataWizardPage) {
      ((ReviewCalDataWizardPage) currentPage).backPressed();
    }
    else if (currentPage instanceof RuleSetSltnPage) {
      ((RuleSetSltnPage) currentPage).backPressed();
    }
  }

  /**
   * Call next pressed method
   *
   * @param currentPage current Page
   */
  private void callNextPressed(final IWizardPage currentPage) {
    if (currentPage instanceof FileSelectionWizardPage) {
      ((FileSelectionWizardPage) currentPage).nextPressed();
    }
    else if (currentPage instanceof ProjectDataSelectionWizardPage) {
      ((ProjectDataSelectionWizardPage) currentPage).nextPressed();
    }

    else if (currentPage instanceof RuleSetSltnPage) {
      ((RuleSetSltnPage) currentPage).nextPressed();
    }

    else if (currentPage instanceof SSDRuleSelectionPage) {
      ((SSDRuleSelectionPage) currentPage).nextPressed();
    }
    else if (currentPage instanceof WorkpackageSelectionWizardPage) {
      ((WorkpackageSelectionWizardPage) currentPage).nextPressed();
    }
    else if (currentPage instanceof ReviewFilesSelectionWizardPage) {
      ((ReviewFilesSelectionWizardPage) currentPage).nextPressed();
    }
  }
}
