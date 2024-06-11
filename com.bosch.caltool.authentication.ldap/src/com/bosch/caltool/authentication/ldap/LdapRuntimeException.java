/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;

/**
 * Runtime exception for LDAP authentication related exceptions.
 *
 * @author bne4cob
 */
public class LdapRuntimeException extends RuntimeException {

  /**
   * id
   */
  private static final long serialVersionUID = 3273014772329977317L;

  /**
   * Creates an instance of <code>LdapException</code> with the input message.
   *
   * @param message error message of the exception.
   */
  public LdapRuntimeException(final String message) {
    super(message);
  }

  /**
   * Create an exception with a message and a reference exception
   * <p>
   * Use the constructor, when wrapping another exception
   *
   * @param message message
   * @param cause source exception
   */
  public LdapRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
