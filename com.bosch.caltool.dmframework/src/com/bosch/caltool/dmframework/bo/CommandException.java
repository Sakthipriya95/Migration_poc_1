/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * Exception class for handling of exceptions within command operations
 *
 * @author adn1cob
 */
public class CommandException extends IcdmException {

  /**
   * Serial version ID
   */
  private static final long serialVersionUID = -212643764565738243L;


  /**
   * Create a new exception which has been caused by another exception.
   *
   * @param errorCode the error code
   * @param message the message
   * @param varArgs the var args
   */
  @Deprecated
  public CommandException(final String errorCode, final String message, final Object... varArgs) {
    super(errorCode, varArgs);
  }

  /**
   * Constructor with the error message and the Throwable object.
   *
   * @param message error message.
   * @param errorCode error code.
   */
  @Deprecated
  public CommandException(final String message, final int errorCode) {
    super(message, errorCode);
  }

  /**
   * Constructor with the error message.
   *
   * @param message error message.
   */
  public CommandException(final String message) {
    super(message);
  }

  /**
   * Constructor with the error message and cause
   *
   * @param message error message.
   * @param cause the exception which causes this exception
   */
  public CommandException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
