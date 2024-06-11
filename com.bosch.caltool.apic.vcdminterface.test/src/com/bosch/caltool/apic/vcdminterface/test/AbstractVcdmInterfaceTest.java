/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.vcdminterface.test;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.bosch.calcomp.junittestframework.JUnitTest;

/**
 * @author BNE4COB
 */
public abstract class AbstractVcdmInterfaceTest extends JUnitTest {

  // Initialize Log4j
  static {
    // Set level of non-relevant loggers to WARN
    getLoggerConfig("org.apache").setLevel(Level.WARN);
    getLoggerConfig("httpclient").setLevel(Level.WARN);
    getLoggerConfig("A2L").setLevel(Level.WARN);

    getLoggerConfig("EASEE").setLevel(Level.INFO);
    getLoggerConfig("TESTER").setLevel(Level.DEBUG);
  }

  /**
   * @return LoggerContext
   */
  private static LoggerContext getLoggerContext() {
    return (LoggerContext) LogManager.getContext(false);
  }

  /**
   * @param theLoggerName logger name
   * @return LoggerConfig
   */
  private static LoggerConfig getLoggerConfig(final String theLoggerName) {
    return getLoggerContext().getConfiguration().getLoggerConfig(theLoggerName);
  }
}
