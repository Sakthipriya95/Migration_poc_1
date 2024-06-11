/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author GDH9COB
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParameterInvalidException extends Exception {

  /**
   * 
   */
  public ParameterInvalidException() {
    super("Input parameters missing");
  }
  
  /**
   * @param message
   */
  public ParameterInvalidException(String message) {
    super(message);
  }

  /**
   * @param fileNotFound
   * @param ex
   */
  public ParameterInvalidException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
