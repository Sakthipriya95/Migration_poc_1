/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author dmo5cob
 */
public final class SSDLogger extends AbstractLogger {

  /**
   * ssdLoggerObj instance
   */
  private static final SSDLogger INSTANCE = new SSDLogger();

  /**
   * Returns the singleton class instance.
   *
   * @return CDFLogger
   */
  public static SSDLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.SSD_LOGGER);
  }

}
