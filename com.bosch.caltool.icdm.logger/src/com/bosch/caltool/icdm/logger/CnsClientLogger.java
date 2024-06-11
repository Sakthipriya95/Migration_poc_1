/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author BNE4COB
 */
public final class CnsClientLogger extends AbstractLogger {

  /**
   * CnsClientLogger instance
   */
  private static final CnsClientLogger INSTANCE = new CnsClientLogger();

  /**
   * Returns the singleton class instance.
   *
   * @return CnsClientLogger
   */
  public static CnsClientLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.CNS_CLIENT_LOGGER);
  }
}
