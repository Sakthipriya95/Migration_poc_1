/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logger for messages from Check SSD
 *
 * @author bne4cob
 */
public final class CheckSSDLogger extends AbstractLogger {

  /**
   * Unique Instance of CheckSSDLogger class
   */
  private static final CheckSSDLogger INSTANCE = new CheckSSDLogger();

  /**
   * Returns the singleton class instance.
   *
   * @return CheckSSDLogger
   */
  public static CheckSSDLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.CHECK_SSD_LOGGER);
  }

}
