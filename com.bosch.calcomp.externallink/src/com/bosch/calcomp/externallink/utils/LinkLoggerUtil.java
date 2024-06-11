/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.utils;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.externallink.ILoggingCustomization;

/**
 * Logger for this module
 *
 * @author bne4cob
 */
// ICDM-1649
public final class LinkLoggerUtil {

  /**
   * Logger
   */
  private static ILoggerAdapter logger;

  /**
   * Logging customization
   */
  private static ILoggingCustomization logCustomization;

  /**
   * private constructor for utility class
   */
  private LinkLoggerUtil() {
    // No implementation
  }

  /**
   * @return the logger
   */
  public static ILoggerAdapter getLogger() {
    return logger;
  }

  /**
   * @param logger the logger to set
   */
  public static void setLogger(final ILoggerAdapter logger) {
    LinkLoggerUtil.logger = logger;
  }

  /**
   * @param logCustomization ILoggingCustomization instance
   */
  public static void setLoggingCustomization(final ILoggingCustomization logCustomization) {
    LinkLoggerUtil.logCustomization = logCustomization;
  }

  /**
   * Shows error
   *
   * @param message message
   * @param exp exception stack, if present
   */
  public static void showError(final String message, final Exception exp) {
    if (logCustomization == null) {
      logger.error(message, exp);
    }
    else {
      logCustomization.showError(message, exp);
    }
  }

  /**
   * Shows error messages in a dialog
   *
   * @param message message
   * @param exp exception stack, if present
   */
  public static void showErrorDialog(final String message, final Exception exp) {
    if (logCustomization == null) {
      logger.error(message, exp);
    }
    else {
      logCustomization.showErrorDialog(message, exp);
    }
  }


  /**
   * Shows informational messages
   *
   * @param message message
   */
  public static void showInfo(final String message) {
    if (logCustomization == null) {
      logger.info(message);
    }
    else {
      logCustomization.showInfo(message);
    }
  }


  /**
   * Shows informational messages in a dialog
   *
   * @param message message
   */
  public static void showInfoDialog(final String message) {
    if (logCustomization == null) {
      logger.info(message);
    }
    else {
      logCustomization.showInfoDialog(message);
    }
  }


}
