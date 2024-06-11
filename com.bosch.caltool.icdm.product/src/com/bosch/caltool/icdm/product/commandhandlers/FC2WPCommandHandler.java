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

import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizard;
import com.bosch.caltool.icdm.ui.wizards.FC2WPAssignmentWizardDialog;

/**
 * @author bru2cob
 */
public class FC2WPCommandHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    IWorkbenchWindow activeWindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    Shell parent = activeWindow.getShell();
    // Create new fc2wp wizard
    FC2WPAssignmentWizard wizard = new FC2WPAssignmentWizard();
    // Initialise fc2wp wizard dialog
    FC2WPAssignmentWizardDialog fc2wpWizardDialog = new FC2WPAssignmentWizardDialog(parent, wizard);
    // Create fc2wp wizard dialog
    fc2wpWizardDialog.create();
    // Open fc2wp wizard dialog
    fc2wpWizardDialog.open();

    return null;
  }

}
