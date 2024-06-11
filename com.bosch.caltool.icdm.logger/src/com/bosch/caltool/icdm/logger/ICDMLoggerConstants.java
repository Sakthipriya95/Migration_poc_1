package com.bosch.caltool.icdm.logger;

/**
 * This class provides Log file constants
 */
public final class ICDMLoggerConstants {

  /**
   * Error logger - default plugin name
   */
  public static final String DEFAULT_PLUGIN_NAME_ERROR_LOG = "iCDM Client";

  /**
   * Default log file sub-directory
   */
  public static final String DEFAULT_LOG_FILE_SUBDIR = "iCDM";

  /**
   * Default log file name
   */
  public static final String DEFAULT_LOG_FILE_NAME = "iCDM.log";

  // iCDM-340
  /**
   * Dialog titles
   */
  /**
   * Error dialog title
   */
  public static final String DIALOG_TITLE_ERROR = "iCDM Error";

  /**
   * Warning dialog title
   */
  public static final String DIALOG_TITLE_WARN = "iCDM Warning";

  /**
   * Information dialog title
   */
  public static final String DIALOG_TITLE_INFO = "iCDM Information";

  /*
   * Different logger names used in in iCDM datamodel, services and client layers
   */

  /**
   * Logger for ICDM related functions
   */
  public static final String ICDM_LOGGER = "ICDM";
  /**
   * Logger for A2L related operations
   */
  public static final String A2L_LOGGER = "A2L";
  /**
   * Logger for EASEE operations
   */
  public static final String EASEE_LOGGER = "EASEE";
  /**
   * Defines Parser logger info
   */
  public static final String PARSER_LOGGER = "PARSER";
  /**
   * Defines SSD logger info
   */
  public static final String SSD_LOGGER = "SSD";
  /**
   * Defines Check SSD logger
   */
  public static final String CHECK_SSD_LOGGER = "CHECKSSD";
  /**
   * Logger for JPA operations
   */
  public static final String JPA_LOGGER = "JPA";
  /**
   * CDF Logger
   */
  public static final String CDFWRITER_LOGGER = "CDFWRITER";
  /**
   * CNS Client Logger
   */
  public static final String CNS_CLIENT_LOGGER = "CNSCLIENT";

  /**
   * A2L Loader logger
   */
  public static final String A2L_LDR_LOGGER = "A2LLOADER";

  /**
   * CalDataAnalyzer logger
   */
  public static final String CDA_LOGGER = "CDA";

  /**
   * LDAP logger
   */
  public static final String LDAP_LOGGER = "LDAP";

  /**
   * 2FA Checker logger
   */
  public static final String TWO_FA_LOGGER = "2FAChecker";


  /**
   * private constructor to hide implicit public one
   */
  private ICDMLoggerConstants() {
    // Nothing to be done
  }


}
