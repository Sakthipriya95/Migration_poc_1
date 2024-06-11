/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;

/**
 * @author adn1cob
 */
public final class CDMLogger extends AbstractLogger { // NOPMD by bne4cob on 12/10/13 4:17 PM

  /**
   * Text to identify whether input text is a plugin name
   */
  private static final String PLUGIN_NAME_IDENTIFIER = "com.bosch";

  /**
   * Property name for application mode
   */
  // NOTE : Though the same key is being used in APIC JPA, the constant definition is local, as this plugin is not a
  // dependant to the jpa plugin
  private static final String APP_MODE_PROP_NAME = "applicationMode";

  /**
   * Application mode type - Web service
   */
  // NOTE : Though the same key is being used in APIC JPA, the constant definition is local, as this plugin is not a
  // dependant to the jpa plugin
  private static final String APP_MODE_TYPE_WBSRVCE = "WebService";

  /**
   * CDMLogger instance
   */
  private static final CDMLogger INSTANCE = new CDMLogger();

  /**
   * Private constructor. Sets the eclipse preferences
   */
  private CDMLogger() {
    super();
    // Icdm-761 by default do not show the error log view
    if (!APP_MODE_TYPE_WBSRVCE.equals(System.getProperty(APP_MODE_PROP_NAME))) {
      final IEclipsePreferences pref = InstanceScope.INSTANCE.getNode("org.eclipse.ui.views.log");
      pref.putBoolean("activate", false);
    }
  }

