/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * Class to decrypt the encrypted text
 * 
 * @author bne4cob
 */
public enum Decryptor {
  /**
   * Instance
   */
  INSTANCE;

  /**
   * Error message
   */
  private static final String ERR_MSG = "Error occured while decrypting password";

  /**
   * @return the instance
   */
  public static Decryptor getInstance() {
    return INSTANCE;
  }

  /**
   * Decrypts the encrypted password.
   * 
   * @param encPassword the encrypted password
   * @param logger logger
   * @return the decrypted password as plain text
   */
  public String decrypt(final String encPassword, final ILoggerAdapter logger) {

    try {
      return CryptUtil.INSTANCE.decrypt(encPassword);
    }
    catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
        | BadPaddingException | IOException exp) {
      logger.error(ERR_MSG, exp);
    }

    return null;
  }
}
