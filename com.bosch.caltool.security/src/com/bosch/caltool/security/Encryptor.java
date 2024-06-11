/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * Class to encrypt text
 *
 * @author dmo5cob
 */
public enum Encryptor {
                       /**
                        * Instance
                        */
                       INSTANCE;

  /**
   * Error message
   */
  private static final String ERR_MSG = "Error occured while encrypting password";

  /**
   * @return the instance
   */
  public static Encryptor getInstance() {
    return INSTANCE;
  }

  /**
   * Encrypts the password.
   *
   * @param passwordTxt the encrypted password
   * @param logger logger
   * @return the encrypted password as plain text
   */
  public String encrypt(final String passwordTxt, final ILoggerAdapter logger) {

    try {
      return CryptUtil.INSTANCE.encrypt(passwordTxt);
    }
    catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
        | BadPaddingException exp) {
      logger.error(ERR_MSG, exp);
    }

    return null;
  }
}
