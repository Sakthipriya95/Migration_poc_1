/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * @author SON9COB
 *
 */
@ResponseStatus(HttpStatus.FOUND)
public class FileChecksumException extends Exception{

  /**
   * @param message - 
   */
  public FileChecksumException(String message) {
      super(message);
  }

  /**
   * @param message - 
   * @param cause -
   */
  public FileChecksumException(String message, Throwable cause) {
      super(message, cause);
  }
}
