/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.AbstractWizardDialog;


/**
 * @author mkl2cob
 */
public class CdrReportGenerationWizardDialog extends AbstractWizardDialog {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    super.createButtonsForButtonBar(parent);
    // set the text for finish as 'Generate'
    getButton(IDialogConstants.FINISH_ID).setText("Generate");
  }

  /**
   * @param parentShell Shell
   * @param wizard GenerateRvwReportWizard
   */
  public CdrReportGenerationWizardDialog(final Shell parentShell, final CdrReportGenerationWizard wizard) {
    super(parentShell, wizard);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE);
    setBlockOnOpen(false);
  }


}
