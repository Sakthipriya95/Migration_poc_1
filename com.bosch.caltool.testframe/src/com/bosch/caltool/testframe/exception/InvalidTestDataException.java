/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.exception;


/**
 * @author bne4cob
 */
public class InvalidTestDataException extends TestDataException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public InvalidTestDataException() {
    super();
  }

  /**
   * @param message message
   */
  public InvalidTestDataException(final String message) {
    super(message);
  }

  /**
   * @param excep exception
   */
  public InvalidTestDataException(final Throwable excep) {
    super(excep);
  }

  /**
   * @param message message
   * @param exception exception
   */
  public InvalidTestDataException(final String message, final Throwable exception) {
    super(message, exception);
  }

  /**
   * @param message message
   * @param exception exception
   * @param arg2 arg2
   * @param arg3 arg3
   */
  public InvalidTestDataException(final String message, final Throwable exception, final boolean arg2,
      final boolean arg3) {
    super(message, exception, arg2, arg3);
  }

}
