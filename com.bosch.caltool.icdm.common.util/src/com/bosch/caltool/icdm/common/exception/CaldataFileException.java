/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * Exception related to Caldatafile read/write
 *
 * @author bne4cob
 */
public class CaldataFileException extends IcdmException {

  /**
   * Serial ID
   */
  private static final long serialVersionUID = -2428003070867883387L;

  /**
   * @param message message
   * @param cause cause
   */
  public CaldataFileException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with the error message.
   *
   * @param message error message.
   */
  public CaldataFileException(final String message) {
    super(message);
  }

}
