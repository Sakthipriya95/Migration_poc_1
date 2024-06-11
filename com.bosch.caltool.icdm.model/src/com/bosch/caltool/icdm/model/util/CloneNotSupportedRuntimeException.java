/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.util;


/**
 * @author ukt1cob
 */
public class CloneNotSupportedRuntimeException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 4247735429546565547L;

  /**
   * @param cause - exception
   */
  public CloneNotSupportedRuntimeException(final Throwable cause) {
    super(cause);
  }

}
