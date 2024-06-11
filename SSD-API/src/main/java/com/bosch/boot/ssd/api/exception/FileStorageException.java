/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.exception;


/**
 * @author SON9COB
 *
 */
public class FileStorageException extends Exception{

  /**
   * @param message - 
   */
  public FileStorageException(String message) {
      super(message);
  }

  /**
   * @param message -
   * @param cause -
   */
  public FileStorageException(String message, Throwable cause) {
      super(message, cause);
  }
}
