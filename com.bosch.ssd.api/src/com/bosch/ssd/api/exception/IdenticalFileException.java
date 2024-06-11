/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.exception;


/**
 * @author VAU3COB
 */
public class IdenticalFileException extends SSDAPIException {


  /**
   * Serial ID
   */
  private static final long serialVersionUID = 968975440778408450L;

  /**
   * @param message error message
   */
  public IdenticalFileException(final String message) {
    super(message);
  }

  /**
   * @param message error message
   * @param exp cause
   */
  public IdenticalFileException(final String message, final Throwable exp) {
    super(message, exp);
  }


}
