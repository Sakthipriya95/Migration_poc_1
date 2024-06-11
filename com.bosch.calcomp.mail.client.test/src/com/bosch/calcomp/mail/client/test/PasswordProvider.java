/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.mail.client.test;


import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.mail.client.util.MailUtil;

/**
 *
 */
class PasswordProvider {

  private static final ILoggerAdapter LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger(PasswordProvider.class));

  /**
   * @return password
   */
  public String getPassword() {
    // Provide the encrypted password
    return MailUtil.getConfig("TEST.Mail.SenderPassword");
    // return <Encrypted Password>;
  }
}
