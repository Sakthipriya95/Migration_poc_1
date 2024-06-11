/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author adn1cob
 */
public final class CDFWriterLogger extends AbstractLogger {

  /**
   * CDFLoggerObj instance
   */
  private static final CDFWriterLogger INSTANCE = new CDFWriterLogger();

  /**
   * Returns the singleton class instance.
   *
   * @return CDFLogger
   */
  public static CDFWriterLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.CDFWRITER_LOGGER);
  }

}
