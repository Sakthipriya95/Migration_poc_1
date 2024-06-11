/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.commandhandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;

import com.bosch.caltool.cdr.ui.dialogs.ReviewResultDialog;


/**
 * Handler class for ReviewParamEditor open icon
 *
 * @author mkl2cob
 */
public class ReviewResultEditorHandler extends AbstractHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {

    final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    // Open the review result editor dialog
    final ReviewResultDialog dialog = new ReviewResultDialog(shell);
    dialog.open();
    // If cancel pressed, show the intro page
    if (dialog.getReturnCode() == Window.CANCEL) {
      PlatformUI.getWorkbench().getIntroManager().getIntro();
    }
    else {
      // Get the intro page
      final IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
      // close the intro/welcome page
      PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
    }
    return null;
  }

}
