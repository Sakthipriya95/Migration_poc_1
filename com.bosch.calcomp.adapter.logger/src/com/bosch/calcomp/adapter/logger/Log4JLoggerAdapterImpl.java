package com.bosch.calcomp.adapter.logger;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Loader;

/**
 * An easy and handy implementation of the ILoggerAdapter. The main advantages are that not the Log4J root logger is
 * used, so that other existing loggers that build upon the root logger don't interfere. Furthermore, a constructor were
 * a logger name can be passed, exists. On this way, several logger instances can be created instead of just one in
 * Log4JLoggerAdapterImpl. Appenders for the current logger will only be added, if none exist at creation time.
 *
 * @author imi2si
 * @since 1.3.0
 */
public class Log4JLoggerAdapterImpl implements ILoggerAdapter {

  /**
   * The default log pattern. Shows the date, name of the thread that generated the event, the category, the message and
   * a line separator
   */
  private static final String DEFAULT_LOG_PATTERN = "%d [%t] [%c] %-5p - %m%n";

  /**
   * The Default Name for a new Logger
   */
  private static final String DEFAULT_LOGGER_NAME = "Log4JLoggerAdapterImplExt";

  /**
   * The Default Temporary Directory
   */
  private static final String DEFAULT_TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator;

  /**
   * Log4j instance variable
   */
  private final Logger log4jLogger;

  /**
   * The logger name. Either the name passed in the constructor or the default name.
   */
  private final String loggerName;

  /**
   * The Layout (Format) of the logger
   */
  private PatternLayout patternLayout;

  /**
   * Creates a Log4JLoggerAdapterImplExt for the specified org.apache.logging.log4j.Logger instance. You can pass any
   * Log4J logger to this class by passing <code>LogManager.getLogger("loggerName")</code>, even if they are created
   * with an interface that is not compatible to ILoggerAdapter. The iCDM Loggers, which based on another interface, are
   * an example therefor. This constructor creates so to say a wrapper for the passed Logger, that makes it compatible
   * to the ILoggerAdapter interface.
   *
   * @param log4jLogger the Logger that should be used for this Log4JLoggerAdapterImplExt instance
   */
  public Log4JLoggerAdapterImpl(final Logger log4jLogger) {
    this.log4jLogger = log4jLogger;
    this.loggerName = log4jLogger.getName();
  }

  /**
   * Creates a Log4JLoggerAdapterImplExt with the given path, logger pattern and logger name. A good strategy for a user
   * defined name is using the plug-in name or class name.
   *
   * @param logFilePath the path including filename, where the file should be stored
   * @param loggerPattern the pattern of the logger output. The syntax of the pattern can be found in the description of
   *          the class {@link PatternLayout PatternLayout }
   * @param loggerName the user defined name of the logger
   */
  public Log4JLoggerAdapterImpl(final String logFilePath, final String loggerPattern, final String loggerName) {
    this.loggerName = loggerName;
    this.log4jLogger = LogManager.getLogger(loggerName);

    // Message, if existing logger properties file exists
    warnExistingLoggerProp();

    // CDMBC-2
    // DEFAULT_CONVERSION_PATTERN : "%m%n" which just prints the application supplied message
    this.patternLayout = PatternLayout.newBuilder().withPattern(PatternLayout.DEFAULT_CONVERSION_PATTERN).build();

    if ((loggerPattern != null) && (!loggerPattern.trim().isEmpty())) {
      this.patternLayout = PatternLayout.newBuilder().withPattern(loggerPattern).build();
    }

    LoggerConfig loggerConfig = getLoggerConfig(loggerName);
    try {
      if ((logFilePath != null) && !("").equals(logFilePath)) {

        if (isFileAppenderExisting(logFilePath)) {
          this.log4jLogger.debug(
              "File writer already exists for this logger. No additional File Appender will be created to avoid showing the same messages several times. (No action required).");
        }
        else {
          FileAppender fileAppender = FileAppender.newBuilder().setName("Log4JLoggerAdapterImpl_File")
              .withFileName(logFilePath).setLayout(this.patternLayout).build();
          if (fileAppender != null) {
            fileAppender.start();
            loggerConfig.addAppender(fileAppender, null, null);
          }
        }
      }
    }
    catch (Exception err) {
      this.log4jLogger.fatal("Could not instantiate the File logger" + " under path " + logFilePath, err); //$NON-NLS-1$
    }

    if (isConsoleAppenderExisting()) {
      this.log4jLogger.debug(
          "Console appender already exists for this logger. No additional Console Appender will be created to avoid showing the same messages several times. (No action required).");
    }
    else {
      ConsoleAppender consoleAppender =
          ConsoleAppender.newBuilder().setName("Log4JLoggerAdapterImpl_Console").setLayout(this.patternLayout).build();
      if (consoleAppender != null) {
        consoleAppender.start();
        loggerConfig.addAppender(consoleAppender, null, null);
      }
    }

    // This causes all Loggers to refetch information from their LoggerConfig
    getLoggerContext().updateLoggers();
  }

