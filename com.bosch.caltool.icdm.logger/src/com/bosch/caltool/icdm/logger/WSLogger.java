/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * Main logger for ICDM web service
 *
 * @author bne4cob
 */
// ICDM-2202
public final class WSLogger extends AbstractLogger {

  /**
   * Name of the logger
   */
  private final String loggerName;

  /**
   * Map of logger implementations, against their names
   */
  private static final ConcurrentMap<String, ILoggerAdapter> loggerMap = new ConcurrentHashMap<>();

  /**
   * Unique Instance of WSLogger class
   */
  private static final WSLogger INSTANCE = new WSLogger("ICDMWS");

  /**
   * Private constructor
   *
   * @param loggerName logger name
   */
  private WSLogger(final String loggerName) {
    super();
    this.loggerName = loggerName;
  }


  /**
   * Returns the singleton class instance.
   *
   * @return WSLogger
   */
  public static WSLogger getInstance() {
    return INSTANCE;
  }

  /**
   * Provides a logger with the given logger name. If the logger is not created yet, a new instance is provided.
   *
   * @param loggerName logger name
   * @return ILoggerAdapter implementation
   */
  public static ILoggerAdapter getLogger(final String loggerName) {
    return loggerMap.computeIfAbsent(loggerName, k -> new WSLogger(loggerName));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Logger getLogger() {
    return LogManager.getLogger(this.loggerName);
  }

}
