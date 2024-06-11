/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.util;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.adapter.logger.util.Log4jLoggerUtil;

/**
 * @author bne4cob
 */
public class ToolLogger {

  /**
   * Tool Logger
   */
  private static ILoggerAdapter logger;

  /**
   * JPA Logger
   */
  private static ILoggerAdapter jpaLogger;

  /**
   * Logger creation
   */
  public static void createLogger() {

    String logFileName = "C:/Temp/ToolLogger.log";

    File logFile = new File(logFileName);

    if (logFile.exists()) {
      logFile.delete();
    }

    Properties prop = new Properties();
    // Default Level - Error
    prop.put("log4j.logger.JPA", "ERROR");

    // Rolling file logging
    prop.put("log4j.appender.FILE", "org.apache.log4j.RollingFileAppender");
    prop.put("log4j.appender.FILE.File", logFileName);
    prop.put("log4j.appender.FILE.MaxFileSize", "2MB");
    prop.put("log4j.appender.FILE.MaxBackupIndex", "5");
    prop.put("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout ");
    prop.put("log4j.appender.FILE.layout.ConversionPattern", "%d [%t] [%c] %-5p - %m%n");

    // Console Logging
    prop.put("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
    prop.put("log4j.appender.CONSOLE.Target", "System.out");
    prop.put("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
    prop.put("log4j.appender.CONSOLE.layout.ConversionPattern", "%d [%t] [%c] %-5p - %m%n");

    // Log Levels
    prop.put("log4j.rootLogger", "DEBUG,FILE,CONSOLE");

    // Level set to avoid NPE from Log4JLoggerAdapter.getLevel() method.
    prop.put("log4j.logger.PROJEVALTOOL", "INFO");
    prop.put("log4j.logger.JPA", "WARN");

    Log4jLoggerUtil.configureProperties(prop);
    // Applicatin logger
    logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("UtilLogger"));

    // JPA Logger
    jpaLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("JPA"));
  }


  /**
   * @return the logger
   */
  public static ILoggerAdapter getLogger() {
    return logger;
  }


  /**
   * @return the jpaLogger
   */
  public static ILoggerAdapter getJpaLogger() {
    return jpaLogger;
  }


}
