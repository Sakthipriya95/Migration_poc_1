/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.exception;


/**
 * @author bru2cob
 */
public class CDRException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * CDRException
   */
  public CDRException() {
    super();
  }

  /**
   * Constructs a new CDRException with the specified detail message and cause
   * 
   * @param message String
   * @param cause Throwable
   */
  public CDRException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new CDRException with the specified detail message
   * 
   * @param message String
   */
  public CDRException(final String message) {
    super(message);
  }

  /**
   * Constructs a new CDRException with the specified cause
   * 
   * @param cause Throwable
   */
  public CDRException(final Throwable cause) {
    super(cause);
  }

}
