/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author bne4cob
 */
public final class JPALogger extends AbstractLogger {

  /**
   * JPALoggerObj instance
   */
  private static final JPALogger INSTANCE = new JPALogger();

  /**
   * Returns the singleton class instance.
   *
   * @return JPALogger
   */
  public static JPALogger getInstance() {
    return INSTANCE;
  }

  /**
   * {@inheritDoc}
   */
  // ICDM-2202
  @Override
  protected Logger getLogger() {
    return LogManager.getLogger(ICDMLoggerConstants.JPA_LOGGER);
  }


}
