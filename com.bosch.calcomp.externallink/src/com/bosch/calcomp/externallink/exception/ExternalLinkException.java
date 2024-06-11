/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.exception;

/**
 * External link exceptions
 *
 * @author bne4cob
 */
// ICDM-1649
public class ExternalLinkException extends Exception {

  /**
   * Serialisation ID
   */
  private static final long serialVersionUID = 5090292881399047186L;

  /**
   * Creates new excpetion instance with the given error message
   *
   * @param message error message
   */
  public ExternalLinkException(final String message) {
    super(message);
  }

  /**
   * Creates new excpetion instance with the given error message and cause
   *
   * @param message error message
   * @param cause generated exception
   */
  public ExternalLinkException(final String message, final Throwable cause) {
    super(message, cause);
  }


}
