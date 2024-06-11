/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.exception;


/**
 * @author adn1cob
 */
public class MailException extends IcdmException {

  /**
  *
  */
  private static final long serialVersionUID = 7150611252130774615L;

  /**
   * Constructs a new MailException with the specified errorCode/message and cause
   *
   * @param errorCode errorCode/message
   * @param cause Throwable
   */
  public MailException(final String errorCode, final Throwable cause) {
    super(errorCode, cause);
  }

  /**
   * Constructs a new MailException with the specified errorCode/message
   *
   * @param errorCode errorCode/message
   */
  public MailException(final String errorCode) {
    super(errorCode);
  }


}
