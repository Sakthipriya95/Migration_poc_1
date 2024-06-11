/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.nattable.exception;


/**
 * @author msp5cob
 */
public class NatTableRuntimeException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = -6301792951024339121L;

  /**
   * Constructs a new NatTableRuntimeException with the specified detail message
   *
   * @param message String
   */
  public NatTableRuntimeException(final String message) {
    super(message);
  }

  /**
   * Constructs a new NatTableRuntimeException with the specified detail message and cause
   *
   * @param message String
   * @param cause cause
   */
  public NatTableRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