  /**
   * Creates a Log4JLoggerAdapterImplExt. Format for Console Output is set to DEFAULT_LOG_PATTERN and
   * DEFAULT_LOGGER_NAME.
   *
   * @param logFilePath the path of the file where the logfile should be stored
   */
  public Log4JLoggerAdapterImpl(final String logFilePath) {
    this(logFilePath, DEFAULT_LOG_PATTERN, DEFAULT_LOGGER_NAME);
  }


  /**
   * Creates a Log4JLoggerAdapterImplExt. Logger Name is set to DEFAULT Logger Name DEFAULT_LOGGER_NAME.
   *
   * @param logFilePath the path of the file where the logfile should be stored
   * @param loggerPattern the pattern (format) of the console appender
   */
  public Log4JLoggerAdapterImpl(final String logFilePath, final String loggerPattern) {
    this(logFilePath, loggerPattern, DEFAULT_LOGGER_NAME);
  }

  /**
   * @return LoggerContext
   */
  private LoggerContext getLoggerContext() {
    return (LoggerContext) LogManager.getContext(false);
  }

  /**
   * @param theLoggerName logger name
   * @return LoggerConfig
   */
  private LoggerConfig getLoggerConfig(final String theLoggerName) {
    return getLoggerContext().getConfiguration()
        .getLoggerConfig(theLoggerName == null ? DEFAULT_LOGGER_NAME : theLoggerName);
  }


  /**
   * Returns the users temp directory, detetcted by the environment variable TEMP. If this variable is not found,
   * C:\Temp\ is used as basic logger.
   *
   * @return the users Temp directory
   */
  public static String getUserTempDirectory() {
    String userTempDir = System.getProperty("java.io.tmpdir");

    if ((userTempDir == null) || (userTempDir.trim().isEmpty())) {
      userTempDir = DEFAULT_TEMP_DIR;
    }

    return userTempDir + "\\";
  }

  /**
   * Creates a new Defualt logger with the passed name. The Log File is stored in the users default temp directory.
   *
   * @param loggerName the name of the logger
   * @return a default ILoggerAdapter instance
   */
  public static ILoggerAdapter getDefaultLogger(final String loggerName) {
    return new Log4JLoggerAdapterImpl(getUserTempDirectory() + loggerName + ".log", DEFAULT_LOG_PATTERN, loggerName);
  }

  /**
   * Get the current Log4J Logger instance to be used in other components
   *
   * @return The current Log4J Logger instance
   */
  public final Logger getLog4jLogger() {
    return this.log4jLogger;
  }

  /**
   * To log error.
   *
   * @param error the error text
   */
  @Override
  public final void error(final String error) {
    this.log4jLogger.error(error);
  }

  /**
   * To log error with a message and a Throwable object
   *
   * @param error the error text
   * @param cause the casue (of the Java exception)
   */
  @Override
  public final void error(final String error, final Throwable cause) {
    this.log4jLogger.error(error, cause);
  }

