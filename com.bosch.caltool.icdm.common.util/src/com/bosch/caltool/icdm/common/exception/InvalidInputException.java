/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * Exception to be thrown if the input(s) (for e.g. to a web service or a method) is invalid
 *
 * @author bne4cob
 */
public class InvalidInputException extends DataException {

  /**
   * Serial version ID
   */
  private static final long serialVersionUID = 7754822350472829334L;

  /**
   * Create a new exception with error code/message, when the message has dynamic variable replacements
   *
   * @param errorCode the error code/message
   * @param varArgs error message's dynamic replacements. If the first object is an Exception, then it is treated as
   *          'cause'
   */
  public InvalidInputException(final String errorCode, final Object... varArgs) {
    super(errorCode, varArgs);
  }
}
