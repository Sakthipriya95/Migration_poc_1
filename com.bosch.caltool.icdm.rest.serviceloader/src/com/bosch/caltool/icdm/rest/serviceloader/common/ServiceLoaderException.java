/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.serviceloader.common;


/**
 * @author bne4cob
 */
public class ServiceLoaderException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = -7256169761522880289L;

  /**
   * @param message error mesage
   * @param cause parent exception
   */
  public ServiceLoaderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message error message
   */
  public ServiceLoaderException(final String message) {
    super(message);
  }


}
