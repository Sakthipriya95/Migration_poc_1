/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient;

/**
 * @author bne4cob
 */
public class SoapServiceTestException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = -1535645850443199597L;

  /**
   * @param message error messsage
   * @param cause cause
   */
  public SoapServiceTestException(final String message, final Exception cause) {
    super(message, cause);
  }

}
