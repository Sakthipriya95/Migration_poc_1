/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.monicareportparser.exception;


/**
 * @author rgo7cob
 */
public class MonicaRptParserException extends Exception {


  private static final long serialVersionUID = 1L;


  /**
   * Constructs a new ICDMFileException with the specified detail message and cause
   * 
   * @param message String
   * @param cause Throwable
   */
  public MonicaRptParserException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new ICDMFileException with the specified detail message
   * 
   * @param message String
   */
  public MonicaRptParserException(final String message) {
    super(message);
  }


}
