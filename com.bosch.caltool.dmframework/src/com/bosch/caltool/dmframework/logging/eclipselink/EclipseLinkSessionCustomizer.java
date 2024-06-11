/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.logging.eclipselink;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;

/**
 * Session customizer. Forwards the JPA log messages to the application logger
 *
 * @author bne4cob
 */
public class EclipseLinkSessionCustomizer implements SessionCustomizer {

  /**
   * Use the custom jpa logger.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void customize(final Session aSession) {

    // Create and set the custom session log to the jpa session
    final SessionLog sessionLog = new EclipseLinkSessionLog(ObjectStore.getInstance().getJPALogger());
    sessionLog.setLevel(getEclLogLevel());

    aSession.setSessionLog(sessionLog);
  }

  /**
   * Returns the appropriate the Session log level equivalent to the Logger level.Mapping between log levels is as given
   * below
   * <p>
   * ILoggerAdapter Level | SessionLog Level<br>
   * LEVEL_DEBUG | ALL<br>
   * LEVEL_INFO | FINE<br>
   * LEVEL_WARN | WARNING<br>
   * LEVEL_ERROR| SEVERE<br>
   * LEVEL_FATAL| SEVERE <br>
   * Default | FINE
   *
   * @return eclipse link log level
   */
  private int getEclLogLevel() {

    int logLevel;
    switch (ObjectStore.getInstance().getJPALogger().getLogLevel()) {

      case ILoggerAdapter.LEVEL_DEBUG:
        logLevel = SessionLog.ALL;
        break;
      // If it is a warning
      case ILoggerAdapter.LEVEL_WARN:
        logLevel = SessionLog.WARNING;
        break;
      case ILoggerAdapter.LEVEL_ERROR:
      case ILoggerAdapter.LEVEL_FATAL:
        logLevel = SessionLog.SEVERE;
        break;
      case ILoggerAdapter.LEVEL_INFO:
      default:
        logLevel = SessionLog.FINE;
        break;
    }
    return logLevel;
  }
}
