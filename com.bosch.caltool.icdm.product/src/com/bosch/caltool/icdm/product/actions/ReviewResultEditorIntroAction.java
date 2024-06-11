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

import com.bosch.caltool.cdr.ui.dialogs.ReviewResultDialog;


/**
 * Icdm-567
 * 
 * @author bru2cob
 */
public class ReviewResultEditorIntroAction implements IIntroAction {

  /**
   * opening review param editor
   */
  @Override
  public void run(final IIntroSite site, final Properties params) {
    final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    // Review result dialog
    final ReviewResultDialog dialog = new ReviewResultDialog(shell);
    dialog.open();
    // If cancel pressed, keep the into page open
    if (dialog.getReturnCode() == Window.CANCEL) {
      PlatformUI.getWorkbench().getIntroManager().getIntro();
    }
    else {
      // Get the intro page
      final IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
      // Close the intro page
      PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
    }
  }

}
