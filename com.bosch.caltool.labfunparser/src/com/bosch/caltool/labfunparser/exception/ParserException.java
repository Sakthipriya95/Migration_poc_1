/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.labfunparser.exception;


/**
 * @author adn1cob
 */
public class ParserException extends Exception {

  // iCDM-711

  private static final long serialVersionUID = 1L;

  /**
   * ICDMFileException
   */
  public ParserException() {
    super();
  }

  /**
   * Constructs a new ICDMFileException with the specified detail message and cause
   * 
   * @param message String
   * @param cause Throwable
   */
  public ParserException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new ICDMFileException with the specified detail message
   * 
   * @param message String
   */
  public ParserException(final String message) {
    super(message);
  }

  /**
   * Constructs a new ICDMFileException with the specified cause
   * 
   * @param cause Throwable
   */
  public ParserException(final Throwable cause) {
    super(cause);
  }


}
