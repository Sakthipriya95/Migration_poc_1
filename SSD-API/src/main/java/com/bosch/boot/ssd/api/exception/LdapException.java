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
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class LdapException extends Exception{

  /**
   * 
   */
  private static final long serialVersionUID = 3781286769975424009L;

  /**
   * @param message - 
   */
  public LdapException(String message) {
      super(message);
  }

  /**
   * @param message - 
   * @param cause -
   */
  public LdapException(String message, Throwable cause) {
      super(message, cause);
  }
}
