/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;


/**
 * @author bne4cob
 */
public class LdapException extends Exception {

  /**
   * Serial Version ID
   */
  private static final long serialVersionUID = 3273014772329977317L;

  /**
   * Create an exception with a message
   * 
   * @param message message
   */
  public LdapException(final String message) {
    super(message);
  }

  /**
   * Create an exception with a message and a reference exception
   * 
   * @param message message
   * @param cause source exception
   */
  public LdapException(final String message, final Throwable cause) {
    super(message, cause);
  }


}
