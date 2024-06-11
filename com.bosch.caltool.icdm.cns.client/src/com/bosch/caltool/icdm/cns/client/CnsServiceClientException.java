/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client;

/**
 * @author bne4cob
 */
public class CnsServiceClientException extends Exception {

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
  public CnsServiceClientException(final String errorCode, final String message, final Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /**
   * @param errorCode error code.
   * @param message message
   */
  public CnsServiceClientException(final String errorCode, final String message) {
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
