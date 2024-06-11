package com.bosch.caltool.nattable.exception;


/**
 * @author jvi6cob
 */
public class NatTableException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * NatTableException
   */
  public NatTableException() {
    super();
  }

  /**
   * Constructs a new NatTableException with the specified detail message and cause
   * 
   * @param message String
   * @param cause Throwable
   */
  public NatTableException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new NatTableException with the specified detail message
   * 
   * @param message String
   */
  public NatTableException(final String message) {
    super(message);
  }

  /**
   * Constructs a new NatTableException with the specified cause
   * 
   * @param cause Throwable
   */
  public NatTableException(final Throwable cause) {
    super(cause);
  }

}
