/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.exception;


/**
 * @author bne4cob
 */
public class TestDataReaderException extends TestDataException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  public TestDataReaderException() {
    super();
  }

  /**
   * @param message message
   */
  public TestDataReaderException(final String message) {
    super(message);
  }

  /**
   * @param cause cause
   */
  public TestDataReaderException(final Throwable cause) {
    super(cause);
  }

  /**
   * @param message message
   * @param cause cause
   */
  public TestDataReaderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message message
   * @param cause cause
   * @param enableSuppression enableSuppression
   * @param writableStkTrce writableStackTrace
   */
  public TestDataReaderException(final String message, final Throwable cause, final boolean enableSuppression,
      final boolean writableStkTrce) {
    super(message, cause, enableSuppression, writableStkTrce);
  }

}