  /**
   * To log fatal error
   *
   * @param fatal the error text
   */
  @Override
  public final void fatal(final String fatal) {
    this.log4jLogger.fatal(fatal);
  }

  /**
   * To log fatal with a message and a Throwable object
   *
   * @param fatal the error text
   * @param cause the casue (of the Java exception)
   */
  @Override
  public final void fatal(final String fatal, final Throwable cause) {
    this.log4jLogger.fatal(fatal, cause);
  }

  /**
   * To log information during debugging.
   *
   * @param info the text to be shown
   */
  @Override
  public final void debug(final String info) {
    this.log4jLogger.debug(info);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void debug(final String message, final Object... params) {
    this.log4jLogger.debug(message, params);
  }

  /**
   * To log information.
   *
   * @param info the text to be shown
   */
  @Override
  public final void info(final String info) {
    this.log4jLogger.info(info);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void info(final String message, final Object... params) {
    this.log4jLogger.info(message, params);
  }

  /**
   * To log warning.
   *
   * @param warn the warning text to be shown
   */
  @Override
  public final void warn(final String warn) {
    this.log4jLogger.warn(warn);
  }

  /**
   * To log warning with a message and a Throwable object
   *
   * @param warn the warning text to be shown
   * @param cause the casue (of the Java exception)
   */
  @Override
  public final void warn(final String warn, final Throwable cause) {
    this.log4jLogger.warn(warn, cause);
  }

  /**
   * Sets the log level
   *
   * @param level the log level. One of the values Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL
   */
  @Override
  public final void setLogLevel(final int level) {

    LoggerConfig logConfig = getLoggerConfig(getLoggerName());
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
   * Gets the log level
   *
   * @return int one of the values Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL
   */
  @Override
  public final int getLogLevel() {

    int level;

    switch (this.log4jLogger.getLevel().intLevel()) {
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
    return this.log4jLogger.isDebugEnabled();
  }

  /**
   * @return the loggerName
   */
  public final String getLoggerName() {
    return this.loggerName;
  }

  /**
   * Checks if there are already console appenders existing
   *
   * @return boolean true, if an appender is existing, otherwise false
   */
  private boolean isConsoleAppenderExisting() {
    return isConsoleAppenderExisting(getLoggerConfig(getLoggerName())) ||
        isConsoleAppenderExisting(getLoggerConfig(LogManager.ROOT_LOGGER_NAME));
  }


  /**
   * Checks if there are already console appenders existing for the given logger
   *
   * @param logger the logger that should be checked for appenders
   * @return boolean true, if an appender is existing, otherwise false
   */
  private boolean isConsoleAppenderExisting(final LoggerConfig loggerConfig) {
    final Collection<Appender> allAppenders = loggerConfig.getAppenders().values();
    for (Appender appender : allAppenders) {
      if (appender instanceof ConsoleAppender) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks, if there are already file appenders existing
   *
   * @param path the path that is checked for existance
   * @return boolean true, if an appender is existing, otherwise false
   */
  private boolean isFileAppenderExisting(final String path) {
    final Collection<Appender> allAppenders = getLoggerConfig(getLoggerName()).getAppenders().values();
    for (Appender appender : allAppenders) {
      if ((appender instanceof FileAppender) && ((FileAppender) appender).getFileName().equalsIgnoreCase(path)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Shows a warning if an propertie file is used for the current logger. The properties file may come from any plug in
   * project. Sometimes it's hard to figure out which plug in project has a properties file attached. This method shows
   * the currently used properties file.
   */
  private void warnExistingLoggerProp() {
    final URL urlProp = Loader.getResource("log4j.properties", null);
    if (urlProp != null) {
      debug(
          "There's an existing property logfile that might override settings of this Log4JLoggerAdapterImplExt. Path:   " +
              urlProp.getFile());
    }
  }
}
