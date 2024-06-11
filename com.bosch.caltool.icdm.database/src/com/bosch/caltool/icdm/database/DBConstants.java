/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database;

import java.io.File;

import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author bne4cob
 */
// ICDM-2475
public final class DBConstants {

  /**
   * JPA Log path
   */
  public static final String JPA_LOG_PATH =
      CommonUtils.getICDMLogDirectoryDefaultPath() + File.separator + "ICDM_JPA.log";

  /**
   * JDBC User key for JPA configuration
   */
  public static final String JPA_JDBC_USER = "javax.persistence.jdbc.user";

  /**
   * JDBC User's password key for JPA configuration
   */
  public static final String JPA_JDBC_PASS = "javax.persistence.jdbc.password";

  /**
   * JDBC URL key for JPA configuration
   */
  public static final String JPA_JDBC_URL = "javax.persistence.jdbc.url";

  /**
   * JDBC Driver key for JPA configuration
   */
  public static final String JPA_JDBC_DRIVER = "javax.persistence.jdbc.driver";

  /**
   * JPA configuration logging file key
   */
  public static final String JPA_LOGGING = "eclipselink.logging.file";

  /**
   * JDBC Driver
   */
  public static final String JPA_ORA_DRIVER = "oracle.jdbc.OracleDriver";

  /**
   * JPA Persistence group for iCDM
   */
  public static final String ICDM_JPA_PERSISTANCE_NAME = "com.bosch.caltool.icdm.database";

  /**
   * Message for invalid database credentials
   */
  public static final String AUTH_FAILED_INVALID_CRED = "Invalid database credentials!!";

  /**
   * Messages for missing properties in JPA config
   */
  public static final String JPA_PROPERTIES_MISSING = "One or more values missing from the properties file";

  /**
   * Query Hint key for read only property
   */
  public static final String READ_ONLY = "eclipselink.read-only";

  /**
   * Query Hint key for fetch size property
   */
  public static final String FETCH_SIZE = "eclipselink.jdbc.fetch-size";

  /**
   * JPA cache store mode key
   */
  public static final String STORE_MODE = "javax.persistence.cache.storeMode";

  /**
   * JPA shared cache key
   */
  public static final String SHARED_CACHE = "eclipselink.cache.shared.default";

  /**
   * Private constructor for utility class
   */
  private DBConstants() {
    // Private constructor for utility class
  }

}
