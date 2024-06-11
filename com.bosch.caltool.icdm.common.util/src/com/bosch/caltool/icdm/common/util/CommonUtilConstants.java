package com.bosch.caltool.icdm.common.util;

/**
 * @author dmo5cob
 */
public final class CommonUtilConstants {

  /**
   * Boolean mode - yes or no
   *
   * @author bne4cob
   */
  public enum BOOLEAN_MODE {

                            /**
                             * Yes
                             */
                            YES("Y"),

                            /**
                             * No
                             */
                            NO("N");

    final String binaryValue;

    BOOLEAN_MODE(final String binaryValue) {
      this.binaryValue = binaryValue;
    }

    /**
     * @return ParticipantType
     */
    public final String getBinaryValue() {
      return this.binaryValue;
    }

  }

  /**
   * CDF Extn
   */
  public static final String CDF_EXTN = ".CDF";
  /**
   * CDFX Extn
   */
  public static final String CDFX_EXTN = ".CDFX";
  /**
   * PACO Extn
   */
  public static final String PACO_EXTN = ".XML";

  /**
   * DCM Extn
   */
  public static final String DCM_EXTN = ".DCM";

  /**
   * HEX Extn (Intel)
   */
  public static final String HEX_EXTN = ".HEX";

  /**
   * HEX Extn 32 (Intel)
   */
  public static final String HEX_32_EXTN = ".H32";

  /**
   * HEX Extn for S19 files (Motorola)
   */
  public static final String HEX_S19_EXTN = ".S19";

  /**
   * CRETA Extn
   */
  public static final String CRETA_EXTN = ".CSV";
  /**
   * FTE Extn
   */
  public static final String FTE_EXTN = ".FTE";
  /**
   * windows OS sub-folders in file path limit
   */
  public static final int WINDOWS_FILEPATH_SUBFOLDERS_LIMIT = 247;
  /**
   * windows OS total file path limit
   */
  public static final int WINDOWS_TOTAL_FILEPATH_LIMIT = 259;
  /**
   * Invalid characters of windows should not used with filepath
   */
  protected static final Character[] INVALID_WINDOWS_FILENAME_CHARS = { '"', '*', ':', '<', '>', '?', '\\', '/', '|' };

  /**
   * DB user password
   */
  public static final String COMMON_UTILS_DB_USER_PASS = "CommonUtils.DB_USER_PASS";

  /**
   * password service server
   */
  public static final String PASSWRD_SERVER = "PASSWORD_SERVER";

  /**
   * Vcdm user name
   */
  public static final String EASEE_SERVICE_USER_NAME = "EASEEService.USER_NAME";
  /**
   * Vcdm user password
   */
  public static final String EASEE_SERVICE_USER_PASS = "EASEEService.USER_PASS";
  /**
   * Vcdm domain
   */
  public static final String EASEE_SERVICE_DOMAIN_NAME = "EASEEService.DOMAAIN_NAME";
  /**
   * Vcdm ws server
   */
  public static final String EASEE_SERVICE_WS_SERVER = "EASEEService.WS_SERVER";
  /**
   * VILLA user
   */
  public static final String VILLA_DB_USER = "Villa.DB_USER";
  /**
   * VILLA db url
   */
  public static final String VILLA_DB_URL = "Villa.DB_URL";
  /**
   * VILLA user pass
   */
  public static final String VILLA_DB_USER_PASS = "Villa.DB_USER_PASS";
  /**
   * Property name for application mode
   */
  public static final String APP_MODE_PROP_NAME = "applicationMode";

  /**
   * Application mode type - Web service
   */
  public static final String APP_MODE_TYPE_WBSRVCE = "WebService";
  /**
   * constant for xls extension
   */
  public static final String XLS_EXTN = ".XLS";
  /**
   * constant for xlsx extension
   */
  public static final String XLSX_EXTN = ".XLSX";

  /**
   * Text for true/Yes. Can be used to store true/false in database
   */
  public static final String CODE_YES = "Y";

  /**
   * Text for false/No. Can be used to store true/false in database
   */
  public static final String CODE_NO = "N";
  /**
   * Text for all type of versions active/inactive
   */
  public static final String CODE_ALL = "ALL";

  /**
   * Display text for true/Yes
   */
  public static final String DISPLAY_YES = "YES";

  /**
   * Display text for false/no
   */
  public static final String DISPLAY_NO = "NO";

  /**
   * Key for iCDM system password
   */
  public static final String ICDM_SYSTEM_PASSWRD_KEY = "ICDM";
  /**
   * SR result passed
   */
  public static final String CODE_PASSED = "P";
  /**
   * SR result failed
   */
  public static final String CODE_FAILED = "F";

  /**
   * Constant for the Empty String
   */
  public static final String EMPTY_STRING = "";

  /**
   * ICDM Web service url : PRO
   */
  public static final String ICDM_WS_URL_PRO_HTTPS = "ICDM.WS_URL_PRO_HTTPS";

  /**
   * ICDM Web service url : BTEST
   */
  public static final String ICDM_WS_URL_BTEST_HTTPS = "ICDM.WS_URL_BTEST_HTTPS";

  /**
   * ICDM Web service url : DEV
   */
  public static final String ICDM_WS_URL_DEV_HTTPS = "ICDM.WS_URL_DEV_HTTPS";

  /**
   * ICDM Web Service Redirect URI For Azure SSO
   */
  public static final String ICDM_AZURE_ICDM_WS_REDIRECT_URI = "ICDM.AZURE_ICDM_WS_REDIRECT_URI";

  /**
   * Constant for VCDM Connection Failure when user provides password (Transfer to VCDM,Loading Data from DST into A2l
   * parameter,Takeover variants from VCDM)
   */
  public static final String VCDM_CONN_LOGIN_FAILURE =
      "Connection to VCDM failed. This could be due to one of the following reasons\n1. Incorrect VCDM credentials.\n2. Network Problems.";
  /**
   * Constant for VCDM Connection Failure when superuser credentials are used
   */
  public static final String VCDM_CONN_SERVER_FAILURE =
      "Connection to VCDM failed. Please check if the VCDM server is available";

  /**
   * Constant for the version name for baseline before paste of questionnaire
   */
  public static final String BASELINE_VERS_NAME_BFR_PASTE = "Baseline of working set before paste";

  /**
   * Constant for the version name for baseline before questionnaire update
   */
  public static final String BASELINE_VERS_NAME_BFR_QNAIRE_UPD = "Baseline of working set before qnaire update";

  /**
   * Constant for the version name for baseline created from data assessment
   */
  public static final String BASELINE_VERS_NAME_CRE_FROM_DATA_ASSMNT = "Baseline created from Data Assessment";

  /**
   * String Constant for "requestId"
   */
  public static final String REQUEST_ID = "requestId";

  /**
   * String Constant for "method"
   */
  public static final String METHOD = "method";

  /**
   * Method for the creation of data assessment baseline files
   */
  public static final String DA_CREATE_BASELINE_FILES = "dataassesmentbaselinefiles";

  /**
   * Method for the creation of WP Archival baseline files
   */
  public static final String WPA_CREATE_BASELINE_FILES = "wpabaselinefiles";

  /**
   * Private constructor for utility class
   */
  private CommonUtilConstants() {
    // Private constructor for utility class
  }
}