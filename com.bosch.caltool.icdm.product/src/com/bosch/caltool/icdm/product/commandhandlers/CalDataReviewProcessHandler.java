/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizardDialog;


/**
 * @author adn1cob
 */
public class CalDataReviewProcessHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    IWorkbenchWindow activeWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    Shell parent = activeWindow.getShell();
    // Create new CalData Review wizard
    CalDataReviewWizard wizard = new CalDataReviewWizard(false, false, null);
    // Create CalData Review wizard dialog
    CalDataReviewWizardDialog calDataReviewWizardDialog = new CalDataReviewWizardDialog(parent, wizard);
    // create the caldata review dialog
    calDataReviewWizardDialog.create();
    // Open CalData Review wizard dialog
    calDataReviewWizardDialog.open();

    return null;
  }

}
