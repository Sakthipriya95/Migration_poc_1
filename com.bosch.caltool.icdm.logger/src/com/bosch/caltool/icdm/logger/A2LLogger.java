/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author adn1cob
 */
public final class A2LLogger extends AbstractLogger {

  /**
   * A2LLogger instance
   */
  private static final A2LLogger INSTANCE = new A2LLogger();

  /**
   * Returns the singleton class instance.
   *
   * @return CDMLogger
   */
  public static A2LLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.A2L_LOGGER);
  }
}
