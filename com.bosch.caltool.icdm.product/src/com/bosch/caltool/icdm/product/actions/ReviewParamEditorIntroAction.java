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

import com.bosch.caltool.cdr.ui.dialogs.FunctionSelectionDialog;


/**
 * @author bru2cob
 */
public class ReviewParamEditorIntroAction implements IIntroAction {

  /**
   * opening review param editor
   */
  @Override
  public void run(final IIntroSite site, final Properties params) {
    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    // Function selection Dialog
    FunctionSelectionDialog dialog = new FunctionSelectionDialog(shell, null, null);
    dialog.open();
    // If cancel pressed
    if (dialog.getReturnCode() == Window.CANCEL) {
      PlatformUI.getWorkbench().getIntroManager().getIntro();
    }
    else {
      // Get the intro/welcome page
      final IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
      // Close the intro/welcome page
      PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
    }
  }

}
