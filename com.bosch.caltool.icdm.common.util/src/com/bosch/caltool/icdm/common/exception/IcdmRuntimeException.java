/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * Icdm-983 Base class for all iCDM Run time exceptions
 * 
 * @author bne4cob
 */
public class IcdmRuntimeException extends RuntimeException {

  /**
   * Serial version ID
   */
  private static final long serialVersionUID = -1482829104967704690L;

  /**
   * the internal error code for all derived classes
   */
  private final int errorCode;


  /**
   * @return the errorCode
   */
  public int getErrorCode() {
    return this.errorCode;
  }

  /**
   * Error constant for the unknown error
   */
  private static final int UNKNOWN_ERROR = -9999;

  /**
   * Create a new runtime exception with error message
   * 
   * @param message the error message text
   * @param errorCode the error code
   */
  public IcdmRuntimeException(final String message, final int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Create a new runtime exception which has been caused by another exception.
   * 
   * @param errorCode the error code
   * @param cause the exception which causes this exception
   */
  public IcdmRuntimeException(final int errorCode, final Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
  }

  /**
   * Create a new runtime exception which has been caused by another runtime exception. The new runtime exception has
   * its own error message.
   * 
   * @param message the error message text
   * @param errorCode the error code
   * @param cause the exception which causes this exception
   */
  public IcdmRuntimeException(final String message, final int errorCode, final Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /**
   * Constructor with the error message.
   * 
   * @param message error message.
   */
  public IcdmRuntimeException(final String message) {
    super(message);
    this.errorCode = UNKNOWN_ERROR;
  }


}
