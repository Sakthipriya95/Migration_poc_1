/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.mail.client.exception;


/**
 * @author TUD1COB
 */
public class MailServiceException extends RuntimeException {


  /**
   *
   */
  private static final long serialVersionUID = 2163390630439937993L;


  /**
   * @param message message
   */
  public MailServiceException(final String message) {
    super(message);
  }


  /**
   * @param message message
   * @param cause cause
   */
  public MailServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
