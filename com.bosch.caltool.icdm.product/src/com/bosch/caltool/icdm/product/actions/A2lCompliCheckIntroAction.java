/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Properties;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.bosch.caltool.icdm.ui.dialogs.A2LCompliParamCheckDialog;

/**
 * Class for Compliance Check in an a2l file 
 * 
 * @author svj7cob
 */
//Task 264144
public class A2lCompliCheckIntroAction implements IIntroAction {

  /** 
   * Opens the a2l file upload dialog
   */
  @Override
  public void run(IIntroSite arg0, Properties arg1) {
      Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
      // Create CDR dialog
      A2LCompliParamCheckDialog calDataRvwWizardDialog = new A2LCompliParamCheckDialog(shell);
      calDataRvwWizardDialog.create();
      calDataRvwWizardDialog.open();
  }

}
