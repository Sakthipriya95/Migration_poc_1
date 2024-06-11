/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.mail.client.util;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.mail.client.exception.MailServiceException;
import com.bosch.caltool.pwdservice.PasswordService;
import com.bosch.caltool.pwdservice.exception.PasswordNotFoundException;
import com.bosch.caltool.security.Decryptor;

/**
 * @author TUD1COB
 */
public class MailUtil {

  private static final ILoggerAdapter LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger(MailUtil.class));

  /**
  *
  */
  private MailUtil() {
    // Utility class
  }

  /**
   * @param key to get the configuration
   * @return actual configuration
   */
  public static String getConfig(final String key) {
    String result;
    try {
      PasswordService passwordService = new PasswordService();
      result = passwordService.getPassword(key);
      return result;
    }
    catch (PasswordNotFoundException e) {
      LOGGER.error(key + " not found in Password Webservice", e);
    }
    throw new MailServiceException("Missing Configuration, Key: " + key);
  }

  /**
   * @param encryptedPassword input
   * @return decrypted password
   */
  public static String getDecryptedPassword(final String encryptedPassword) {
    return Decryptor.INSTANCE.decrypt(encryptedPassword, LOGGER);
  }

}
