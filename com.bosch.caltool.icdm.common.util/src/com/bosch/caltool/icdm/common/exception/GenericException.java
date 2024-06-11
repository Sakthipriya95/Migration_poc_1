/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * @author bne4cob
 */
public class GenericException extends IcdmException {

  /**
   * Serial version ID
   */
  private static final long serialVersionUID = -6477943471882089655L;

  /**
   * @param errorCode error Code/message
   * @param cause cause
   */
  public GenericException(final String errorCode, final Throwable cause) {
    super(errorCode, cause);
  }

  /**
   * Constructor with the error code/message.
   *
   * @param errorCode error code/message.
   */
  public GenericException(final String errorCode) {
    super(errorCode);
  }

}
