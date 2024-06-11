/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.exception;

/**
 * @author bne4cob
 */
public class CnsServiceException extends Exception {

  /**
   * Serial Version ID
   */
  private static final long serialVersionUID = 1L;

  /**
   * Error code
   */
  private final String errorCode;

  /**
   * @param errorCode error code.
   * @param message message
   * @param cause cause
   */
  public CnsServiceException(final String errorCode, final String message, final Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /**
   * @param errorCode error code.
   * @param message message
   */
  public CnsServiceException(final String errorCode, final String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * @return the error Code.
   */
  public String getErrorCode() {
    return this.errorCode;
  }
}
