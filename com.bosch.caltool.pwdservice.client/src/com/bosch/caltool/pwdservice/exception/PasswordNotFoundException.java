/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.pwdservice.exception;


/**
 * @author vau3cob
 */
public class PasswordNotFoundException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 9167730671555809828L;


  /**
   * @param message message
   */
  public PasswordNotFoundException(final String message) {
    super(message);
  }


  /**
   * @param message message
   * @param cause cause
   */
  public PasswordNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }


}
