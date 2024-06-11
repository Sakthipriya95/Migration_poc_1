/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.pwdservice.exception;


/**
 * @author TUD1COB
 */
public class PasswordServiceException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 7685636271865731598L;


  /**
   * @param message message
   */
  public PasswordServiceException(final String message) {
    super(message);
  }


  /**
   * @param message message
   * @param cause cause
   */
  public PasswordServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }


}
