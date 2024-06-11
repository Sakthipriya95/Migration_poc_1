/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.adapter.logger.util;

import java.io.File;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * @author bne4cob
 */
public final class Log4jLoggerUtil {

  private Log4jLoggerUtil() {
    // Private constructor
  }

  /**
   * IMPORTANT : For RCP env, use the method with 'ClassLoader' input
   * <p>
   * Configure the properties
   *
   * @param configFilePath Config file path
   */
  public static void configureProperties(final String configFilePath) {
    File file = new File(configFilePath);
    configureProperties(file);
  }

  /**
   * Configure the properties
   *
   * @param classLoader class loader : applicable in Eclipse RCP environments
   * @param configFilePath Config file path
   */
  public static void configureProperties(final ClassLoader classLoader, final String configFilePath) {
    File file = new File(configFilePath);
    configureProperties(classLoader, file);
  }

  /**
   * IMPORTANT : For RCP env, use the method with 'ClassLoader' input
   * <p>
   * Configure the properties
   *
   * @param configFile Config file
   */
  public static void configureProperties(final File configFile) {
    configureProperties(null, configFile);
  }

  /**
   * Configure the properties
   *
   * @param classLoader class loader : applicable in Eclipse RCP environments
   * @param configFile Config file
   */
  public static void configureProperties(final ClassLoader classLoader, final File configFile) {
    LoggerContext context = (LoggerContext) LogManager.getContext(classLoader, false);
    context.setConfigLocation(configFile.toURI());
  }

  /**
   * @param properties configure the properties using the input
   */
  public static void configureProperties(final Properties properties) {
    // TODO is this possible?
    throw new RuntimeException("Not implemented yet");
  }

  /**
   * Method to get Logger Context
   *
   * @param classLoader class loader : applicable in Eclipse RCP environments
   * @return LoggerContext
   */
  public static LoggerContext getLoggerContext(final ClassLoader classLoader) {
    return getLoggerContext(classLoader, new File(""));
  }


  /**
   * Method to get Logger Context
   *
   * @param classLoader class loader : applicable in Eclipse RCP environments
   * @param configFile Config file
   * @return LoggerContext
   */
  public static LoggerContext getLoggerContext(final ClassLoader classLoader, final File configFile) {
    return (LoggerContext) LogManager.getContext(classLoader, false, configFile);
  }

}
