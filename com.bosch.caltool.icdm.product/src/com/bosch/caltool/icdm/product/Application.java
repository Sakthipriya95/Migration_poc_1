/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

  /**
   * Command line argument value for toolbaseYN
   */
  private static final String TOOLBASE_VAL_Y = CommonUtilConstants.CODE_YES;
  /**
   * Command line argument for toolbase
   */
  private static final String CMD_ARG_TOOLBASE = "-toolbaseYN";
  /**
   * Info message during startup
   */
  private static final String INFO_MESSAGE = "Please start the application through Toolbase Client!";

  /**
   * Start the iCDM application.
   *
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
   */
  @Override
  public Object start(final IApplicationContext context) throws Exception {
    String[] args = Platform.getCommandLineArgs();

    // Log commandline arguments
    StringBuilder sbArgs = new StringBuilder("Command line arguments :");
    for (String curArg : args) {
      sbArgs.append(' ').append(curArg);
    }
    CDMLogger.getInstance().info(sbArgs.toString());

    // ICDM-361
    if (!isToolbaseStart(args)) {
      MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information", INFO_MESSAGE);
      return IApplication.EXIT_OK;
    }

    Display display = PlatformUI.createDisplay();
    try {
      int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
      if (returnCode == PlatformUI.RETURN_RESTART) {
        return IApplication.EXIT_RESTART;
      }
      return IApplication.EXIT_OK;
    }
    finally {
      display.dispose();
    }

  }

  /**
   * Checks from cmd line args if the tool started from toolbase
   *
   * @param args
   * @return
   */
  private boolean isToolbaseStart(final String[] args) {
    boolean toolBase = false;
    int index = 0;
    if (args != null) {
      while (index < args.length) {
        if (CMD_ARG_TOOLBASE.equals(args[index]) && ((index + 1) < args.length) &&
            TOOLBASE_VAL_Y.equals(args[index + 1])) {

          toolBase = true;
          break;
        }
        index++;
      }
    }
    return toolBase;
  }

  /**
   * @see org.eclipse.equinox.app.IApplication#stop()
   */
  @Override
  public void stop() {
    if (!PlatformUI.isWorkbenchRunning()) {
      return;
    }
    final IWorkbench workbench = PlatformUI.getWorkbench();
    final Display display = workbench.getDisplay();
    display.syncExec(() -> {
      if (!display.isDisposed()) {
        workbench.close();
      }
    });

  }


}
