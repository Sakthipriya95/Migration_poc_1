/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Properties;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.bosch.caltool.cdr.ui.dialogs.CompliReviewDialog;

/**
 * Class for Compliance Check in an a2l file
 *
 * @author svj7cob
 */
public class CompliReviewIntroAction implements IIntroAction {

  /**
   * Opens the HEX file upload dialog
   */
  @Override
  public void run(final IIntroSite arg0, final Properties arg1) {
    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    CompliReviewDialog calDataRvwWizardDialog = new CompliReviewDialog(shell);
    // create and open compli review dialog
    calDataRvwWizardDialog.create();
    calDataRvwWizardDialog.open();
  }

}
