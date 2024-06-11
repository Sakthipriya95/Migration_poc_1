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
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {
  
  public ResourceNotFoundException() {
    super("Resource not found");
  }
  
  public ResourceNotFoundException(String message) {
    super(message);
  }

  /**
   * @param fileNotFound
   * @param ex
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

}
