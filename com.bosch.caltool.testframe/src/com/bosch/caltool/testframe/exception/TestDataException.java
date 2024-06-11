/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.exception;


/**
 * @author bne4cob
 */
public class TestDataException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public TestDataException() {
    super();
  }

  /**
   * @param message message
   */
  public TestDataException(final String message) {
    super(message);
  }

  /**
   * @param cause cause
   */
  public TestDataException(final Throwable cause) {
    super(cause);
  }

  /**
   * @param message message
   * @param cause cause
   */
  public TestDataException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message message
   * @param cause cause
   * @param enableSuppression enableSuppression
   * @param writableStkTrce writableStackTrace
   */
  public TestDataException(final String message, final Throwable cause, final boolean enableSuppression,
      final boolean writableStkTrce) {

    super(message, cause, enableSuppression, writableStkTrce);

  }

}
