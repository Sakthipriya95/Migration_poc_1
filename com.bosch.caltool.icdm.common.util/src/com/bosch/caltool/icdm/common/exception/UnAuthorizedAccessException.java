/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * @author bne4cob
 */
public class UnAuthorizedAccessException extends IcdmException {

  /**
   * Serial ID
   */
  private static final long serialVersionUID = 968975440778408450L;

  /**
   * @param errorCode error code/message
   */
  public UnAuthorizedAccessException(final String errorCode) {
    super(errorCode);
  }

  /**
   * Create a new exception with error code/message, when the message has dynamic variable replacements
   *
   * @param errorCode the error code/message
   * @param varArgs error message's dynamic replacements. If the first object is an Exception, then it is treated as
   *          'cause'
   */
  public UnAuthorizedAccessException(final String errorCode, final Object... varArgs) {
    super(errorCode, varArgs);
  }

}
