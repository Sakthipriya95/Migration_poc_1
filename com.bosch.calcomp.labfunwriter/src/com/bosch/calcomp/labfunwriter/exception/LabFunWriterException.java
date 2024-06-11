/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.labfunwriter.exception;


/**
 * Exception for LabFunWriter
 *
 * @author dja7cob
 */
public class LabFunWriterException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new LabFunWriterException with error message
   *
   * @param message Exception message
   */
  public LabFunWriterException(final String message) {
    super(message);
  }

  /**
   * Creates a new LabFunWriterException with error message and throwable exception
   *
   * @param message Exception message
   * @param exception Throwable Exception
   */
  public LabFunWriterException(final String message, final Throwable exception) {
    super(message);
  }
}
