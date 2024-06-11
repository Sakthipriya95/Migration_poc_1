/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Properties;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;

import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizard;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizardDialog;


/**
 * @author bru2cob
 */
public class ManageFC2WPIntroAction extends AbstractPerspectiveIntroAction {

  /**
   * opening manage fc2wp wizard
   */
  @Override
  public void run(final IIntroSite site, final Properties params) {

    IWorkbench workbench = PlatformUI.getWorkbench();
    Shell parent = workbench.getActiveWorkbenchWindow().getShell();
    // Create new fc2wp wizard
    FC2WPAssignmentWizard wizard = new FC2WPAssignmentWizard();
    // Create fc2wp wizard dialog
    // Task 236672
    FC2WPAssignmentWizardDialog fc2wpWizardDialog = new FC2WPAssignmentWizardDialog(parent, wizard);
    fc2wpWizardDialog.create();
    fc2wpWizardDialog.open();
    // If cancel pressed, keep the into page open
    IIntroManager introManager = workbench.getIntroManager();
    if (fc2wpWizardDialog.getReturnCode() == Window.CANCEL) {
      introManager.getIntro();
    }
    else {
      // Get the intro page
      final IIntroPart introPart = introManager.getIntro();
      // Close the intro page
      introManager.closeIntro(introPart);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPerspectiveId() {
    // pidc perspective
    return ICDMConstants.ID_PERSP_PIDC;
  }

}