/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author adn1cob
 */
public final class LdapLogger extends AbstractLogger {

  /**
   * LDAPLogger instance
   */
  private static final LdapLogger INSTANCE = new LdapLogger();

  /**
   * Returns the singleton class instance.
   *
   * @return CDMLogger
   */
  public static LdapLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.LDAP_LOGGER);
  }
}

