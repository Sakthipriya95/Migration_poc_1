/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;


/**
 * @author rgo7cob
 */
public class PasswordServiceWrapper {

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
   * Get the password for the given key
   * 
   * @param passwordKey password Key
   * @return the password for the given passWordName from passwordServer
   */
  public String getPassword(final String passwordKey) {
    String passwrd = "";
    PasswordService service = new PasswordService();
    try {
      passwrd = service.getPassword(passwordKey);
    }
    catch (PasswordNotFoundException exception) {
      this.logger.error(exception.getMessage(), exception);
    }
    return passwrd;

  }
}
