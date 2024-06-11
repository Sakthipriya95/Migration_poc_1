/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.exception;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.dataobject.ErrorMessages;

/**
 * Apic Web Service Exception
 *
 * @author bne4cob
 */
public class ApicWebServiceException extends Exception {

  /**
   * Serial Version ID
   */
  private static final long serialVersionUID = 1L;

  /**
   * Error message model
   */
  private final ErrorMessages errorMessages = new ErrorMessages();

  /**
   * @param errorCode error code. Refer {@link WSErrorCodes} for list of error codes
   * @param message message
   * @param cause cause
   */
  public ApicWebServiceException(final String errorCode, final String message, final Throwable cause) {
    super(message, cause);
    this.errorMessages.getErrors().put(errorCode, message);
  }

  /**
   * @param errorCode error code. Refer {@link WSErrorCodes} for list of error codes
   * @param message message
   */
  public ApicWebServiceException(final String errorCode, final String message) {
    super(message);
    this.errorMessages.getErrors().put(errorCode, message);
  }

  /**
   * Create exception, using a valid error message model from service
   *
   * @param errMsgs ErrorMessages model from service. Message model should have at least one error
   */
  public ApicWebServiceException(final ErrorMessages errMsgs) {
    super(errMsgs.getErrors().entrySet().iterator().next().getValue());
    this.errorMessages.getErrors().putAll(errMsgs.getErrors());
  }

  /**
   * @return the primary error code
   */
  public String getErrorCode() {
    return this.errorMessages.getErrors().isEmpty() ? null : this.errorMessages.getErrors().keySet().iterator().next();
  }

  /**
   * @return the errorMessages, that contains the details of errors
   */
  public ErrorMessages getErrorMessages() {
    return this.errorMessages;
  }

}
