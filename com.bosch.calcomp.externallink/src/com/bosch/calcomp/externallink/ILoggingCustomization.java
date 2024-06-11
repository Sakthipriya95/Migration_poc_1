/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;


/**
 * Provision for Logging customization if any
 *
 * @author bne4cob
 */
// ICDM-1649
public interface ILoggingCustomization {

  /**
   * Shows error
   *
   * @param message message
   * @param exp exception stack, if present
   */
  void showError(String message, Exception exp);

  /**
   * Shows error messages in a dialog
   *
   * @param message message
   * @param exp exception stack, if present
   */
  void showErrorDialog(String message, Exception exp);

  /**
   * Shows informational messages
   *
   * @param message message
   */
  void showInfo(String message);

  /**
   * Shows informational messages in a dialog
   *
   * @param message message
   */
  void showInfoDialog(String message);
}
