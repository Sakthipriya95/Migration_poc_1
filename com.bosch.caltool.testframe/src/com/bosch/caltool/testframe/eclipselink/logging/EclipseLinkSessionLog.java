/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.eclipselink.logging;


import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

import com.bosch.caltool.testframe.TestUtils;

/**
 * Uses the logger provided by TestUtils class for logging JPA level messages.
 * <p>
 * Log level associations given below
 * <p>
 * EclispeLink level | ILoggerAdapter Level <br>
 * ----------------- | ---------------------<br>
 * SessionLog.OFF | No logging<br>
 * SessionLog.SEVERE | Error<br>
 * SessionLog.WARNING | Warn<br>
 * SessionLog.INFO | Info<br>
 * SessionLog.CONFIG | Info<br>
 * SessionLog.FINE | Info<br>
 * SessionLog.FINER | Debug<br>
 * SessionLog.FINEST | Debug<br>
 * SessionLog.ALL | Debug<br>
 * 
 * @author bne4cob
 */
public class EclipseLinkSessionLog extends AbstractSessionLog implements SessionLog {

  /**
   * Uses the custom JPA logger
   * 
   * @see org.eclipse.persistence.logging.AbstractSessionLog#log(org.eclipse.persistence.logging.SessionLogEntry)
   */
  @Override
  public void log(final SessionLogEntry sessionLogEntry) {
    switch (sessionLogEntry.getLevel()) {
      case SessionLog.OFF:
        break;

      case SessionLog.SEVERE:
        logError(sessionLogEntry);
        break;

      case SessionLog.WARNING:
        logWarn(sessionLogEntry);
        break;

      case SessionLog.INFO:
      case SessionLog.CONFIG:
      case SessionLog.FINE:
        logInfo(sessionLogEntry);
        break;

      case SessionLog.FINER:
      case SessionLog.FINEST:
      case SessionLog.ALL:
      default:
        logDebug(sessionLogEntry);

    }

  }

  /**
   * Log error message
   * 
   * @param sesLogEntry Session Log Entry
   */
  private void logError(final SessionLogEntry sesLogEntry) {
    if (!shouldLog(sesLogEntry.getLevel(), sesLogEntry.getNameSpace())) {
      return;
    }
    if (sesLogEntry.hasException() && shouldLogExceptionStackTrace()) {
      TestUtils.getJpaLogger().error(format(sesLogEntry), sesLogEntry.getException());
    }
    else {
      TestUtils.getJpaLogger().error(format(sesLogEntry));
    }
  }

  /**
   * Log warn message
   * 
   * @param sesLogEntry Session Log Entry
   */
  private void logWarn(final SessionLogEntry sesLogEntry) {
    if (!shouldLog(sesLogEntry.getLevel(), sesLogEntry.getNameSpace())) {
      return;
    }
    if (sesLogEntry.hasException() && shouldLogExceptionStackTrace()) {
      TestUtils.getJpaLogger().warn(format(sesLogEntry), sesLogEntry.getException());
    }
    else {
      TestUtils.getJpaLogger().warn(format(sesLogEntry));
    }
  }

  /**
   * Log info message
   * 
   * @param sesLogEntry Session Log Entry
   */
  private void logInfo(final SessionLogEntry sesLogEntry) {
    if (!shouldLog(sesLogEntry.getLevel(), sesLogEntry.getNameSpace())) {
      return;
    }
    // Logger does not support writing exceptions in debug/info levels
    TestUtils.getJpaLogger().info(format(sesLogEntry));
  }

  /**
   * Log debug message
   * 
   * @param sesLogEntry Session Log Entry
   */
  private void logDebug(final SessionLogEntry sesLogEntry) {
    if (!shouldLog(sesLogEntry.getLevel(), sesLogEntry.getNameSpace())) {
      return;
    }
    // Logger does not support writing exceptions in debug/info levels
    TestUtils.getJpaLogger().debug(format(sesLogEntry));

  }

  /**
   * Format the log message
   * 
   * @param logEntry Session log entry
   * @return the formatted string
   */
  private String format(final SessionLogEntry logEntry) {
    final StringBuilder retStr = new StringBuilder();
    synchronized (this) {
      if (logEntry.hasMessage()) {
        retStr.append(formatMessage(logEntry));
      }

      if (logEntry.hasException() && !shouldLogExceptionStackTrace()) {
        // if stack trace is to be logged, it is passed to logger directly
        retStr.append(logEntry.getException().toString());
      }
    }

    return retStr.toString();
  }

}