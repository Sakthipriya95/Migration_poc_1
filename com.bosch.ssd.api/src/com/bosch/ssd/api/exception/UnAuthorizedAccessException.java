/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.exception;

/**
 * @author bne4cob
 */
public class UnAuthorizedAccessException extends SSDAPIException {

  /**
   * Serial ID
   */
  private static final long serialVersionUID = 968975440778408450L;

  /**
   * @param message error message
   */
  public UnAuthorizedAccessException(final String message) {
    super(message);
  }

  /**
   * @param message error message
   * @param exp cause
   */
  public UnAuthorizedAccessException(final String message, final Throwable exp) {
    super(message, exp);
  }

}
