/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.junittestframework;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;


/**
 * @author BNE4COB
 */
public class JUnitTest {

  // Initialize Log4j.
  // Remove existing all appenders and add only console appender
  // Log level is DEBUG
  static {
    LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
    Configuration configuration = loggerContext.getConfiguration();
    LoggerConfig loggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
    loggerConfig.getAppenders().values().stream().forEach(appender -> loggerConfig.removeAppender(appender.getName()));

    PatternLayout patternLayout = PatternLayout.newBuilder().withPattern("%d [%t] [%c] %-5p - %m%n").build();
    ConsoleAppender consoleAppender =
        ConsoleAppender.newBuilder().setName("JUnitTest_Console").setLayout(patternLayout).build();
    consoleAppender.start();
    loggerConfig.addAppender(consoleAppender, null, null);

    // Set default log level as DEBUG
    loggerConfig.setLevel(Level.DEBUG);
    loggerContext.updateLoggers();
  }

  /**
   * Logger for Tests
   */
  protected static final ILoggerAdapter TESTER_LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger("TESTER"));
  static {
    TESTER_LOGGER.setLogLevel(ILoggerAdapter.LEVEL_DEBUG);
  }

  /**
   * Logger for application/component under test
   */
  protected static final ILoggerAdapter AUT_LOGGER = new Log4JLoggerAdapterImpl(LogManager.getLogger("AUT"));
  static {
    AUT_LOGGER.setLogLevel(ILoggerAdapter.LEVEL_DEBUG);
  }

  /**
   * Exec ID
   */
  private static final String EXEC_ID = String.valueOf(System.currentTimeMillis());

  /**
   * Temp directory to be used for tests
   */
  protected static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "Tests" + File.separator;
  static {
    new File(TEMP_DIR).mkdir();
  }

  /**
   * Test method name retrieval
   */
  @Rule
  public final TestName testMethodName = new TestName();

  /**
   * Expected exception
   */
  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  /**
   * Start time of the test (milli seconds)
   */
  private long startTime;

  /**
   * Prints test start
   */
  @Before
  public void logTestNameBefore() {
    TESTER_LOGGER.debug("======================================================");
    TESTER_LOGGER.debug("Test : {}", this.testMethodName.getMethodName());
    TESTER_LOGGER.debug("------------------------------------------------------");

    this.startTime = System.currentTimeMillis();
  }

  /**
   * print test end
   */
  @After
  public void logTestNameAfter() {
    TESTER_LOGGER.debug("Test : {} completed. Time taken = {} ms", this.testMethodName.getMethodName(),
        (System.currentTimeMillis() - this.startTime));
    TESTER_LOGGER.debug("------------------------------------------------------");
  }

  /**
   * @return current run ID
   */
  protected static String getRunId() {
    return EXEC_ID;
  }

}