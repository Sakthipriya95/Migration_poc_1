/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * Icdm-983
 *
 * @author bne4cob
 */
public class DataRuntimeException extends IcdmRuntimeException {


  /**
   * Serial version ID
   */
  private static final long serialVersionUID = -7964594575682165719L;

  /**
   * @param errorCode errorCode
   * @param cause cause
   */
  public DataRuntimeException(final int errorCode, final Throwable cause) {
    super(errorCode, cause);
  }

  /**
   * @param message message
   * @param errorCode errorCode
   * @param cause cause
   */
  public DataRuntimeException(final String message, final int errorCode, final Throwable cause) {
    super(message, errorCode, cause);
  }

  /**
   * @param message message
   * @param errorCode errorCode
   */
  public DataRuntimeException(final String message, final int errorCode) {
    super(message, errorCode);
  }

  /**
   * Constructor with the error message.
   *
   * @param message error message.
   */
  public DataRuntimeException(final String message) {
    super(message);
  }

}
