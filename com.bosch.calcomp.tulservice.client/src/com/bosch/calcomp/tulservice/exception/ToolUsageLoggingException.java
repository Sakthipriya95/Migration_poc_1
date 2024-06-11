/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.exception;


/**
 * @author GDH9COB
 *
 */
public class ToolUsageLoggingException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -8426032242045464511L;
  
  /**
   * 
   */
  public ToolUsageLoggingException() {
    super();
  }
  
  /**
   * @param message Error message
   * 
   */
  public ToolUsageLoggingException(String message) {
    super(message);
  }

  /**
   * @param cause Throwable
   * 
   */
  public ToolUsageLoggingException(Throwable cause) {
    super(cause);
  }
  
  /**
   * @param message Error message
   * @param cause Throwable
   */
  public ToolUsageLoggingException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * @param message Error message
   * @param cause Throwable
   * @param enableSuppression based on user set flag
   * @param writableStackTrace based on user set flag
   */
  public ToolUsageLoggingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }


}
