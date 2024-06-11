/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression.exception;

/**
 * @author bne4cob
 */
public class BlobCompressionException extends Exception {

  /**
   * serial version ID
   */
  private static final long serialVersionUID = -3280507018736806518L;

  /**
   * @param message error message
   */
  public BlobCompressionException(final String message) {
    super(message);
  }

  /**
   * @param message error message
   * @param exp exception
   */
  public BlobCompressionException(final String message, final Exception exp) {
    super(message, exp);
  }

}
