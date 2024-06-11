/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.db;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.logger.WSLogger;


/**
 * Stores web service related common objects
 *
 * @author BNE4COB
 */
public final class WSObjectStore {

  /**
   * Webservice Logger
   */
  private static ILoggerAdapter logger;

  /**
   * ICDM Data access
   */
  private static ApicWebServiceDBImpl apicWebServiceDBImpl;

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
    // create a logger
    logger = WSLogger.getInstance();

    // create the database interface class
    apicWebServiceDBImpl = new ApicWebServiceDBImpl(logger);

  }

  /**
   * Private constructor, to prevent instantiation from outside
   */
  private WSObjectStore() {
    // No code here
  }

  /**
   * @return the logger
   */
  public static ILoggerAdapter getLogger() {
    return logger;
  }


  /**
   * @return the apicWebServiceDBImpl
   */
  public static ApicWebServiceDBImpl getApicWebServiceDBImpl() {
    return apicWebServiceDBImpl;
  }


}
