/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author dmo5cob
 */
public final class ParserLogger extends AbstractLogger {

  /**
   * parserLoggerObj instance
   */
  private static final ParserLogger INSTANCE = new ParserLogger();

  /**
   * Returns the singleton class instance.
   *
   * @return CDFLogger
   */
  public static ParserLogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.PARSER_LOGGER);
  }


}
