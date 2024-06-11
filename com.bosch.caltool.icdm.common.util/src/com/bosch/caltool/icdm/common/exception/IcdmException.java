/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all iCDM exceptions.
 *
 * @author bne4cob
 */
public class IcdmException extends Exception {

  /**
   * Serial version ID.
   */
  private static final long serialVersionUID = 5361665592193842023L;

  /**
   * the internal error code for all derived classes.
   */
  private final String errorCode;
  /**
   * To store addition info
   */
  private final Map<String, String> additionalInfo = new HashMap<>();

  /**
   * The var args.
   */
  private final transient Object[] varArgs;

  /**
   * Create a new exception with error code/message, when the message has dynamic variable replacements
   *
   * @param errorCode the error code/message
   * @param varArgs error message's dynamic replacements. If the first object is an Exception, then it is treated as
   *          'cause'
   */
  public IcdmException(final String errorCode, final Object... varArgs) {
    super(errorCode);
    this.errorCode = errorCode;

    if ((varArgs != null) && (varArgs.length > 0)) {
      if (varArgs[0] instanceof Exception) {
        Exception ex = (Exception) varArgs[0];
        initCause(ex);
        this.varArgs = varArgs.length > 1 ? Arrays.copyOfRange(varArgs, 1, varArgs.length) : null;
      }
      else {
        this.varArgs = varArgs;
      }
    }
    else {
      this.varArgs = null;
    }
  }

  /**
   * Get the error code of this exception.
   *
   * @return the error code of the exception
   */
  public String getErrorCode() {
    return this.errorCode;
  }


  /**
   * Gets the var args.
   *
   * @return the varArgs
   */
  public Object[] getVarArgs() {
    return this.varArgs;
  }


  /**
   * @return the additional info
   */
  public Map<String, String> getAdditionalInfo() {
    return this.additionalInfo;
  }

  /**
   * adding additional info will be set to response header
   *
   * @param headerKey key of response header
   * @param headerValue value of response header
   */
  public void addAdditinalInfo(final String headerKey, final String headerValue) {
    this.additionalInfo.put(headerKey, headerValue);
  }
}
