/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author bne4cob
 */
public final class TwoFALogger extends AbstractLogger {

  /**
   * TwoFALogger instance
   */
  private static final TwoFALogger INSTANCE = new TwoFALogger();

  /**
   * Returns the singleton class instance.
   *
   * @return TwoFALogger
   */
  public static TwoFALogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.TWO_FA_LOGGER);
  }
}

