/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * @author bne4cob
 */
public class SsdInterfaceException extends IcdmException {

  /**
   *
   */
  private static final long serialVersionUID = -4303160311404940310L;

  /**
   * Create a new exception with error code/message, when the message has dynamic variable replacements
   *
   * @param errorCode the error code/message
   * @param varArgs error message's dynamic replacements. If the first object is an Exception, then it is treated as
   *          'cause'
   */
  public SsdInterfaceException(final String errorCode, final Object... varArgs) {
    super(errorCode, varArgs);
  }

}