  /**
   * Returns the singleton class instance.
   *
   * @return CDMLogger
   */
  public static CDMLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.ICDM_LOGGER);
  }

  /**
   * Method to log error - logs both in ERROR LOG view and in log file.
   *
   * @param error the message
   * @param pluginName the name of the plugin
   */
  public void error(final String error, final String pluginName) {
    // Log error message to log file
    error(error);
    showErrorView();
    // Log error message to error log view
    logMsgInLogView(error, pluginName, IStatus.ERROR);
  }

  /**
   * Method to show the error view only when the error is thrown from the application Icdm-761
   */

  private void showErrorView() {
    if (APP_MODE_TYPE_WBSRVCE.equals(System.getProperty(APP_MODE_PROP_NAME))) {
      // Error view is not applicable for web service
      return;
    }

    Display.getDefault().asyncExec(new Runnable() {

      /**
       * Activate the Error view asynchronously
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void run() {
        IWorkbenchWindow activeWrkbnchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if ((activeWrkbnchWindow != null) && (activeWrkbnchWindow.getActivePage() != null)) {
          try {

            activeWrkbnchWindow.getActivePage().showView("org.eclipse.pde.runtime.LogView");

          }
          catch (PartInitException e) {
            error("Error activating Log View", e);
          }

        }

      }
    });
  }

  /**
   * Method to log error - logs both in ERROR LOG view and in log file.
   *
   * @param error the message
   * @param cause the cause
   * @param pluginName the name of the plugin
   */
  public void error(final String error, final Throwable cause, final String pluginName) {
    // Log error message to log file
    error(error, cause);
    showErrorView();
    // Log error message to error log view
    logMsgInLogView(error, pluginName, IStatus.ERROR);
  }

  /**
   * Method to log fatal error - logs both in ERROR LOG view and in log file.
   *
   * @param fatal the message
   * @param pluginName the name of the plugin
   */
  public void fatal(final String fatal, final String pluginName) {
    // Log fatal message to log file
    fatal(fatal);
    showErrorView();
    // Log fatal message to error log view
    logMsgInLogView(fatal, pluginName, IStatus.ERROR);
  }

  /**
   * Method to log fatal error with exception - logs both in ERROR LOG view and in log file.
   *
   * @param fatal the message
   * @param cause the cause
   * @param pluginName the name of the plugin
   */
  public void fatal(final String fatal, final Throwable cause, final String pluginName) {
    // Log fatal message to log file
    fatal(fatal, cause);
    showErrorView();
    // Log fatal message to error log view
    logMsgInLogView(fatal, pluginName, IStatus.ERROR);
  }

  /**
   * Method to log INFO message - logs both in ERROR LOG view and in log file.
   *
   * @param info the message
   * @param pluginName the name of the plugin
   */
  public void info(final String info, final String pluginName) {
    // If second argument is plugin name, then send message to Log viewer. Otherwise handle the argument as a
    // placeholder value
    if ((pluginName != null) && (pluginName.indexOf(PLUGIN_NAME_IDENTIFIER) >= 0)) {
      // Log message to log file
      info(info);
      // Log info message to error log view
      logMsgInLogView(info, pluginName, IStatus.INFO);
    }
    else {
      // message with placeholder
      super.info(info, pluginName);
    }

  }

  /**
   * Method to log WARN message - logs both in ERROR LOG view and in log file.
   *
   * @param warning the message
   * @param pluginName the name of the plugin
   */
  public void warn(final String warning, final String pluginName) {
    // Log warning message to log file
    warn(warning);
    showErrorView();
    // Log warning message to error log view
    logMsgInLogView(warning, pluginName, IStatus.WARNING);
  }

  /**
   * Method to log WARN message with exception- logs both in ERROR LOG view and in log file
   *
   * @param warning the message
   * @param cause the cause
   * @param pluginName the name of the plugin
   */
  public void warn(final String warning, final Throwable cause, final String pluginName) {
    // Log warning message to log file
    warn(warning, cause);
    showErrorView();
    // Log warning message to error log view
    logMsgInLogView(warning, pluginName, IStatus.WARNING);
  }

  // iCDM-340
  /**
   * Shows the message in ERROR Dialog box and also logs in error logs
   *
   * @param message ,error message to be displayed
   * @param pluginName ,plugin name
   */
  public void errorDialog(final String message, final String pluginName) {
    // Call ERROR dialog
    errorDialog(message, null, pluginName);
  }

  /**
   * Shows the message in ERROR Dialog box and also logs in error logs
   *
   * @param message - error message to be displayed
   * @param cause , throwable cause
   * @param pluginName ,plugin name
   */
  public void errorDialog(final String message, final Throwable cause, final String pluginName) {
    // Show this message in ERROR dialog
    showMessageDialog(ILoggerAdapter.LEVEL_ERROR, message);


    // Also log the message to error log and log file
    if (cause == null) {
      error(message, pluginName);
    }
    else {
      error(message, cause, pluginName);
    }
  }

  /**
   * Shows the message in WARNING Dialog box and also logs in error logs
   *
   * @param message ,warning message to be displayed
   * @param pluginName ,plugin name
   */
  public void warnDialog(final String message, final String pluginName) {
    // Call WARNING dialog
    warnDialog(message, null, pluginName);
  }

  /**
   * Shows the message in WARNING Dialog box and also logs in error logs
   *
   * @param message warning message to be displayed
   * @param cause , throwable cause
   * @param pluginName ,plugin name
   */
  public void warnDialog(final String message, final Throwable cause, final String pluginName) {
    // Show this message in WARNING dialog
    showMessageDialog(ILoggerAdapter.LEVEL_WARN, message);

    // Also log the message to error log and log file
    if (cause == null) {
      warn(message, pluginName);
    }
    else {
      warn(message, cause, pluginName);
    }
  }

  /**
   * Shows the message in INFO Dialog box and also logs in error logs
   *
   * @param message info message to be displayed
   * @param pluginName ,plugin name
   */
  public void infoDialog(final String message, final String pluginName) {
    // Show this message in INFO dialog
    showMessageDialog(ILoggerAdapter.LEVEL_INFO, message);

    // Also log the message to error log and log file
    info(message, pluginName);
  }

  /**
   * Show message dialog. If the message is not invoked from the display thread, show the dialog in async mode.
   *
   * @param logLevel log level
   * @param message message
   */
  private void showMessageDialog(final int logLevel, final String message) {

    if (APP_MODE_TYPE_WBSRVCE.equals(System.getProperty(APP_MODE_PROP_NAME))) {
      // Message dialog is not applicable for web service
      return;
    }
    final Display display = Display.getCurrent();
    if (display == null) {
      // If display is null, show message in async mode
      Display.getDefault().asyncExec(new Runnable() { // NOPMD by bne4cob on 12/10/13 2:07 PM

        @Override
        public void run() {
          doShowMessageDialog(logLevel, message);
        }

      });

    }
    else {
      doShowMessageDialog(logLevel, message);
    }

  }

  /**
   * @param logLevel
   * @param message
   */
  private void doShowMessageDialog(final int logLevel, final String message) {
    switch (logLevel) {
      case ILoggerAdapter.LEVEL_FATAL:
      case ILoggerAdapter.LEVEL_ERROR:
        MessageDialog.openError(Display.getCurrent().getActiveShell(), ICDMLoggerConstants.DIALOG_TITLE_ERROR, message);
        break;
      case ILoggerAdapter.LEVEL_WARN:
        MessageDialog.openWarning(Display.getCurrent().getActiveShell(), ICDMLoggerConstants.DIALOG_TITLE_WARN,
            message);
        break;
      default:
        // Icdm 1101 - Default info level
        MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ICDMLoggerConstants.DIALOG_TITLE_INFO,
            message);
    }

  }

  /**
   * ICDM-1493 This method logs message in error log view
   *
   * @param info String
   * @param pluginName String
   * @param status int
   */
  private void logMsgInLogView(final String info, final String pluginName, final int status) {
    // Log view is not applicable non-plugin invocations(e.g. Junit tests)
    if (APP_MODE_TYPE_WBSRVCE.equals(System.getProperty(APP_MODE_PROP_NAME)) ||
        (CDMLoggerActivator.getDefault() == null)) {
      return;
    }

    final String pluginID = (pluginName == null) ? ICDMLoggerConstants.DEFAULT_PLUGIN_NAME_ERROR_LOG : pluginName;
    // Log info message to error log view
    Status logViewLogger = new Status(status, pluginID, info);
    CDMLoggerActivator.getDefault().getLog().log(logViewLogger);
  }
}
