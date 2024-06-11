/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.ssd.service.exception;


/**
 * @author QRK1COB
 */
public class SsdReleaseInfoException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message exception message
   */
  public SsdReleaseInfoException(final String message) {
    super(message);
  }
}

