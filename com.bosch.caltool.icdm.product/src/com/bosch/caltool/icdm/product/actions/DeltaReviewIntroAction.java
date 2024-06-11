/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.actions;

import java.util.Properties;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizardDialog;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;


/**
 * @author bru2cob
 */
public class DeltaReviewIntroAction implements IIntroAction {

  /**
   * opening delta review process wizard
   */
  @Override
  public void run(final IIntroSite site, final Properties params) {
    // ICDM-1111
    // Service handler for Vcdm interface
    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    // delta review will not be started with a a2l file, hence first param to be false
    // Set Review type as Delta Review
    CalDataReviewWizard wizard = new CalDataReviewWizard(false, true, DELTA_REVIEW_TYPE.DELTA_REVIEW);
    // Create CDR dialog
    CalDataReviewWizardDialog calDataReviewWizardDialog = new CalDataReviewWizardDialog(shell, wizard);
    calDataReviewWizardDialog.create();
    calDataReviewWizardDialog.open();
    // If cancel pressed
    if (calDataReviewWizardDialog.getReturnCode() == Window.CANCEL) {
      PlatformUI.getWorkbench().getIntroManager().getIntro();
    }
    else {
      // Get the intro part from intro manager
      final IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
      // Close the intro part of delta review
      PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
    }
  }

}