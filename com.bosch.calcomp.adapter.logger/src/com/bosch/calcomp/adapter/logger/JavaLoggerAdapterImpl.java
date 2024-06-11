package com.bosch.calcomp.adapter.logger;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * REVISION HISTORY<br>
 * Version Date Name Description<br>
 * 0.1 05-Jun-2008 Deepa SAC-82, First Draft<br>
 */

import com.bosch.calcomp.adapter.logger.util.LoggerUtil;

/**
 * REVISION HISTORY<br>
 * Version Date Name Description<br>
 * 0.1 Deepa <SAC-82> First Draft<br>
 * 0.2 23-Jun-2008 Madhu Samuel K <SAC-82> : Added two constructors.<br>
 * Removed setJavaLogger() method. 0.3 24-Jun-2008 Madhu Samuel K <SAC-82> : Renamed to JavaLoggerAdapterImpl<br>
 * from PaCoParserLoggerJavaWrapper.<br>
 * 0.4 25-Nov-2011 Anand <CDMBC-1> : Implemented getLogLevel() <br>
 * and updated the comments
 */

/**
 * A Java Logger adapter implementation of ILoggerAdapter
 */
public class JavaLoggerAdapterImpl implements ILoggerAdapter {

  /**
   * Default Log Location
   */
  private final String DEFAULT_LOG_LOCATION =
      System.getProperty("java.io.tmpdir") + File.separator + "LoggerAdapterDefault.log";

  /**
   * java.util Logger instance variable.
   */
  private Logger javaLogger = null;

  /**
   * Creates a PaCoParserLoggerJavaWrapper with a default java.util.loggin.Logger instance.
   */
  public JavaLoggerAdapterImpl() {
    this.javaLogger = LoggerUtil.getDefaultJavaLogger(this.DEFAULT_LOG_LOCATION);
  }

  /**
   * Creates a JavaLoggerAdapterImpl for the specified java.util.loggin.Logger instance
   *
   * @param javaLogger the Java Logger
   */
  public JavaLoggerAdapterImpl(final Logger javaLogger) {
    this.javaLogger = javaLogger;
  }

  /**
   * Creates a JavaLoggerAdapterImpl with a default java.util.loggin.Logger instance, The log file will be created in
   * the specified filePath.
   *
   * @param filePath the log file path
   */
  public JavaLoggerAdapterImpl(final String filePath) {
    this.javaLogger = LoggerUtil.getDefaultJavaLogger(filePath);
  }

  /**
   * To log information.
   *
   * @param error error message
   */
  @Override
  public void error(final String error) {
    this.javaLogger.severe(error);
  }

  /**
   * To log error with a message and a Throwable object.
   *
   * @param error error message
   * @param cause the error cause
   */
  @Override
  public void error(final String error, final Throwable cause) {
    this.javaLogger.log(Level.SEVERE, error, cause);
  }

  /**
   * To log fatal error.
   *
   * @param fatal fatal message
   */
  @Override
  public void fatal(final String fatal) {
    this.javaLogger.severe(fatal);
  }

  /**
   * To log fatal with a message and a Throwable object.
   *
   * @param fatal fatal message
   * @param cause the error cause
   */
  @Override
  public void fatal(final String fatal, final Throwable cause) {
    this.javaLogger.log(Level.SEVERE, fatal, cause);
  }

  /**
   * To log information during debugging.
   *
   * @param info debug message
   */
  @Override
  public void debug(final String info) {
    this.javaLogger.fine(info);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void debug(final String message, final Object... params) {
    if (isDebugEnabled()) {
      this.javaLogger.fine(LoggerUtil.buildLogMessage(message, params));
    }
  }

  /**
   * To log information.
   *
   * @param info info message
   */
  @Override
  public void info(final String info) {
    this.javaLogger.info(info);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public final void info(final String message, final Object... params) {
    if (getEffectiveLevel().intValue() <= Level.INFO.intValue()) {
      this.javaLogger.info(LoggerUtil.buildLogMessage(message, params));
    }
  }

  /**
   * To log warning.
   *
   * @param warn warn message
   */
  @Override
  public void warn(final String warn) {
    this.javaLogger.warning(warn);
  }

  /**
   * To log warning with a message and a Throwable object.
   *
   * @param warn warn message
   * @param cause the error cause
   */
  @Override
  public void warn(final String warn, final Throwable cause) {
    this.javaLogger.log(Level.WARNING, warn, cause);
  }

  /**
   * Sets the log level
   *
   * @param level log level to set
   */
  @Override
  public void setLogLevel(final int level) {
    switch (level) {
      case LEVEL_DEBUG:
        this.javaLogger.setLevel(Level.FINER);
        break;

      case LEVEL_INFO:
        this.javaLogger.setLevel(Level.INFO);
        break;

      case LEVEL_WARN:
        this.javaLogger.setLevel(Level.WARNING);
        break;

      case LEVEL_ERROR:
        this.javaLogger.setLevel(Level.SEVERE);
        break;

      case LEVEL_FATAL:
        this.javaLogger.setLevel(Level.SEVERE);
        break;

      default:
        break;
    }

  }

  /**
   * Gets the log level
   *
   * @return int
   */
  @Override
  public int getLogLevel() {
    // Effective level is used here instead of javaLogger.getLevel() since getLevel() can be null
    Level level = getEffectiveLevel();
    if (Level.FINER.equals(level)) {
      return LEVEL_DEBUG;
    }
    else if (Level.INFO.equals(level)) {
      return LEVEL_INFO;
    }
    else if (Level.WARNING.equals(level)) {
      return LEVEL_WARN;
    }
    else if (Level.SEVERE.equals(level)) {
      return LEVEL_ERROR;
    }
    else {
      return 0;
    }
  }

  /**
   * @return effective log level
   */
  private Level getEffectiveLevel() {
    Level level = this.javaLogger.getLevel();
    if (level == null) {
      level = getParentLogLevel(this.javaLogger);
    }
    return level;
  }

  /**
   * @param logger parent java logger
   * @return effective level of the parent logger
   */
  private Level getParentLogLevel(final Logger logger) {
    if (logger == null) {
      return Level.WARNING;
    }
    Level level = logger.getLevel();
    return level == null ? getParentLogLevel(logger.getParent()) : level;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDebugEnabled() {
    // Level FINER is used here as the minimum debug level because Logger Adapter considers this as DEBUG level
    // see getLogLevel()
    return getEffectiveLevel().intValue() <= Level.FINER.intValue();
  }
}
