/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.constants;


/**
 * Constant Class that contains all common constants used with the component
 *
 * @author SSN9COB
 */
public final class SSDiCDMInterfaceConstants {


  /**
   * Plugin ID
   */
  public static final String PLUGIN_ID = "com.bosch.ssd.icdm.jpa";
  /**
   * Exception Constant: General Scenarios
   */
  public static final String EXCEPTION_GENERAL = " Generic Exception";
  /**
   * Exception Constant: Input Related Scenarios
   */
  public static final String EXCEPTION_INVALID_INPUT = " Input Related Exception";
  /**
   * Exception Constant: Database Related Scenarios
   */
  public static final String EXCEPTION_DATABASE = " Database Related Exception";
  /**
   * Exception Constant: Error Code String
   */
  public static final String EXCEPTION_ERROR_CODE = "SSD iCDM Interface Error Code: ";
  /**
   * Exception Constant: String login credentials
   */
  public static final String EXCEPTION_MESSAGE_LOGIN_CREDENTIALS =
      "Please provide login credentials/Entity Manager Connection";
  /**
   * Exception Constant: Entity Manager not set
   */
  public static final String EXCEPTION_MESSAGE_EM_NOT_SET = "Entity Manager is not set. Please create Entity Manager";
  /**
   * General String Constant: Delimiter Hifen
   */
  public static final String DELIMITER_HIFEN = " - ";
  /**
   * Database String Constant: Key for DB INstance from Property file
   */
  public static final String DATABASE_PROPERTY_KEY = "database";
  /**
   * Database String Constant: JDBC Property URL
   */
  public static final String DATABASE_JDBC_PERSISTENCE_URL_PROPERTY = "javax.persistence.jdbc.url";
  /**
   * Database String Constant: Connection String prefix
   */
  public static final String DATABASE_CONNECTION_JDBC_PREFIX = "jdbc:oracle:thin:@";
  /**
   * Database String Constant: JDBC Property Username
   */
  public static final String DATABASE_JDBC_PERSISTENCE_PSWD_PROPERTY = "javax.persistence.jdbc.password";
  /**
   * Database String Constant: JDBC Property password
   */
  public static final String DATABASE_JDBC_PERSISTENCE_USER_PROPERTY = "javax.persistence.jdbc.user";
  /**
   *
   */
  public static final String DOUBLE_SLASH = "\\.";
  /**
   *
   */
  public static final String REGEX_PATTERN = "[^a-zA-Z_0-9.]";

  /**
   * to check if the label is variant coded
   */
  public static final String VARCODE_SYM = "[";

  /**
   * Parameters of value type
   */
  public static final String VALUE = "VALUE";

  /**
   * Parameters of CURVE type
   */
  public static final String CURVE = "CURVE";

  /**
   * Parameters of VAL_BLK type
   */
  public static final String VAL_BLK = "VAL_BLK";

  /**
   * Parameters of MAP type
   */
  public static final String MAP = "MAP";

  /**
   * Parameters of ASCII type
   */
  public static final String ASCII = "ASCII";


  /**
   * Parameters of AXIS_PTS type
   */
  public static final String AXIS_PTS = "AXIS_PTS";


  private SSDiCDMInterfaceConstants() {
    // default private constructor
  }
}
