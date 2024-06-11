package com.bosch.calcomp.adapter.logger;


/**
 * REVISION HISTORY<br>
 * Version Date Name Description<br>
 * 0.1 05-Jun-2008 Deepa SAC-82 First Draft<br>
 * 0.2 12-Jun-2008 Deepa Removed the console logging in setFileLogger()<br>
 * 0.3 23-Jun-2008 Madhu Samuel K <SAC-82>: Removed all concreate methods to PacoParser.<br>
 * Converted the abstract class to Interface.<br>
 * 0.4 24-Jun-2008 Madhu Samuel K <SAC-82> Renamed this interface to ILoggerAdapter from<br>
 * PacoParserLogger.<br>
 * 0.5 25-Nov-2011 Anand Removed unused packages <br>
 */

/**
 * ILoggerAdapter
 */
public interface ILoggerAdapter {

  /**
   * DEBUG log level
   */
  public static final int LEVEL_DEBUG = 1;
  /**
   * INFO log level
   */
  public static final int LEVEL_INFO = 2;
  /**
   * WARN log level
   */
  public static final int LEVEL_WARN = 3;
  /**
   * ERROR log level
   */
  public static final int LEVEL_ERROR = 4;
  /**
   * FATAL log level
   */
  public static final int LEVEL_FATAL = 5;

  /**
   * To set LOG level
   *
   * @param level the LOG level to set
   */
  public abstract void setLogLevel(int level);

  /**
   * To get LOG level
   *
   * @return int
   */
  public abstract int getLogLevel();

  /**
   * Checks whether the debug level logging is enabled or not.
   *
   * @return true, if log level is 'DEBUG'
   */
  public boolean isDebugEnabled();

  /**
   * To log information.
   *
   * @param info info message
   */
  public abstract void info(String info);

  /**
   * To log info message
   * <p>
   * Sample usage : <br>
   * <code>myLogger.debug("This logger prints messages with Level "{}" or '{}'", "Debug", "Info");</code> <br>
   * will print<br>
   * <code>This logger prints messages with Level "Debug" or 'Info'</code>
   *
   * @param message message to log, with placeholders
   * @param params objects to be placed inside the message
   */
  public abstract void info(String message, Object... params);

  /**
   * To log warning.
   *
   * @param warning warning message
   */
  public abstract void warn(String warning);

  /**
   * To log warning with a message and a Throwable object.
   *
   * @param warning warning message
   * @param cause the error cause
   */
  public abstract void warn(String warning, Throwable cause);

  /**
   * To log error.
   *
   * @param error error message
   */
  public abstract void error(String error);

  /**
   * To log error with a message and a Throwable object.
   *
   * @param error error message
   * @param cause the error cause
   */
  public abstract void error(String error, Throwable cause);

  /**
   * To log fatal error.
   *
   * @param fatal fatal message
   */
  public abstract void fatal(String fatal);

  /**
   * To log fatal with a message and a Throwable object.
   *
   * @param fatal fatal message
   * @param cause the error cause
   */
  public abstract void fatal(String fatal, Throwable cause);

  /**
   * To log debug message.
   *
   * @param info debug message
   */
  public abstract void debug(String info);

  /**
   * To log debug message.
   * <p>
   * Sample usage : <br>
   * <code>myLogger.debug("This logger prints messages with Level "{}" or '{}'", "Debug", "Info");</code> <br>
   * will print<br>
   * <code>This logger prints messages with Level "Debug" or 'Info'</code>
   *
   * @param message message to log, with placeholders
   * @param params objects to be placed inside the message
   */
  public abstract void debug(String message, Object... params);


}
