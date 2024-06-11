/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * @author adn1cob
 */
public abstract class AbstractLogger implements ILoggerAdapter {

  /**
   * Provide the Log4J Logger
   *
   * @return an instance of Log4J Logger
   */
  // ICDM-2202
  protected abstract Logger getLogger();

  /**
   * {@inheritDoc}
   */
  @Override
  public void info(final String info) {
    // Log message to log file
    getLogger().info(info);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void info(final String message, final Object... params) {
    getLogger().info(message, params);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void warn(final String warning) {
    // Log warning message to log file
    getLogger().warn(warning);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void warn(final String warning, final Throwable cause) {
    // Log warning message to log file
    getLogger().warn(warning, cause);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void error(final String error) {
    // Log error message to log file
    getLogger().error(error);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void error(final String error, final Throwable cause) {
    // Log error message to log file
    getLogger().error(error, cause);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fatal(final String fatal) {
    // Log fatal message to log file
    getLogger().fatal(fatal);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fatal(final String fatal, final Throwable cause) {
    // Log fatal message to log file
    getLogger().fatal(fatal, cause);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void debug(final String info) {
    // Log debug message to only to log file
    getLogger().debug(info);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void debug(final String message, final Object... params) {
    getLogger().debug(message, params);
  }

  /**
   * Sets the log level.
   *
   * @param level the message
   */
  @Override
  public void setLogLevel(final int level) {
    LoggerConfig logConfig = getLoggerConfig();
    switch (level) {
      case LEVEL_DEBUG:
        logConfig.setLevel(Level.DEBUG);
        break;

      case LEVEL_INFO:
        logConfig.setLevel(Level.INFO);
        break;

      case LEVEL_WARN:
        logConfig.setLevel(Level.WARN);
        break;

      case LEVEL_ERROR:
        logConfig.setLevel(Level.ERROR);
        break;

      case LEVEL_FATAL:
        logConfig.setLevel(Level.FATAL);
        break;

      default:
        break;
    }

    getLoggerContext().updateLoggers();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getLogLevel() {

    int level;

    switch (getLogger().getLevel().intLevel()) {
      case 500:
        level = LEVEL_DEBUG;
        break;

      case 400:
        level = LEVEL_INFO;
        break;

      case 300:
        level = LEVEL_WARN;
        break;

      case 200:
        level = LEVEL_ERROR;
        break;

      case 100:
        level = LEVEL_FATAL;
        break;

      default:
        level = 0;
    }
    return level;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDebugEnabled() {
    return getLogger().isDebugEnabled();
  }

  /**
   * @param apploggername logger name
   * @return LoggerConfig
   */
  private LoggerConfig getLoggerConfig() {
    return getLoggerContext().getConfiguration().getLoggerConfig(getLogger().getName());
  }

  /**
   * @return LoggerContext
   */
  private LoggerContext getLoggerContext() {
    return (LoggerContext) LogManager.getContext(false);
  }
}
