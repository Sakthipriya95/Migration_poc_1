/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

/**
 * Runtime exceptions from data model
 * 
 * @author bne4cob
 */
public class DMRuntimeException extends RuntimeException {

  //
  // error codes:
  //
  // 1001 - Duplicate ID given for Entity User attribute storage

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 1L;

  /**
   * the internal error code
   */
  private final int errorCode;

  /**
   * Create a new exception with error message and error code.
   * 
   * @param message the error message text
   * @param errorCode the error code
   */
  public DMRuntimeException(final String message, final int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Create a new exception which has been caused by another exception.
   * 
   * @param errorCode the error code
   * @param cause the exception which causes this exception
   */
  public DMRuntimeException(final int errorCode, final Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
  }

  /**
   * Create a new exception which has been caused by another exception. The new exception has its own error message.
   * 
   * @param message the error message text
   * @param errorCode the error code
   * @param cause the exception which causes this exception
   */
  public DMRuntimeException(final String message, final int errorCode, final Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /**
   * Get the error code of an exception
   * 
   * @return the error code of the exception
   */
  public int getErrorCode() {
    return this.errorCode;
  }
}
