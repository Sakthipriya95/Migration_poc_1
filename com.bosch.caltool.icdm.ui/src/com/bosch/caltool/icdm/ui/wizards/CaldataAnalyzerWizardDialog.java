/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.client.bo.cda.CDAFilterValidationBo;
import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerFunctionFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerParamFilterWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.CaldataAnalyzerSysConFilterWizardPage;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author pdh2cob
 */
public class CaldataAnalyzerWizardDialog extends AbstractWizardDialog {

  
  private static final String PLEASE_REMOVE_THEM = "Please remove them.";
  /**
   * Minimum size to which the dialog can be resized
   */
  private static final int DIALOG_MIN_WIDTH = 650;
  /**
   * Minimum size to which the dialog can be resized
   */
  private static final int DIALOG_MIN_HEIGHT = 690;
  /**
   * dialog height
   */
  private static final int DIALOG_HEIGHT = 700;
  /**
   * dialog width
   */
  private static final int DIALOG_WIDTH = 700;

  private final CDAFilterValidationBo validationBo;

  /**
   * @param parentShell
   * @param newWizard
   */
  public CaldataAnalyzerWizardDialog(final Shell parentShell, final IWizard newWizard) {
    super(parentShell, newWizard);
    this.validationBo = new CDAFilterValidationBo();
  }

  /**
   * Actions to be done on button click {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    boolean canGoToNextPage = true;
    final IWizardPage currentPage = getCurPage();
    // call next pressed method
    if (buttonId == IDialogConstants.NEXT_ID) {
      canGoToNextPage = callNextPressed(currentPage);

    }
    // call back pressed
    else if (buttonId == IDialogConstants.BACK_ID) {
      canGoToNextPage = callBackPressed();
    }
    if (canGoToNextPage) {
      super.buttonPressed(buttonId);
    }

  }

  /**
   * Call back pressed method
   */
  private boolean callBackPressed() {
    return true;
  }

  /**
   * Call next pressed method
   *
   * @param currentPage current Page
   */
  private boolean callNextPressed(final IWizardPage currentPage) {
    boolean success = true;
    StringBuilder errorMsg = new StringBuilder();
    if (currentPage instanceof CaldataAnalyzerParamFilterWizardPage) {
      try {
        if (!((CaldataAnalyzerParamFilterWizardPage) currentPage).getLabels().isEmpty()) {
          List<String> invalidList =
              this.validationBo.validateParameters(((CaldataAnalyzerParamFilterWizardPage) currentPage).getLabels());
          if (invalidList.size() > 0) {
            errorMsg.append("The following parameters are not valid :\n");
            for (String parameterFilterLabel : invalidList) {
              errorMsg.append(parameterFilterLabel + "\n");
            }
            errorMsg.append(PLEASE_REMOVE_THEM);
            success = false;
            MessageBox messageBox =
                new MessageBox(((CaldataAnalyzerParamFilterWizardPage) currentPage).getShell(), SWT.ICON_ERROR);
            messageBox.setMessage(errorMsg.toString());
            messageBox.open();
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    if (currentPage instanceof CaldataAnalyzerFunctionFilterWizardPage) {
      try {
        if (!((CaldataAnalyzerFunctionFilterWizardPage) currentPage).getFunctions().isEmpty()) {
          List<String> invalidList = this.validationBo
              .validateFunctions(((CaldataAnalyzerFunctionFilterWizardPage) currentPage).getFunctions());
          if (invalidList.size() > 0) {
            errorMsg.append("The following functions are not valid :\n");
            for (String functionFilter : invalidList) {
              errorMsg.append(functionFilter + "\n");
            }
            errorMsg.append(PLEASE_REMOVE_THEM);
            success = false;
            MessageBox messageBox =
                new MessageBox(((CaldataAnalyzerFunctionFilterWizardPage) currentPage).getShell(), SWT.ICON_ERROR);
            messageBox.setMessage(errorMsg.toString());
            messageBox.open();
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }

    if (currentPage instanceof CaldataAnalyzerSysConFilterWizardPage) {
      try {
        if (!((CaldataAnalyzerSysConFilterWizardPage) currentPage).getSystemConstants().isEmpty()) {
          List<SystemConstantFilter> duplicates = new ArrayList<SystemConstantFilter>();
          Set<String> sysconNameValue = new HashSet<>();
          for (SystemConstantFilter sysconFilter : ((CaldataAnalyzerSysConFilterWizardPage) currentPage)
              .getSystemConstants()) {
            if (!sysconNameValue.add(sysconFilter.getSystemConstantName() + sysconFilter.getSystemConstantValue())) {
              duplicates.add(sysconFilter);
            }
          }
          if (duplicates.size() > 0) {
            success = false;

            StringBuilder errorMsg1 = new StringBuilder();
            errorMsg1.append("The following system constant(s) has duplicates:\n");
            for (SystemConstantFilter sysconFilter : duplicates) {
              errorMsg1.append("System constant: " + sysconFilter.getSystemConstantName() + ", Value:" +
                  sysconFilter.getSystemConstantValue() + "\n");
            }
            errorMsg1.append(PLEASE_REMOVE_THEM);
            MessageBox messageBox =
                new MessageBox(((CaldataAnalyzerSysConFilterWizardPage) currentPage).getShell(), SWT.ICON_ERROR);
            messageBox.setMessage(errorMsg1.toString());
            messageBox.open();

          }
          else {
            List<String> invalidList = this.validationBo
                .validateSystemConstants(((CaldataAnalyzerSysConFilterWizardPage) currentPage).getSystemConstants());
            if (invalidList.size() > 0) {
              errorMsg.append("The following system constants are not valid :\n");
              for (String sysconFilter : invalidList) {
                errorMsg.append(sysconFilter + "\n");
              }
              errorMsg.append(PLEASE_REMOVE_THEM);
              success = false;
              MessageBox messageBox =
                  new MessageBox(((CaldataAnalyzerSysConFilterWizardPage) currentPage).getShell(), SWT.ICON_ERROR);
              messageBox.setMessage(errorMsg.toString());
              messageBox.open();
            }
          }
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }


    return success;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // set the wizard size
    newShell.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    // Task-238467
    newShell.setMinimumSize(DIALOG_MIN_WIDTH, DIALOG_MIN_HEIGHT);
    // set configure shell
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * @return Current Page
   */
  public IWizardPage getCurPage() {
    return super.getCurrentPage();
  }

}
