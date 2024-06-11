/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.authentication.ldap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;


/**
 * @author rgo7cob
 */
public class PasswordServiceWrapper {

  // Icdm-1644 - New Wrapper for password service.
  /**
   * logger for logging
   */
  ILoggerAdapter logger;

  /**
   * @param logger logger input
   */
  public PasswordServiceWrapper(final ILoggerAdapter logger) {
    this.logger = logger;
  }


  /**
   * @param passwordKey password Key
   * @return the password for the given passWordName from passwordServer
   */
  public String getPassword(final String passwordKey) {
    String password = "";
    PasswordService service = new PasswordService();
    try {
      password = service.getPassword(passwordKey);
    }
    catch (PasswordNotFoundException exception) {
      this.logger.error(exception.getMessage(), exception);
    }
    return password;

  }

}
