/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe;


/**
 * Defines the common properties for using test frame
 * 
 * @author bne4cob
 */
public final class CommonProps {

  /**
   * Log file path
   */
  public static final String LOG_FILE_PATH = "c:/temp/";

  /**
   * Logger pattern
   */
  public static final String LOGGER_PATTERN = "%d [%t] [%c] %-5p - %m%n";

  /**
   * Database user
   */
  public static final String DB_SERVER_URL = "jdbc:oracle:thin:@rb-engsrv32.de.bosch.com:38550:dgstest";

  /**
   * Database user
   */
  public static final String DB_USER = "DGS_ICDM_JPA";

  /**
   * Password of the database user
   */
  public static final String DB_PASSWORD = "DGS_ICDM_JPA";

  /**
   * Default application user
   */
  public static final String DEF_APP_USER = "DGS_ICDM";

  /**
   * Default exception message
   */
  public static final String DEF_EXPTN_MSG = "Exception occured";

  /**
   * Private constructor
   */
  private CommonProps() {
    // Private constructor
  }

}
