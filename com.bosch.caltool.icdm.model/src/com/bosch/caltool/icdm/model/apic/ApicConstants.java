package com.bosch.caltool.icdm.model.apic;


import java.io.File;

import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * The Class ApicConstants.
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public final class ApicConstants {

  /** the module name in TabV_APIC_Acces_Rights. */
  public static final String APIC_MODULE_NAME = "APIC";

  /** The Constant NO_ACCESS. */
  public static final String NO_ACCESS = "NO_ACCESS";

  /** the module name in TabV_APIC_Node_Acces. */
  public static final String PIDC_NODE_TYPE = "PIDC";

  /** the module name in TabV_APIC_Node_Acces. */
  public static final String ATTR_NODE_TYPE = "APIC_ATTR";

  /** the Node type in T_Links. */
  public static final String ATTRIB_VALUE = "ATTRIB_VALUE";

  /** the Use Case Node Type. */
  public static final String UC_NODE_TYPE = "USECASE";

  /** the Focus matrix Type. */
  public static final String FM_NODE_TYPE = "FOCUS_MATRIX";

  /** the Focus matrix Review Type. */
  public static final String FM_RVW_NODE_TYPE = "FOCUS_MATRIX_REVIEW";

  /** the Mandatory attr Type. */
  public static final String MAND_ATTR_TYPE = "MANDATORY_ATTR";

  /** Alias Definition type. */
  public static final String ALIAS_DEF_TYPE = "ALIAS_DEF";

  /** Workpackage Responsibility Constant. */
  public static final String WP_RESPONSIBILITY = "RESP";

  /** A2l Workpackage Constant. */
  public static final String A2L_WORK_PKG = "WP";

  /** Alias details. */
  public static final String ALIAS_DETAIL_TYPE = "ALIAS_DETAIL";

  /** the Use Case Section Node Type. */
  public static final String UCS_NODE_TYPE = "USECASE_SEC";

  /** the Use Case Section Node Type. */
  public static final String RULE_SET_NODE_TYPE = "CDR_RULE_SET";

  /** the Use Case Section Node Type. */
  public static final String COMPONENT_PKG_NODE_TYPE = "Component Package";

  /** the Function Node Type. */
  public static final String FUNC_NODE_TYPE = "FUNCTION";

  /** Component node type. */
  public static final String COMP_PKG_NODE_TYPE = "COMP_PKG";

  /** Workpackage Division type. */
  public static final String WORKPACKAGE_DIV_NODE_TYPE = "WP_DIV";

  /** RVW QNAIRE ANS node type. */
  public static final String RVW_QNAIRE_ANS_NODE_TYPE = "RVW_QNAIRE_ANS";

  /** the ATTR_LEVEL used for the CUSTOMER/BRAND ATTRIBUTE. */
  public static final Long CUST_BRAND_ATTR_LEVEL = 1L;

  /** the ATTR_LEVEL used for the project name. */
  public static final int PROJECT_NAME_ATTR = -1;

  /** No Variant Id. */
  public static final Long NO_VARIANT_ID = -1L;

  /** the ATTR_LEVEL used for the variant code. */
  public static final int VARIANT_CODE_ATTR = -2;

  /** the ATTR_LEVEL used for the sub-variant code. */
  public static final int SUB_VARIANT_CODE_ATTR = -3;

  /** the ATTR_LEVEL used for the variant coding attribute. */
  public static final int VARIANT_CODING_ATTR = -10;

  /** the ATTR_LEVEL used for the vCDM APRJ Name attribute. */
  public static final int VCDM_APRJ_NAME_ATTR = -20;

  /** the ATTR_LEVEL used for the SDOM Project Name attribute. */
  public static final int SDOM_PROJECT_NAME_ATTR = -30;

  /** The root level index for the PIDC structure. */
  public static final int PIDC_ROOT_LEVEL = 0;

  /** The character used in the database to identify NO. */
  public static final String CODE_YES = "Y";

  /** The character used in the database to identify NO. */
  public static final String CODE_NO = "N";
  /**
   * Constant for WpResp type customer
   */
  public static final String WP_RESP_TYPE_CUSTOMER = "C";
  /**
   * Constant for WpResp Type Bosch
   */
  public static final String WP_RESP_TYPE_BOSCH = "R";

  /**
   * Constant for Rule Id
   */
  public static final String RULE_ID = "ruleId";

  /**
   * Constant for RuleSetParameter Id
   */
  public static final String RULESET_PARAM_ID = "rulesetParamId";

  /**
   * constant to store date format for attributes editor date selection
   */
  public static final String[] ATTR_DATE_FORMATS = { "yyyy.mm.dd", "yyyy.mm" };

  /**
   * The character used in the database to identify YES.
   *
   * @deprecated use {@link #CODE_YES} instead
   */
  @Deprecated
  public static final String YES = CODE_YES;

  /**
   * The character used in the database to identify NO.
   *
   * @deprecated use {@link #CODE_NO} instead
   */
  @Deprecated
  public static final String NO = CODE_NO;

  /** ICDM-480 The character used in the database to identify Internal Secured Attr. */
  public static final String INTERNAL = "I";

  /** ICDM-480 The character used in the database to identify External Secured Attr. */
  public static final String EXTERNAL = "E";


  /** The character used in the database to identify NOT_DEFINED. */
  public static final String NOT_DEFINED = "?";

  /** The name of the root node of the Attributes structure. */
  public static final String ATTR_ROOT_NODE_NAME = "System View"; // iCDM-530

  /** The name of the root node of the Use case structure. */
  public static final String USE_CASE_ROOT_NODE_NAME = "Use Cases";

  /** The name of the root node of the Use case structure. */
  public static final String QNAIRE_RESPONSE_STATUS_MSG = "Questionnaire finished - All questions answered";

  /** The ID for the attribute value type TEXT. */
  public static final int ATTR_VALUE_TYPE_TEXT = 1;

  /** The ID for the attribute value type NUMBER. */
  public static final int ATTR_VALUE_TYPE_NUMBER = 2;

  /** The ID for the attribute value type DATE. */
  public static final int ATTR_VALUE_TYPE_DATE = 3;

  /** The ID for the attribute value type BOOLEAN. */
  public static final int ATTR_VALUE_TYPE_BOOLEAN = 4;

  /** The ID for the attribute value type HYPERLINK. */
  public static final int ATTR_VALUE_TYPE_HYPERLINK = 5;

  /** The ID for the attribute value type ICDM_USER. */
  public static final int ATTR_VALUE_TYPE_ICDM_USER = 6;

  /** Column index of Coc WP column in Coc WP Page */
  public static final int COC_WP_COLUMN_INDEX = 0;

  /** Column index of used undefined column in Coc WP Page */
  public static final int COC_WP_UNDEFINED_COLUMN_INDEX = 1;

  /** Column index of used no column in Coc WP Page */
  public static final int COC_WP_NO_COLUMN_INDEX = 2;

  /** Column index of used yes column in Coc WP Page */
  public static final int COC_WP_YES_COLUMN_INDEX = 3;

  /** Column index of level column in Coc WP Page */
  public static final int COC_WP_LEVEL_COLUMN_INDEX = 4;

  /** The character used in the database to identify TRUE. */
  public static final String BOOLEAN_TRUE_DB_STRING = "T";

  /** The text to be used in the application for boolean TRUE. */
  public static final String BOOLEAN_TRUE_STRING = "TRUE";

  /** The character used in the database to identify FALSE. */
  public static final String BOOLEAN_FALSE_DB_STRING = "F";
  /**
   * Support Dashboard visibility
   */
  public static final String SUPPORT_DASHBOARD_VISIBLE = "Y";
  /** The text to be used in the application for boolean FALSE. */
  public static final String BOOLEAN_FALSE_STRING = "FALSE";

  /** The text to be used in the application for YES. */
  public static final String USED_YES_DISPLAY = "YES";

  /** The text to be used in the application for NO. */
  public static final String USED_NO_DISPLAY = "NO";

  /** The text to be used in the application for NOT DEFINED. */
  public static final String USED_NOTDEF_DISPLAY = "???";

  /** The text to be used in the application for for Nat table column used flag no. */
  public static final String USED_NO_COL_NAME = "No";

  /** The text to be used in the application for Nat table column used flag yes. */
  public static final String USED_YES_COL_NAME = "Yes";

  /** The text to be used in the application for INVALID values. */
  public static final String USED_INVALID_DISPLAY = "---";
  /**
   * params not associated to Wp.
   */
  public static final String NOT_DEFINED_PARAM = "NOT DEFINED";

  /** The text to be used in the application for VARIANT attributes. */
  public static final String VARIANT_ATTR_DISPLAY_NAME = "<VARIANT>";

  /** The text to be used in the application to select VARIANT. */
  public static final String VARIANT_SELECT = "<Select the variant>";

  /** The text to be used in the application for used flag no. */
  public static final String NOT_USED = "<NOT USED>";

  /** The text to be used in the application for used flag YES (iCDM-1317). */
  public static final String USED = "<USED>";

  /** The text to be used in the application for SUB-VARIANT attributes. */
  public static final String SUB_VARIANT_ATTR_DISPLAY_NAME = "<SUB-VARIANT>";

  /** The text to be used in the application for not defined attributes. */
  public static final String ATTR_NOT_DEFINED = "<NOT-DEFINED>";

  /** The text used in the database for READ access to the APIC database. */
  public static final String APIC_READ_ACCESS = "APIC_READ";

  /** The text used in the database for WRITE access to the APIC database. */
  public static final String APIC_WRITE_ACCESS = "APIC_WRITE";

  /** The text used in the database for WRITE access for PIDCs. */
  public static final String PIDC_WRITE_ACCESS = "PID_WRITE";

  /** The text used in the database for READ access for hidden PIDCs and hidden attributes. */
  public static final String APIC_READ_ALL_ACCESS = "APIC_READ_ALL";

  /** The ID for pidc status INWORK. */
  public static final String PIDC_STATUS_ID_INWORK_STR = "I";

  /** The ID for pidc status RELEASED. */
  public static final String PIDC_STATUS_ID_LOCKED_STR = "L";

  /** Default text for combo boxes. */
  public static final String DEFAULT_COMBO_SELECT = "<SELECT>";

  /** Default text for combo boxes. */
  public static final String NO_GROUP_COMBO_SELECT = "No groups available!";

  /** Text for creating or updating with a duplicate attribute value. */
  public static final String DUPLICATE_ATTR_VALUE = "Value already present. Please add another value!";

  /**
   * TO mail address for Hotline.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String ICDM_HOTLINE_TO = "ICDM_HOTLINE_TvO";

  /**
   * SUBJECT in mail for Hotline.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String ICDM_HOTLINE_SUBJECT = "ICDM_HOTLINE_SUBJECT";

  /**
   * Icdm -1117 SUBJECT in mail for Hotline for PIDC creation.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String MAIL_NEW_PIDC = "MAIL_NEW_PIDC";

  /**
   * The Constant PIDC_DIVISION_ATTR.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String PIDC_DIVISION_ATTR = "PIDC_DIVISION_ATTR";

  /**
   * The Constant WP_TYPE_ATTR_ID.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String WP_TYPE_ATTR_ID = "WP_TYPE_ATTR_ID";

  /** The Constant WP_FC2WP2_VALUE_ID. */
  public static final String WP_FC2WP2_VALUE_ID = "WP_FC2WP2_VALUE_ID";

  /** The Constant WP_FC2WP1_VALUE_ID. */
  public static final String WP_FC2WP1_VALUE_ID = "WP_FC2WP1_VALUE_ID";

  /** The Constant WP_GROUP_VALUE_ID. */
  public static final String WP_GROUP_VALUE_ID = "WP_GROUP_VALUE_ID";

  /**
   * The Constant WP_ROOT_GROUP_ATTR_ID.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String WP_ROOT_GROUP_ATTR_ID = "WP_ROOT_GROUP_ATTR_ID";

  /** The Constant FC2WP_1. */
  public static final String FC2WP_1 = "FC2WP1";

  /** The Constant FC2WP_2. */
  public static final String FC2WP_2 = "FC2WP2";

  /**
   * The Constant GROUP_MAPPING_ID.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String GROUP_MAPPING_ID = "GROUP_MAPPING_ID";

  /** The Constant FC_WP_MAPPING. */
  public static final String FC_WP_MAPPING = "FC_WP_MAPPING";

  /**
   * The Constant ICDM_CLIENT_VERSION.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String ICDM_CLIENT_VERSION = "iCDM_CLIENT_VERSION";

  /** Dummy variant node for 'No variant'. */
  // ICDM-2332
  public static final String DUMMY_VAR_NODE_NOVAR = "NO-VARIANT";

  /**
   * The Constant MANDATORY_RULE_SET.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String MANDATORY_RULE_SET = "CDR_MANDATORY_RULESET_ATTR_ID";

  /**
   * The Constant SSD_NODE_ID.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String SSD_NODE_ID = "SSD_NODE_ID";

  /**
   * The Constant SSD_COMP_PKG_NODE_ID.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String SSD_COMP_PKG_NODE_ID = "SSD_COMP_PKG_NODE_ID";

  /** SR result passed. */
  public static final String CODE_PASSED = "P";

  /** SR result failed. */
  public static final String CODE_FAILED = "F";

  /**
   * Constant for General Question Name
   */
  public static final String GENERAL_QUESTIONS = "General Questions";

  /**
   * ICDM-2440.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String SSD_COMPLI_PARAM_NODE_ID = "SSD_COMPLI_RULE_NODE_ID";

  /**
   * iCDM-946 : Common param const to enable/disable mail notifications.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String MAIL_NOTIFICATION_ENABLED = "MAIL_NOTIFICATION_ENABLED";

  // ICDM-2487 P1.27.122
  /** The Constant COMPLIANCE_PARAM. */
  public static final String COMPLIANCE_PARAM = "Compliance Parameter";

  /** The Constant for Read Only Parameters. */
  public static final String READ_ONLY_PARAM = "Read Only Parameter";

  /**
   * Name of compliance report
   */
  public static final String COMPLIANCE_REPORT_NAME = "SSDComplianceReport.pdf";


  /** Parameter not in CalMemory. */
  public static final String PARAM_NOT_IN_CALMEMORY = "Parameter not in CalMemory";

  /** Workpackage Constant. */
  public static final String WORKPACKAGE = "WP";

  /** The Constant FC_CONST. */
  /* constant for FC */
  public static final String FC_CONST = "FC";

  /** The Constant BC_CONST. */
  /* constant for BC */
  public static final String BC_CONST = "BC";

  /** The Constant COMP. */
  /* constant for Component Pkg */
  public static final String COMP = "COMP";

  /** Workpackage Constant. */
  public static final String WORKPACKAGE_DESC = "Work Packages";

  /** The Constant FC_DESC. */
  /* constant for FC */
  public static final String FC_DESC = "Functions";

  /** The Constant BC_DESC. */
  /* constant for BC */
  public static final String BC_DESC = "Base Components";

  /** The Constant COMP_DESC. */
  /* constant for Component Pkg */
  public static final String COMP_DESC = "Component Packages";

  /** The Constant UNASSIGNED_PARAM. */
  /* constant for Special FC */
  public static final String UNASSIGNED_PARAM = "<NOT_ASSIGNED>";

  /** The Constant UNASSIGNED_PARAM_DESC. */
  /* constant for Special FC */
  public static final String UNASSIGNED_PARAM_DESC = "Not Assigned";

  /** The Constant NOT_ASSIGNED. */
  // ICDM 395
  public static final String NOT_ASSIGNED = "<NOT_ASSIGNED>";

  /**
   * Input Json file - Meta data
   */
  public static final String COMPLI_INPUT_METADATA_JSON_FILE_NAME = "CompliInputMetaData.json";

  /** The Constant RULE_SET. */
  // Icdm-1368 Constant for Rule Set
  public static final String RULE_SET = "Rule Set";

  /** The Constant FUNCTION. */
  // Constant for Function
  public static final String FUNCTION = "Function";

  /** Code Word Yes. */
  public static final String CODE_WORD_YES = "Yes";


  /** No. */
  public static final String CODE_WORD_NO = "No";

  /** Constant for option ALL. */
  public static final String OPTION_ALL = "<ALL>";

  /** Constant for isDiff. */
  public static final String IS_DIFF = "IsDiff";

  /**
   * Error code if wp is already existing while FC2WP Import
   */
  public static final String ERRCODE_FC2WP_IMPORT_A2L_WP_ALREADY_EXISTS = "FC2WP_IMPORT_TO_A2L_WP.WP_ALREADY_EXISTS";

  /**
   * CDR Ready for series types.
   */
  public enum READY_FOR_SERIES {

                                /** Yes. */
                                YES("A", ApicConstants.USED_YES_DISPLAY),

                                /** No. */
                                NO("M", ApicConstants.USED_NO_DISPLAY),

                                /** Manual. */
                                NOT_DEFINED("", "");

    /** The db type. */
    public final String dbType;

    /** The ui type. */
    public final String uiType;

    /**
     * Instantiates a new ready for series.
     *
     * @param dbType the db type
     * @param uiType the ui type
     */
    READY_FOR_SERIES(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * Gets the db type.
     *
     * @return DB type literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * Gets the ui type.
     *
     * @return UI Type string
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Ready for series object for the given db type.
     *
     * @param dbType db literal of type
     * @return the file type object
     */
    public static READY_FOR_SERIES getType(final String dbType) {
      for (READY_FOR_SERIES type : READY_FOR_SERIES.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return NOT_DEFINED;
    }

    /**
     * Return the Ready for series object for the given db type.
     *
     * @param uiType ui literal of type
     * @return the file type object
     */
    public static String getDbType(final String uiType) {
      for (READY_FOR_SERIES type : READY_FOR_SERIES.values()) {
        if (type.uiType.equals(uiType)) {
          return type.dbType;
        }
      }
      return "";
    }
  }

  /**
   * Match Ref Flag in cdr result parameter or exact match
   */
  public enum EXACT_MATCH {

                           /** Yes. */
                           YES("Y", ApicConstants.USED_YES_DISPLAY),

                           /** No. */
                           NO("N", ApicConstants.USED_NO_DISPLAY),

                           /** Manual. */
                           NOT_DEFINED("", "");

    /** The db type. */
    public final String dbType;

    /** The ui type. */
    public final String uiType;

    /**
     * Instantiates a new ready for series.
     *
     * @param dbType the db type
     * @param uiType the ui type
     */
    EXACT_MATCH(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * Gets the db type.
     *
     * @return DB type literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * Gets the ui type.
     *
     * @return UI Type string
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the Ready for series object for the given db type.
     *
     * @param dbType db literal of type
     * @return the file type object
     */
    public static EXACT_MATCH getType(final String dbType) {
      for (EXACT_MATCH type : EXACT_MATCH.values()) {
        if (type.dbType.equals(dbType)) {
          return type;
        }
      }
      return NOT_DEFINED;
    }

    /**
     * Return the Ready for series object for the given db type.
     *
     * @param uiType ui literal of type
     * @return the file type object
     */
    public static String getDbType(final String uiType) {
      for (EXACT_MATCH type : EXACT_MATCH.values()) {
        if (type.uiType.equals(uiType)) {
          return type.dbType;
        }
      }
      return "";
    }
  }

  /**
   * CDR Ready for series types.
   */
  public enum PROJ_ATTR_USED_FLAG {

                                   /** Used flag YES. */
                                   YES("Y", ApicConstants.USED_YES_DISPLAY),

                                   /** Used flag NO. */
                                   NO("N", ApicConstants.USED_NO_DISPLAY),

                                   /** Not defined. */
                                   NOT_DEFINED("?", ApicConstants.USED_NOTDEF_DISPLAY),

                                   /** new attribute. */
                                   NEW_ATTR("", "NEW");

    /** value in database column. */
    private final String dbType;

    /** Display value in UI. */
    private final String uiType;

    /**
     * Instantiates a new proj attr used flag.
     *
     * @param dbType the db type
     * @param uiType the ui type
     */
    PROJ_ATTR_USED_FLAG(final String dbType, final String uiType) {
      this.dbType = dbType;
      this.uiType = uiType;
    }

    /**
     * Gets the db type.
     *
     * @return DB type literal
     */
    public final String getDbType() {
      return this.dbType;
    }

    /**
     * Gets the ui type.
     *
     * @return UI Type string
     */
    public final String getUiType() {
      return this.uiType;
    }

    /**
     * Return the proj attr used flag enum value for the given db type.
     *
     * @param dbType db literal of type
     * @return the file type object
     */
    public static PROJ_ATTR_USED_FLAG getType(final String dbType) {
      String typToCheck = ModelUtil.checkNull(dbType);
      for (PROJ_ATTR_USED_FLAG type : PROJ_ATTR_USED_FLAG.values()) {
        if (type.dbType.equals(typToCheck)) {
          return type;
        }
      }
      return null;
    }

    /**
     * Return the proj attr used flag db string for ui string.
     *
     * @param uiType ui literal of type
     * @return the file type object
     */
    public static String getDbType(final String uiType) {
      for (PROJ_ATTR_USED_FLAG type : PROJ_ATTR_USED_FLAG.values()) {
        if (type.uiType.equals(uiType)) {
          return type.dbType;
        }
      }
      return "";
    }
  }

  /** The Constant SORT_ATTRNAME. */
  public static final int SORT_ATTRNAME = 1;

  /** The Constant SORT_ATTRDESCR. */
  public static final int SORT_ATTRDESCR = 2;

  /** The Constant SORT_SUPERGROUP. */
  public static final int SORT_SUPERGROUP = 3;

  /** The Constant SORT_GROUP. */
  public static final int SORT_GROUP = 4;

  /** The Constant SORT_VALUETYPE. */
  public static final int SORT_VALUETYPE = 5;

  /** The Constant SORT_UNIT. */
  public static final int SORT_UNIT = 6;

  /** The Constant SORT_LEVEL. */
  public static final int SORT_LEVEL = 7;

  /** The Constant SORT_VALUE. */
  public static final int SORT_VALUE = 8;

  /** The Constant SORT_USED. */
  public static final int SORT_USED = 9;

  /** The Constant SORT_USED_NOT_DEF. */
  public static final int SORT_USED_NOT_DEF = 10;

  /** The Constant SORT_USED_NO. */
  public static final int SORT_USED_NO = 11;

  /** The Constant SORT_USED_YES. */
  public static final int SORT_USED_YES = 12;

  /** The Constant SORT_MANDATORY. */
  // ICDM-179
  public static final int SORT_MANDATORY = 13;

  // ICDM-2485
  /** To sort the icon column in the pidc editor. */
  public static final int SORT_ICONS = 13;

  /** The Constant SORT_PART_NUMBER. */
  // ICDM-260
  public static final int SORT_PART_NUMBER = 14;

  /** The Constant SORT_SPEC_LINK. */
  public static final int SORT_SPEC_LINK = 15;

  /** The Constant SORT_DESC. */
  public static final int SORT_DESC = 16;

  /** The Constant SORT_SUMMARY_DESC. */
  public static final int SORT_SUMMARY_DESC = 17;

  /** The Constant SORT_ALL. */
  // Icdm-379
  public static final int SORT_ALL = 18;

  /** The Constant SORT_NONE. */
  public static final int SORT_NONE = 19;

  /** The Constant SORT_ANY. */
  public static final int SORT_ANY = 20;

  /** The Constant SORT_NORMALIZED_FLAG. */
  // ICDM-860
  public static final int SORT_NORMALIZED_FLAG = 21;

  /** The Constant SORT_FORMAT. */
  public static final int SORT_FORMAT = 22;

  /** The Constant SORT_MODIFIED_DATE. */
  // ICDM-907
  public static final int SORT_MODIFIED_DATE = 23;

  /** ICDM-2227 constant for sorting created date. */
  public static final int SORT_ATTR_CREATED_DATE_PIDC = 29;

  /** The Constant USED_FLAG_DISPLAY_NAME. */
  public static final String USED_FLAG_DISPLAY_NAME = "<USED FLAG = TRUE>";

  /** The Constant INWORK. */
  // ICDM-99
  public static final String INWORK = "INWORK";

  /** The Constant LOCKED. */
  public static final String LOCKED = "LOCKED";


  /** Copy of text. */
  public static final String COPY_OF = "Copy of ";

  // iCDM-350
  // Description constants

  /** Description of root use case node. */
  public static final String USECASE_ROOT_DESC = "Filtered view related to special tasks"; // iCDM-1132

  /** Description of root project use case favourite node. */
  public static final String PROJ_USECASE_ROOT_DESC = "Sub sets of common use cases specially assigned to a PIDC";

  /** Description of root private use case favourite node. */
  public static final String USER_USECASE_ROOT_DESC =
      "Sub sets of common use cases specially assigned to me (in my role and my interest)";

  // iCDM-471
  // JPA constants
  /** Constant defining Query hint - READ-ONLY. */
  public static final String READ_ONLY = "eclipselink.read-only";

  /** Constant defining Query hint - Fetch-size. */
  public static final String FETCH_SIZE = "eclipselink.jdbc.fetch-size";

  /** Constant defining Query hint - Store mode. */
  public static final String STORE_MODE = "javax.persistence.cache.storeMode";

  /** Constant defining Query hint - Shared cache. */
  public static final String SHARED_CACHE = "eclipselink.cache.shared.default";

  /** Seperator for framing Key for CDR paramater name with type. */
  public static final String CDR_PARAM_DELIMITER = ":";

  /**
   * File ID for ICDM clearing Hotline template.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String ICDM_HOTLINE_FILE_ID = "ICDM_HOTLINE_TEMPL_FILE";

  /** File Name for ICDM clearing Hotline template. */
  public static final String ICDM_HOTLINE_FILE_NAME = "hotLineMailTemplate.html";

  /** Node ID Hotline Template. */
  public static final Long HOTLINE_TEMPLATE_NODEID = -1L;

  /** File Name for Caldata Analyzer Disclaimer html file. */
  public static final String CDA_DISCLAIMER_FILE_NAME = "cda_disclaimer.html";

  /** Deutsch File Name for cdfx readiness condition html file. */
  public static final String CDFX_READINESS_FILE_NAME_DE = "cdfx_readiness_condition.html";
  /** English File Name for cdfx readiness condition html file. */
  public static final String CDFX_READINESS_FILE_NAME_EN = "cdfx_readiness_condition_en.html";

  /** Node ID for Caldata Analyzer Disclaimer html. */
  public static final Long CDA_DISCLAIMER_NODE_ID = -8L;

  /** File Name for ICDM disclaimer template. */
  public static final String ICDM_DISCLAIMER_FILE_NAME = "Disclaimer.html";

  /** Node ID ICDM disclaimer template. */
  public static final Long ICDM_DISCLAIMER_NODEID = -5L;

  /** File Name for ICDM PIDC Requestor File template. */
  public static final String ICDM_PIDC_REQUESTOR_FILE_NAME = "PIDC-Requester_V_4.0_Template.xlsm";

  /** Node ID ICDM PIDC Requestor. */
  public static final Long ICDM_PIDC_REQUESTOR_NODEID = -2L;

  /** Attribute Super Group. */
  public static final String ATTR_SUPER_GROUP = "ATTR_SUPER_GRP";

  /** Use case Group. */
  public static final String TOP_LEVEL_UCG = "TOP_LEVEL_UCG";

  /** ICDM-933 top level entity for component package. */
  public static final Object COMP_PCKG = "COMP_PCKG";

  /** Name not defined for the Work Package. */
  public static final String NAME_NOT_DEFINED = "<NO NAME>";

  /** Text constant for VALUE. */
  public static final String VALUE_TEXT = "VALUE";

  /** Constant for the Empty String iCDM-654. */
  public static final String EMPTY_STRING = "";

  /** reviewed String. */
  public static final String REVIEWED = "Reviewed";

  /** Checked review score String. */
  public static final String CHECKED_REVIEW_SCORE = "checked";

  /** Not reviewed String. */
  public static final String NOT_REVIEWED = "Not Reviewed";

  /** Never reviewed String. */
  public static final String NEVER_REVIEWED = "Never Reviewed";

  /** reviewed String. */
  public static final String NOT_FINALLY_REVIEWED = "Not Finally Reviewed";

  /** reviewed String. */
  public static final String NOT_APPLICABLE = "NA";

  /**
   * Maximum fetch size of database.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String DB_SEARCH_MAX_RESULT_SIZE = "DB_SEARCH_MAX_RESULT_SIZE";

  /** Text indicator for deleted item. */
  public static final String DEL_TEXT = "<DELETED>";

  // ICDM-788
  /** String constant to indicate excel is with filtered attributes. */
  public static final String FILTERED_ATTR_EXCEL = "FY";

  /** String constant to indicate excel is with all attributes. */
  public static final String NOT_A_FILTERED_ATTR_EXCEL = "FN";


  /** Sort Attr Internal Security Column. */
  public static final int SORT_ATTR_SEC = 24;

  /** Sort Attr Value Internal Security Column. */
  public static final int SORT_ATTR_VAL_SEC = 25;

  /** Sort Attr Chararcertsics Column ICdm-956. */
  public static final int SORT_CHAR = 26;

  /** Sort Attr Chararcertsics Value Column ICdm-955. */
  public static final int SORT_CHAR_VAL = 27;

  /** Constant for two Objects Equal in Compare to Method. */
  public static final int OBJ_EQUAL_CHK_VAL = 0;

  /** Constant for two the Object Higher in Compare to Method. */
  public static final int OBJ_HIGHER_CHK_VAL = 1;

  /** Constant for two Objects Lower in Compare to Method. */
  public static final int OBJ_LOWER_CHK_VAL = -1;

  /** Fetch start level 0. */
  public static final int START_DATA_FETCH_STAGE0 = 0;

  /** Start level 2. */
  public static final int START_DATA_FETCH_STAGE2 = 2;

  /** Start level 3. */
  public static final int START_DATA_FETCH_STAGE3 = 3;

  /** Start level 4. */
  public static final int START_DATA_FETCH_STAGE4 = 4;

  /** Start level 5. */
  public static final int START_DATA_FETCH_STAGE5 = 5;

  /** Start data stage 6. */
  public static final int START_DATA_FETCH_STAGE6 = 6;

  /** Start level 7. */
  public static final int START_DATA_FETCH_STAGE7 = 7;

  /** Start level 8. */
  public static final int START_DATA_FETCH_STAGE8 = 8;

  /** Start level 9. */
  public static final int START_DATA_FETCH_STAGE9 = 9;

  /** Start level 10. */
  public static final int START_DATA_FETCH_STAGE10 = 10;

  /** Start level 11. */
  public static final int START_DATA_FETCH_STAGE11 = 11;

  /** Start level 12. */
  public static final int START_DATA_FETCH_STAGE12 = 12;


  /** ICDM-485 constant to indicate internal report. */
  public static final String INTERNAL_REPORT = "IR";

  /** ICDM-485 constant to indicate external report. */
  public static final String EXTERNAL_REPORT = "ER";

  /** Icdm-990 startindex of a String. */
  public static final int TASK_STR_START_IDX = 31;

  /** Icdm-1004 Constant for Char to Attribute Value. */
  public static final String CHARACTERISTIC = "Attribute Class";

  /** Icdm-1004 Constant for Char Val. */
  public static final String CHARVAL = "Value Class";

  /** Sort order ascending. */
  public static final int SORT_ORDER_ASC = 0;

  /** Sort order descending. */
  public static final int SORT_ORDER_DESC = 1;

  /** Maximum number of items that can be passed in 'in' clause in a JPA query. */
  public static final int JPA_IN_CLAUSE_LIMIT = 1000;

  /** 1025 Constant for Modified Date String. */
  public static final String MODIFIED_DATE = "Modified Date";

  /** Constant for Part number. */
  public static final String PART_NUMBER = "Part Number";

  /** Constant for Specification. */
  public static final String SPECIFICATION = "Specification";

  /** Icdm-1038 Constant for left arrow. */
  public static final String LEFT_ARROW = "<";

  /** Icdm-1038 Constant for right arrow. */
  public static final String RIGHT_ARROW = ">";

  /** Icdm-1038 Constant for Empty Space. */
  public static final String EMPTY_SPACE = " ";

  /**
   * Icdm-1566 loading the welcome page.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String KEY_WELCOME_FILE_ID = "WELCOME_FILE_ID";

  /** welcome page constant for iCDM client. */
  public static final String WELCOME_PAGE = "welcome_page";

  /** constant for OSS document download path. */
  public static final String OSS_DOCUMENT_PATH = "OSS_Document";

  /** icdm-1030 constant for project fav usecase root node. */
  public static final String PROJ_USE_CASE_ROOT_NODE_NAME = "Project Use Cases";

  /** icdm-1028 constant for project fav usecase root node. */
  public static final String USER_USE_CASE_ROOT_NODE_NAME = "Private Use Cases";

  /** Unit text of a numeric attribute, if it is not relevant to the attribute. */
  public static final String ATTRVAL_EMPTY_UNIT = "-";

  /** Any string. */
  public static final String ANY = "<ANY VALUE>";

  /** Hidden Value. */
  public static final String HIDDEN_VALUE = "<HIDDEN>";

  public static final String JSON_UNDEFINED_VALUE = "<???>";


  // iCDM-1240
  /** Tooltip text for used flag. */
  public static final String TOOLTIP_USED_FLAG =
      "USED is to be interpreted as follows : \n For Technical information -> Installed in my project \n For Administrative information -> Relevant for my project \n For Scheduling information -> Relevant for my project.";

  /** Tooltip text for used NO. */
  public static final String TOOLTIP_USED_NO =
      "NO is to be interpreted as follows : \n For Technical information -> NOT installed in my project \n For Administrative information -> NOT relevant for my project \n For Scheduling information -> NOT relevant for my project.";

  /** Tooltip text for used YES. */
  public static final String TOOLTIP_USED_YES =
      "YES is to be interpreted as follows : \n For Technical information -> Installed in my project \n For Administrative information -> Relevant for my project \n For Scheduling information -> Relevant for my project.";

  /** Tooltip text for NOT defined. */
  public static final String TOOLTIP_USED_NOT_DEFINED = "Installation/Relevancy to my project is NOT YET DECIDED!";

  /** Prime constant for computing hash code for equality checks. */
  public static final int HASH_CODE_PRIME = 31;

  /** Empty size for arrays. */
  public static final int EMPTY_ARR_SIZE = 0;

  /** Constant for Desc Order. */
  public static final int DESCENDING = 1;

  /** Constant for Ascending Order. */
  public static final int ASCENDING = 0;

  /** constant for Component Package. */
  public static final String COMP_PKG = "Component Package";

  /** column index for 0. */
  public static final int COLUMN_INDEX_0 = 0;

  /** column index for 1. */
  public static final int COLUMN_INDEX_1 = 1;

  /** column index for 2. */
  public static final int COLUMN_INDEX_2 = 2;

  /** The Constant SUMMARY_LABEL. */
  public static final String SUMMARY_LABEL = "Summary";

  /** The Constant COMMENT. */
  public static final String COMMENT = "Comment";

  // Column headings for pidc nattable
  /** Group label. */
  public static final String LABEL_GROUP = "Group";

  /** Super group label. */
  public static final String LABEL_SUPER_GROUP = "Super Group";


  /** Part Number label. */
  public static final String LABEL_PART_NUM = "Part Number";

  /** Specification label. */
  public static final String LABEL_SPECIFICATION = "Specification";

  /** Modified Date label. */
  public static final String LABEL_MODIFIED_DATE = "Modified Date";

  /** Attribute On label. */
  public static final String LABEL_ATTRIBUTE_CREATED_ON = "Attribute Created On";

  // ICDM-1560
  /** Sort Attr eadm name column. */
  public static final int SORT_ATTR_EADM_NAME = 28;

  /** Key for RISK_EVALUATION_TITLE_DESC in TABV_COMMON_PARAMS. */
  public static final String RISK_EVALUATION = "RISK_EVALUATION";

  /** Focus matrix definition level - Global(by use case attribute mapping). */
  public static final String FMD_LEVEL_GLOBAL = "Global";

  /** Focus matrix definition level - Local. */
  public static final String FMD_LEVEL_LOCAL = "Local";

  /**
   * ICDM-1836 level attribute constant that defines mandatory attributes
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String MANDATORY_LEVEL_ATTR = "MANDATORY_LEVEL_ATTR";

  /** column index for 3. */
  public static final int COLUMN_INDEX_3 = 3;

  /** column index for 4. */
  public static final int COLUMN_INDEX_4 = 4;

  /** column index for 5. */
  public static final int COLUMN_INDEX_5 = 5;

  /** column index for 6. */
  public static final int COLUMN_INDEX_6 = 6;

  /** column index for 7. */
  public static final int COLUMN_INDEX_7 = 7;

  /** column index for 8. */
  public static final int COLUMN_INDEX_8 = 8;

  /** column index for 9. */
  public static final int COLUMN_INDEX_9 = 9;


  /** tooltip string size. */
  public static final int TOOLTIP_STRING_SIZE = 65;

  /** pidc version string size. */
  public static final int PIDC_VERSION_STRING_SIZE = 60;

  /** hint string size. */
  public static final int HINT_STRING_SIZE = 100;

  /** for generic string array index value-0. */
  public static final int STRING_ARR_INDEX_ZERO = 0;

  /** for generic string array index value-1. */
  public static final int STRING_ARR_INDEX_ONE = 1;

  /** for generic string array index value-2. */
  public static final int STRING_ARR_INDEX_TWO = 2;

  /** for generic string array index value-3. */
  public static final int STRING_ARR_INDEX_THREE = 3;

  /** Vcdm transfer flag. */
  public static final int SORT_TRANSFERVCDM = 21;

  /** FM relevant flag. */
  public static final int SORT_FM_RELEVANT = 22;

  /** PIDC Editor message group. */
  public static final String MSGGRP_PIDC_EDITOR = "PIDC_EDITOR";

  /**
   * Mail Template File Id for deletion of Attribute value.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  // ICDM-2217 / ICDM-2300
  public static final String KEY_DEL_ATTRVAL_MAIL_TEMPLATE = "DELETE_ATTR_VAL_MAIL_TEMPLATE";

  /** Delete Attribute Value Mail Template Key. */
  // ICDM-2217 / ICDM-2300
  public static final String DEL_ATTRVAL_MAIL_TEMPLATE = "delAttrValMailTemplate.txt";

  /** Attribute in tabv_common params for which the pidc status is configured. */
  public static final String KEY_QUOT_ATTR_ID = "QUOT_ATTR_ID";

  /**
   * Value id in tabv_common params for which the pidc status is configured.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String KEY_QUOT_VALUE_HIDDEN_STATUS = "QUOT_VALUE_HIDDEN_STATUS";

  /** Review Questionnaires group in t_messages. */
  // ICDM-2188
  public static final String REVIEW_QUESTIONNAIRES_GRP = "REVIEW_QUESTIONNAIRES";

  /** The Constant KEY_HINT. */
  // ICDM-2188
  public static final String KEY_HINT = "HINT";

  /**
   * Result Column Name
   */
  public static final String RESULT_COL_NAME = "Result";

  /**
   * Question column Name
   */
  public static final String QUESTION_COL_NAME = "Question";

  /**
   * Serial Number
   */
  public static final String SERIAL_NO = "No.";

  /** The Constant KEY_MEASURABLE_Y_N. */
  // ICDM-2188
  public static final String KEY_MEASURABLE_Y_N = "MEASURABLE_Y_N";

  /** The Constant KEY_SERIES_MAT_Y_N. */
  // ICDM-2188
  public static final String KEY_SERIES_MAT_Y_N = "SERIES_MAT_Y_N";

  /** The Constant KEY_REMARK. */
  // ICDM-2188
  public static final String KEY_REMARK = "REMARK";

  /** The Constant KEY_OPL_OPEN_POINTS. */
  // ICDM-2188
  public static final String KEY_OPL_OPEN_POINTS = "OPL_OPEN_POINTS";

  /** The Constant KEY_OPL_MEASURE. */
  // ICDM-2188
  public static final String KEY_OPL_MEASURE = "OPL_MEASURE";

  /** The Constant KEY_OPL_RESPONSIBLE. */
  // ICDM-2188
  public static final String KEY_OPL_RESPONSIBLE = "OPL_RESPONSIBLE";

  /** The Constant KEY_OPL_DATE. */
  // ICDM-2188
  public static final String KEY_OPL_DATE = "OPL_DATE";

  /** Key for opl status label. */
  // ICDM-2188
  public static final String KEY_OPL_STATUS = "OPL_STATUS";

  /** The Constant KEY_RESULT. */
  // ICDM-2188
  public static final String KEY_RESULT = "RESULT";

  /** Top level user. */
  public static final String APIC_USER = "APIC_USER";


  /** statistical cannot be set manually ICDM-2583. */
  public static final String STAT_RIVET_ERR_MESS = "Statistical Rivet cannot be set manually";

  /**
   * Task 242053.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String PIDC_UP_TO_DATE_INTERVAL = "PIDC_UP_TO_DATE_INTERVAL";

  /** Task 274996. */
  public static final String USECASE_UP_TO_DATE_INTERVAL = "USECASE_UP_TO_DATE_INTERVAL";

  /**
   * Task 242053.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String SSD_PROJ_NODE_ATTR_ID = "SSD_PROJ_NODE_ATTR_ID";

  /**
   * Task 244427.
   *
   * @deprecated use enum {@link CommonParamKey} instead
   */
  @Deprecated
  public static final String DISCLAIMER_VALID_INTERVAL = "DISCLAIMER_VALID_INTERVAL";

  /** Status of shape check. */
  public static final String SHAPE_CHECK_PERFORMED = "Shape Check Performed";

  /** Column :COLUMN_PROJECT_CHAR used for Risk Evaluation in T_MESSAGES. */
  public static final String COLUMN_PROJECT_CHAR = "COLUMN_PROJECT_CHAR";

  /** Column :COLUMN_IS_RELEVANT used for Risk Evaluation in T_MESSAGES. */
  public static final String COLUMN_IS_RELEVANT = "COLUMN_IS_RELEVANT";

  /** Column :COLUMN_RB_INPUT_DATA used for Risk Evaluation in T_MESSAGES. */
  public static final String COLUMN_RB_INPUT_DATA = "COLUMN_RB_INPUT_DATA";

  /** Column :COLUMN_RB_SW_SHARE used for Risk Evaluation in T_MESSAGES. */
  public static final String COLUMN_RB_SW_SHARE = "COLUMN_RB_SW_SHARE";

  /** Top level entity ID for PID Card. */
  public static final Long TOP_LVL_ENT_ID_PIDC = 1L;

  /** Top level entity ID for Super Group. */
  public static final Long TOP_LVL_ENT_ID_SUPER_GRP = 2L;

  /** Top level entity ID for use case group. */
  public static final Long TOP_LVL_ENT_ID_UCG = 3L;

  /** Top level entity ID for component packages. */
  public static final Long TOP_LVL_ENT_ID_COMP_PKG = 4L;

  /** Top level entity ID for Super Group. */
  public static final Long TOP_LVL_ENT_ID_USER = 5L;

  /** Value id for USED YES. */
  public static final Long ATTR_VAL_USED_VALUE_ID = -1L;

  /** Value id for USED YES. */
  public static final Long ATTR_VAL_NOT_SET_VALUE_ID = -3L;

  /** Value id for USED NO. */
  public static final Long ATTR_VAL_NOT_USED_VALUE_ID = -2L;


  /** CDA output file path. */
  public static final String CDA_OUTPUT_DIR = System.getProperty("java.io.tmpdir") + File.separator + "CaldataAnalyzer";

  /** CDA output file name. */
  public static final String CDA_ZIPPED_FILE_NAME = "CDAResult";

  /** zip file extension. */
  public static final String ZIP_FILE_EXT = ".zip";

  /** Json file name for calibration data analyzer summary file. */
  public static final String CALDATA_JSON_FILE_NAME = "caldata.json";

  /** cda filter json file name. */
  public static final String CALDATA_FILTER_JSON_NAME = "caldata_filter.json";

  /** The Constant LEVEL_PIDC_VERSION. */
  public static final String LEVEL_PIDC_VERSION = "VER";

  /** The Constant LEVEL_PIDC_VARIANT. */
  public static final String LEVEL_PIDC_VARIANT = "VAR";

  /** The Constant LEVEL_PIDC_SUB_VARIANT. */
  public static final String LEVEL_PIDC_SUB_VARIANT = "SUBVAR";


  /** Config label used to display value edit icon in pidc nattable. */
  public static final String CONFIG_LABEL_VALUE_EDIT = "VALUE_EDIT_ICON";

  /**
   *
   */
  public static final String CONFIG_LABEL_HYPERLINK = "HYPERLINK";
  /**
   * Config label which show that the project attribute value is undefined
   */
  public static final String CONFIG_LABEL_CHECK_BOX5 = "checkBoxEditor_5";
  /**
   * Config label which show that the project attribute is not relevant for the project
   */
  public static final String CONFIG_LABEL_CHECK_BOX4 = "checkBoxEditor_4";
  /**
   * Config label which show that the project attribute is relevant for the project
   */
  public static final String CONFIG_LABEL_CHECK_BOX3 = "checkBoxEditor_3";

  /** The Constant FUNCTIONALITY_PIDC_EDITOR. */
  public static final String FUNCTIONALITY_PIDC_EDITOR = "PIDC_EDITOR";

  /** Constant for SDOM Pver attribute. */
  public static final String SDOM_PVER_ATTR_NAME = "PVER name in SDOM";

  /** The Constant WELCOME_PAGE_FILE. */
  public static final String WELCOME_PAGE_FILE = "welcomepage.zip";

  /**
   * The Constant OSS Document file name
   */
  public static final String OSS_DOCUMENT = "OSS_Document.zip";
  /** The Constant DISCLAIMER_FILE. */
  public static final String DISCLAIMER_FILE = "disclaimer.zip";

  /** The Constant DISCLAIMER_FILE. */
  public static final String MAIL_TEMPLATE_FILE = "mailtemplate.zip";


  /**
   *
   */
  public static final String DELETE_ATTR_VAL_MAIL_TEMP = "deletemail.zip";


  /** caldata analyzer disclaimer file name. */
  public static final String CDA_DISCLAIMER_FILE = "cda_disclaimer.zip";

  /**
   * cdfx export readiness condition file name.
   */
  public static final String CDFX_READINESS_FILE = "cdfx_readiness_condition.zip";

  /** The Constant ICDM_STARTUP. */
  public static final String ICDM_STARTUP = "ICDM_STARTUP";

  /** The Constant NO_ACCESS_TEXT. */
  public static final String NO_ACCESS_TEXT = "NO_ACCESS_TEXT";

  /**
   *
   */
  public static final String PIDC_VERS_ID_STR = "pidcVersionId";

  /**
   *
   */
  public static final String SELECTED_UC_ITEM_ID_STR = "selectedUcItemId";

  /**
   * Focus Matrix version Status.
   */
  public enum FM_VERS_STATUS {

                              /** Working set version. */
                              WORKING_SET("W"),

                              /** Old version(Archived). */
                              OLD("O");

    /** Status of version. */
    public final String status;

    /**
     * Instantiates a new fm vers status.
     *
     * @param code the code
     */
    FM_VERS_STATUS(final String code) {
      this.status = code;
    }

    /**
     * Return the enum object for the given db type.
     *
     * @param status db literal of type
     * @return the file type object
     */
    public static FM_VERS_STATUS getStatus(final String status) {
      for (FM_VERS_STATUS code : FM_VERS_STATUS.values()) {
        if (code.getDbStatus().equals(status)) {
          return code;
        }
      }
      return WORKING_SET;
    }

    /**
     * Gets the db status.
     *
     * @return the db status code
     */
    public String getDbStatus() {
      return this.status;
    }
  }

  /** Level 3 index. */
  public static final int LEVEL_3 = 3;

  /** Level 2 index. */
  public static final int LEVEL_2 = 2;

  /** Level 1 index. */
  public static final int LEVEL_1 = 1;

  /** Attr Col0. */
  public static final int ATTR_TAB_COL0 = 0;

  /** The Constant MAX_LEVELS_ALLOWED. */
  public static final int MAX_LEVELS_ALLOWED = 3;

  /** The Constant ONE_CONST. */
  public static final int ONE_CONST = 1;

  /** constant for level 3. */
  public static final int LEVEL_THREE_CONST = 3;

  /** constant for level 2. */
  public static final int LEVEL_TWO_CONST = 2;

  /** The Constant VERSION_SIZE_1. */
  public static final int VERSION_SIZE_1 = 1;

  /** trail comma size to display text. */
  public static final int TRAIL_COMMA_SIZE = 2;

  /** initial size of StringBuilder. */
  public static final int STR_BUILDER_SIZE = 100;

  /** The Constant LABEL_VCDM_FLAG. */
  public static final String LABEL_VCDM_FLAG = "vCDM";

  /** The Constant LABEL_FM_RELEVANT_FLAG. */
  public static final String LABEL_FM_RELEVANT_FLAG = "FM";

  /** The Constant REPORT_TYPE_EXCEL. */
  public static final String REPORT_TYPE_EXCEL = "EXCEL";

  /** The Constant REPORT_TYPE_PDF. */
  public static final String REPORT_TYPE_PDF = "PDF";

  /** The Constant CSSD_EXTERNAL_REPORT_CODE. */
  public static final String CSSD_EXTERNAL_REPORT_CODE = "_External_";

  /**
   *
   */
  public static final String HEX_COMPARE_COMPLI_CHECK_MSG = "HEX_COMPARE_COMPLI_CHECK";

  /**
   * USER_ID to be used only for MoniCa Auditor
   */
  public static final Long MONICA_USER_ID = -1L;

  /** USER_ID to be used only for Creating Dummy Apic User - FC2WP */
  public static final Long APIC_DUMMY_USER_ID = -2L;

  /**
   * Language key
   */
  public static final String LANGUAGE = "Language";
  /**
   * Pidc key
   */
  public static final String PREF_PIDC_USECASE_ATTR_FILTER_ENABLED = "pidc.project.usecase.attributes.filter.enabled";

  /**
   * 489386 - Handling special characters in WP names (Renamed <DEFAULT_WP> to _DEFAULT_WP_) Default WP name for PAL
   */
  public static final String DEFAULT_A2L_WP_NAME = "_DEFAULT_WP_";

  /**
   * Prefix constant for Responsibility while doing Export/Import operation
   */
  public static final String PREFIX_RESP = "_RESP__";

  /**
   * Prefix constant for Responsibility while doing Export/Import operation
   */
  public static final String PREFIX_FUNC = "_FUNC__";


  /**
   * Prefix constant for WorkPackage while doing Export/Import operation
   */
  public static final String PREFIX_WP = "_WP__";

  /**
   * Alias name for default Robert bosch user
   */
  public static final String ALIAS_NAME_RB = "RB";

  /**
   * Alias name for default customer type user
   */
  public static final String ALIAS_NAME_CUSTOMER = "CUST";

  /**
   * Alias name for default others type user
   */
  public static final String ALIAS_NAME_OTHERS = "OTHER";

  /**
   * constant for responsiblity BEG's alias name
   */
  public static final String ALIAS_NAME_RB_BEG = "RB_BEG";

  /**
   * String for default level wp in WP selection dialog
   */
  public static final String DEFAULT_LEVEL_WP = "Default level WP (This will remove variant group level customization)";

  /**
   * Icdm-830 Data Model changes for New Column Clearing status
   *
   * @author rgo7cob enumeration for the Clearing status
   */
  public enum CLEARING_STATUS {
                               /**
                                * cleared Value
                                */
                               CLEARED("Y", "Cleared"),
                               /**
                                * not cleared Value
                                */
                               NOT_CLEARED("N", "Not Cleared"),
                               /**
                                * In clearing
                                */
                               IN_CLEARING("I", "In Clearing"),

                               /**
                                * Deleted Status - Icdm-1180 new Status
                                */
                               DELETED("D", "Deleted"),
                               /**
                                * Rejected Icdm-1180 new Status
                                */
                               REJECTED("R", "Rejected");

    /**
     * DB Text for clearing status
     */
    private final String dbText;

    /**
     * UI text for clearing status
     */
    private final String uiText;


    /**
     * Constructor
     *
     * @param dbText DB text
     * @param uiText UI display text
     */
    CLEARING_STATUS(final String dbText, final String uiText) {
      this.dbText = dbText;
      this.uiText = uiText;
    }

    /**
     * @return the text
     */
    public String getDBText() {
      return this.dbText;
    }

    /**
     * @return the ui text
     */
    public String getUiText() {
      return this.uiText;
    }

    /**
     * @param dbText dbType
     * @return the Enum For the Cleraing Status
     */
    public static CLEARING_STATUS getClearingStatus(final String dbText) {

      for (CLEARING_STATUS clrStatus : CLEARING_STATUS.values()) {
        if (clrStatus.getDBText().equals(dbText)) {
          return clrStatus;
        }
      }
      return null;

    }
  }


  /**
   * @author dmr1cob enumeration for the 2fA check level
   */
  public enum TWOFA_CHECK_LEVEL {
                                 /**
                                  * Check level - NONE
                                  */
                                 NONE("N", "None"),
                                 /**
                                  * Check level - WARN
                                  */
                                 WARN("W", "Warn"),
                                 /**
                                  * Check level - BLOCK
                                  */
                                 BLOCK("B", "Block");

    /**
     * DB Text for 2FA check level
     */
    private final String dbText;

    /**
     * UI text for 2FA check level
     */
    private final String uiText;


    /**
     * Constructor
     *
     * @param dbText DB text
     * @param uiText UI display text
     */
    TWOFA_CHECK_LEVEL(final String dbText, final String uiText) {
      this.dbText = dbText;
      this.uiText = uiText;
    }

    /**
     * @return the text
     */
    public String getDBText() {
      return this.dbText;
    }

    /**
     * @return the ui text
     */
    public String getUiText() {
      return this.uiText;
    }

    /**
     * @param dbText dbType
     * @return the Enum For 2FA check level
     */
    public static TWOFA_CHECK_LEVEL get2FACheckLevel(final String dbText) {

      for (TWOFA_CHECK_LEVEL level : TWOFA_CHECK_LEVEL.values()) {
        if (level.getDBText().equals(dbText)) {
          return level;
        }
      }
      return null;

    }
  }

  /**
   * Working set name
   */
  public static final String WORKING_SET_NAME = "Working Set";

  /**
   *
   */
  public static final String QSSD_PARAM = "QSSD Parameter";

  /**
   *
   */
  public static final String BLACK_LIST_PARAM = "Black List Parameter";
  /**
   *
   */
  public static final String ROOT_GROUP_RESPONSIBILITIES = "_Responsibilities";
  /**
   *
   */
  public static final String ROOT_GROUP_WORKPACKAGES = "_Workpackages";


  /**
   *
   */
  public static final String ROOT_GROUP_RESP_FUNC = "_RespFunc";

  /**
   *
   */
  public static final String ROOT_GROUP_RESP_WP = "_RespWp";

  /**
   *
   */
  public static final String ROOT_GROUP_RESP_WP_FUNC = "_RespWpFunc";

  /**
   * constant for Webflow element ID
   */
  public static final String WEBFLOW_ELEMENT = "Webflow_Element";

  /**
   * constant for Webflow element's Variant ID
   */
  public static final String WEBFLOW_VARIANT = "Variant";

  /**
   *
   */
  public static final String WEBFLOW_PIDC = "pidc";

  /**
   * Folder to store a2l resp merge data as json
   */
  public static final String A2L_RESP_MERGE = "A2L_RESP_MERGE";

  /**
   * File name to store a2l resp merge data
   */
  public static final String A2L_RESP_MERGE_DATA_JSON_NAME = "A2lRespMergeData.json";
  /**
   * Lock object to avoid parallel usage of A2L parser as it does not support multi threading
   */
  public static final Object A2L_PARSER_SYNC_LOCK = new Object();

  /**
   * Citi has Limited access (only for power users)
   */
  public static final String CITI_ACCESS_LIMITED = "P";

  /**
   * Citi is not visible to any user
   */
  public static final String CITI_ACCESS_INVISIBLE = "N";

  /**
   * Citi is visible to all users
   */
  public static final String CITI_ACCESS_VISIBLE = "A";

  /**
   * Dependent parameter
   */
  public static final String DEPENDENT_PARAM = "Parameter dependent on :";

  /**
   * Holds the value of last used file directory path
   */
  public static final String LAST_USED_FILE_PATH = "LAST_USED_FILE_PATH";

  /**
   * Regex to format file name with spl chars
   */
  public static final String SPL_CHAR_PTRN = "[/:<?>|\\*]";

  /**
   * Regex to format file name with invalid chars
   */
  public static final String INVALID_CHAR_PTRN = "[^a-zA-Z0-9]+";

  /**
   * Regex to format file name with invalid chars
   */
  public static final String INVALID_CHAR_PTRN_EXCLUD_SPACE = "[^a-zA-Z0-9 ]+";

  /** Constant for 0 rows in COoC WP page */
  public static final int COC_WP_EMPTY_ROW_COUNT = 0;

  /**
   * Constant for the EMR node id
   */
  public static final long EMR_NODE_ID = -6000L;

  /** Seperator for framing Primary variant Resp WP Name for Qnaire Response */
  public static final String QNAIRE_RESP_PRIMARY_NAME_DELIMITER = ";";

  /** Deutsch File Name for simplified General Qnaire declaration html file. */
  public static final String SIMP_QNAIRE_DECLAR_FILE_NAME_DE = "simplified_qnaire_declaration.html";
  /** English File Name for simplified General Qnaire declaration html file. */
  public static final String SIMP_QNAIRE_DECLAR_FILE_NAME_EN = "simplified_qnaire_declaration_en.html";
  /**
   * simplified General Qnaire declaration.
   */
  public static final String SIMP_QNAIRE_DECLAR_FILE = "simplified_general_qnaire_declaration.zip";
  /**
   * Constant for OBD General Question Name
   */
  public static final String OBD_GENERAL_QUESTIONS = "OBD General Questionnaire";

  /** Simp Qnaire Resp Id. */
  public static final Long SIMP_QUES_RESP_ID = -1L;

  /** Simp Qnaire Resp NAME */
  public static final String SIMP_QUES_RESP_NAME = "SIMPLIFIED_QNAIRE";

  /**
   * Label not relevant for current release, still to be calibrated - Standard Comment
   */
  public static final String LABEL_NOT_RELEVANT_COMMENT =
      "Label not relevant for current release, still to be calibrated";

  /**
   * message that score cannot be set - Compli Failure
   */
  public static final String MSG_CANNOT_CHANGE_SCORE_COMPLI_FAILURE =
      "Enter the reason for a Compliance violation in the comment field before setting the score to 9.";

  /**
   * message that score cannot be set - Q-SSD Failure
   */
  public static final String MSG_CANNOT_CHANGE_SCORE_QSSD_FAILURE =
      "Enter the reason for a Q-SSD violation in the comment field before setting the score to 9.";

  /**
   * message that score cannot be set - Unfulfilled Rule
   */
  public static final String MSG_CANNOT_CHANGE_SCORE_UNFULFILLED_RULE =
      "Enter the reason for a rule violation in the comment field before setting the score to 9.";

  /**
   * message that score cannot be set - Multiple Records
   */
  public static final String MSG_CANNOT_CHANGE_SCORE_MULTIPLE_RECORDS =
      "Enter the reason for a Compliance/Q-SSD/Rule violation in the comment field before setting the score to 9.";

  /**
   * private constructor.
   */
  private ApicConstants() {
    // private constructor to prevent intantiation
  }


}

