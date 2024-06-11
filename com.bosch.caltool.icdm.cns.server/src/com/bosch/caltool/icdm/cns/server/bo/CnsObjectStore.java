/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.adapter.logger.util.Log4jLoggerUtil;

/**
 * @author bne4cob
 */
public final class CnsObjectStore {

  /**
   * Webservice Logger
   */
  private static ILoggerAdapter logger;

  private static Properties prop;

  /**
   * Static constructor to ensure, that the WebService uses only one logger and one instance of the implementation class
   */
  static {
    initialize();
  }

  private CnsObjectStore() {
    // Private constructor
  }

  /**
   *
   */
  private static void initialize() {
    initializeLogger();
    initializeServerProps();
  }

  private static void initializeLogger() {
    File log4jProperties = new File(System.getProperty("WebServiceConfPath") + "/CnsLogger.xml");

    if (log4jProperties.exists()) {
      // Configure log4j properties
      Log4jLoggerUtil.configureProperties(log4jProperties.getAbsolutePath());
    }

    // create a logger
    logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("ICDMCNS"));

    logger.info("CNS Server logger successfully created, log level: {}", getLogger().getLogLevel());
    logger.info("********************** Starting CNS Web Service ***************************");
  }

  /**
   * Sets the Resource Bundle to a file located in the file system by using the Java class UrlCLassLoader. Needed to set
   * the Resource Bundle location for the iCDM webservice. The filename of the Resource Bundle file is always
   * 'messages.properties'. The file name must not be added to the passed path.
   *
   * @param filePath the full qualified path to the resource bundle file. The file name must not be added to the path.
   */
  private static void initializeServerProps() {
    String propFile = System.getProperty("WebServiceConfPath") + "/CnsServerConfig.properties";
    try (Reader reader = new FileReader(propFile)) {
      prop = new Properties();
      prop.load(reader);
      getLogger().info("Server properties loaded succesfully from {}", propFile);
    }
    catch (IOException e) {
      CnsObjectStore.getLogger().error("Failed to load properties : " + e.getMessage(), e);
    }
  }

  /**
   * @return the logger
   */
  public static ILoggerAdapter getLogger() {
    return logger;
  }

  /**
   * @param key property key
   * @return property
   */
  public static String getServerProperty(final String key) {
    String val = prop.getProperty(key);
    if (val == null) {
      throw new IllegalArgumentException("Invalid server property - " + key);
    }
    return val;
  }


}
