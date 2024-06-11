/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.eclipselink.logging;


import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.testframe.TestUtils;

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
  public void customize(final Session session) {

    // Create and set the custom session log to the jpa session
    final SessionLog sessionLog = new EclipseLinkSessionLog();
    sessionLog.setLevel(getEclLogLevel());

    session.setSessionLog(sessionLog);
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
    int eclLevel;

    switch (TestUtils.getJpaLogger().getLogLevel()) {

      case ILoggerAdapter.LEVEL_DEBUG:
        eclLevel = SessionLog.ALL;
        break;

      case ILoggerAdapter.LEVEL_INFO:
        eclLevel = SessionLog.FINE;
        break;

      case ILoggerAdapter.LEVEL_WARN:
        eclLevel = SessionLog.WARNING;
        break;

      case ILoggerAdapter.LEVEL_ERROR:
      case ILoggerAdapter.LEVEL_FATAL:
        eclLevel = SessionLog.SEVERE;
        break;

      default:
        eclLevel = SessionLog.FINE;
        break;
    }

    return eclLevel;
  }
}
