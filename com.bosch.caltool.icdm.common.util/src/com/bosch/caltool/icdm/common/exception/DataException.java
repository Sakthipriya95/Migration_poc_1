/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * @author bne4cob
 */
public class DataException extends IcdmException {


  /**
   * Serial version ID
   */
  private static final long serialVersionUID = 7211722794664850812L;

  /**
   * ERCD_DB_EX_DURING_INIT1 = 1001;
   *
   * @deprecated not used
   */
  @Deprecated
  public static final String ERCD_DB_EX_DURING_INIT1 = "1001";

  /**
   * ERCD_INIT_UNEXP_ERR = 1003;
   * 
   * @deprecated not used
   */
  @Deprecated
  public static final String ERCD_INIT_UNEXP_ERR = "1003";

  /**
   * Create a new exception which has been caused by another exception.
   *
   * @param errorCode the error code/message
   * @param message error message
   * @param varArgs the var args
   * @deprecated use constructor {@link #DataException(String, Object...)} instead
   */
  @Deprecated
  public DataException(final String errorCode, final String message, final Object... varArgs) {
    super(errorCode, varArgs);
  }

  /**
   * Create a new exception with error code/message, when the message has dynamic variable replacements
   *
   * @param errorCode the error code/message
   * @param varArgs error message's dynamic replacements. If the first object is an Exception, then it is treated as
   *          'cause'
   */
  public DataException(final String errorCode, final Object... varArgs) {
    super(errorCode, varArgs);
  }
}
