/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * @author bne4cob
 */
public class StaleDataException extends DataException {

  /**
   * Serial ID
   */
  private static final long serialVersionUID = 968975440778408450L;

  /**
   * Operation that resulted in stale data exception
   */
  private final String operation;

  /**
   * @param operation - CREATE/UPDATE/DELETE
   * @param message error message
   */
  public StaleDataException(final String operation, final String message) {
    super(message);
    this.operation = operation;
  }

  /**
   * @param action - CREATE/UPDATE/DELETE
   * @param message error message
   * @param exp cause
   */
  public StaleDataException(final String action, final String message, final Throwable exp) {
    super(message, exp);
    this.operation = action;
  }

  /**
   * @return the action
   */
  public String getOperation() {
    return this.operation;
  }

}
