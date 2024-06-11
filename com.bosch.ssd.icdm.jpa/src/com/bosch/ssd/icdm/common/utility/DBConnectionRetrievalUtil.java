/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.common.utility;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.caltool.security.Decryptor;


/**
 * Retrieve DB Connection String from Password Web Service
 *
 * @author SSN9COB
 */

public final class DBConnectionRetrievalUtil {

  // Class instance
  private static DBConnectionRetrievalUtil dbConnectionStringRetrival;

  private DBConnectionRetrievalUtil() {
    // private constructor
  }

  /**
   * singleton method for DBPasswordRetrival class
   *
   * @return dbPwdRetrival
   */
  public static DBConnectionRetrievalUtil getInstance() {
    // if null create a new instance of the util
    if (dbConnectionStringRetrival == null) {
      dbConnectionStringRetrival = new DBConnectionRetrievalUtil();
    }
    // return the single instance
    return dbConnectionStringRetrival;
  }

  /**
   * method to get password from webservice and decrypt
   *
   * @param key key
   * @param logger loggerInst
   * @return password pwd
   * @throws PasswordNotFoundException exception
   */
  public String getConnStringAndDecrypt(final String key, final ILoggerAdapter logger)
      throws PasswordNotFoundException {
    String password = null;
    // invoke password web service
    PasswordService pwdService = new PasswordService();
    // decrypt the password for the key passed from the pwdservice
    password = Decryptor.INSTANCE.decrypt(pwdService.getPassword(key), logger);


    // return the password (connection String)
    return password;
  }

}
