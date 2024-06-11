package com.bosch.caltool.dmframework.logging.eclipselink;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;

/**
 * @author bne4cob
 */
public class EclipseLinkSessionLog extends AbstractSessionLog implements SessionLog {

  /**
   * Logger
   */
  private final ILoggerAdapter logger;

  /**
   * Constructor
   * 
   * @param jpaLogger logger
   */
  public EclipseLinkSessionLog(final ILoggerAdapter jpaLogger) { // NOPMD by bne4cob on 2/24/14 3:12 PM
    this.logger = jpaLogger;
  }

  /*
   * @see org.eclipse.persistence.logging.AbstractSessionLog#log(org.eclipse.persistence.logging.SessionLogEntry)
   */
  /**
   * {@inheritDoc}
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
      this.logger.error(format(sesLogEntry), sesLogEntry.getException());
    }
    else {
      this.logger.error(format(sesLogEntry));
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
      this.logger.warn(format(sesLogEntry), sesLogEntry.getException());
    }
    else {
      this.logger.warn(format(sesLogEntry));
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
    this.logger.info(format(sesLogEntry));
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
    this.logger.debug(format(sesLogEntry));

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