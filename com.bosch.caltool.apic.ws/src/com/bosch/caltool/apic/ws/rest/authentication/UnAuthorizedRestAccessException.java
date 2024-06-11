/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.authentication;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author SVJ7COB
 */
public class UnAuthorizedRestAccessException extends WebApplicationException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message error message
   */
  public UnAuthorizedRestAccessException(final String message) {
    super(message, Response.Status.UNAUTHORIZED);
  }

  /**
   * @param message error message
   * @param exp cause
   */
  public UnAuthorizedRestAccessException(final String message, final Throwable exp) {
    super(message, exp, Response.Status.UNAUTHORIZED);
  }

}
