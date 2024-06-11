/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.oslc.ws.db;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.logger.CDMLoggerUtil;
import com.bosch.caltool.icdm.logger.WSLogger;

/**
 * @author mkl2cob
 */
public final class OSLCObjectStore {

  /**
   * Webservice Logger
   */
  private static ILoggerAdapter logger;
  /**
   * OSLCWebServiceDBImpl
   */
  private static OSLCWebServiceDBImpl oslcWebServiceDBImpl;


  /**
   * Static constructor to ensure, that the WebService uses only one logger and one instance of the implementation class
   */
  static {
    createLoggerAndDbImpl();
  }

  /**
   * moved the logic here to be called if dcn refesh fails
   */
  public static void createLoggerAndDbImpl() {
    // Init the right message.properties file

    CDMLoggerUtil.initialise("WebServiceConfPathOslc");

    com.bosch.caltool.icdm.common.util.messages.Messages
        .setResourceBundleFile(System.getProperty("WebServiceConfPathOslc"));

    // create a logger
    logger = WSLogger.getInstance();
    logger.info("ICDM OSLC WS logger successfully created, log level: {}", logger.getLogLevel());
    logger.info("********************** Starting ICDM OSLC Web Service ***************************");

    // create the database interface class
    try {
      oslcWebServiceDBImpl = new OSLCWebServiceDBImpl(logger);
    }
    catch (IcdmException e) {
      logger.error("Error when initializing Server", e);
    }
  }

  /**
   * Private constructor, to prevent instantiation from outside
   */
  private OSLCObjectStore() {
    // No code here
  }

  /**
   * @return the logger
   */
  public static ILoggerAdapter getLogger() {
    return logger;
  }

  /**
   * @return the oslcWebServiceDBImpl
   */
  public static OSLCWebServiceDBImpl getOslcWebServiceDBImpl() {
    return oslcWebServiceDBImpl;
  }


}
