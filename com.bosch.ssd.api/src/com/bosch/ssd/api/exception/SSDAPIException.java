/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.exception;


/**
 * Base class for all SSDAPI exceptions
 * 
 * @author VAU3COB
 */
public class SSDAPIException extends Exception {

  /**
   * Serial version ID
   */
  private static final long serialVersionUID = 5361665592193842023L;

  /**
   * Error constant for the unknown error
   */
  private static final int UNKNOWN_ERROR = -9999;

  /**
   * SSD_INTERFACE_ERROR = 1001;
   */
  public static final int SSD_INTERFACE_ERROR = 1001;

  /**
   * the internal error code for all derived classes
   */
  private final int errorCode;

  /**
   * Create a new exception with error message and error code.
   * 
   * @param message the error message text
   * @param errorCode the error code
   */
  public SSDAPIException(final String message, final int errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Create a new exception which has been caused by another exception.
   * 
   * @param errorCode the error code
   * @param cause the exception which causes this exception
   */
  public SSDAPIException(final int errorCode, final Throwable cause) {
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
  public SSDAPIException(final String message, final int errorCode, final Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /**
   * Constructor with the error message.
   * 
   * @param message error message.
   */
  public SSDAPIException(final String message) {
    super(message);
    this.errorCode = UNKNOWN_ERROR;
  }

  /**
   * Constructor with the error message and cause
   * 
   * @param message error message.
   * @param cause the exception which causes this exception
   */
  public SSDAPIException(final String message, final Throwable cause) {
    super(message, cause);
    this.errorCode = UNKNOWN_ERROR;
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
