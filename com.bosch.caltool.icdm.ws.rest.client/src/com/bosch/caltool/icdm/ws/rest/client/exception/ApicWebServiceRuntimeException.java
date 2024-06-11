/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.exception;


/**
 * Apic Web Service Runtime Exceptions
 *
 * @author bne4cob
 */
public class ApicWebServiceRuntimeException extends RuntimeException {

  /**
   * Serial Version ID
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message message
   * @param cause cause
   */
  public ApicWebServiceRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message message
   */
  public ApicWebServiceRuntimeException(final String message) {
    super(message);
  }


}
