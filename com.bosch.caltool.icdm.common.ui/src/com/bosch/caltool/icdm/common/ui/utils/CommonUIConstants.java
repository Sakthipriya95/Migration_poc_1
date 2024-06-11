/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.utils;

import java.io.File;

import org.eclipse.swt.SWT;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Common UI Constants Defines constants used in UI objects - View, Editors, Dialogs
 */
public final class CommonUIConstants {

  /**
   * Constant defining the PROPERTIES view
   */
  public static final String PROPERTIES_VIEW = "org.eclipse.ui.views.PropertySheet";

  /**
   * Constant defining the Progress view
   */
  public static final String PROGRESS_VIEW = "org.eclipse.ui.views.ProgressView";

  /**
   * Constanst defining Review data Info title name
   */
  public static final String REVIEW_DATA_TITLE = "Review Information";
  /**
   * Constanst defining Review data Info title name
   */
  public static final String REVIEW_RESULT_TITLE = "Review Statistics";

  /**
   * Rule version title
   */
  public static final String RULE_VERSION_TITLE = "Rules History";

  /**
   * Constanst defining Rule data Info title name
   */
  public static final String RULE_DATA_TITLE = "Rule Information";
  /**
   * Constant for storing PIDC tree variants node display
   */
  public static final String PIDC_TREE_SHOW_VARNODE = "pidcTreeShowVarnode";
  /**
   * Constant for storing PIDC tree variants node display
   */
  public static final String PIDC_TREE_HIDE_EMPTY_NODES = "pidcTreeHideEmptyNodes";
  /**
   * Constant for displaying inactive A2L Files in PIDC Tree
   */
  public static final String PIDC_TREE_DISPLAY_INACTIVE_A2L_FILES = "pidcTreeDisplayInactiveA2lFiles";

  /**
   * Constant for displaying Deleted Questionnaire Reponse in PIDC Tree
   */
  public static final String PIDC_TREE_DISPLAY_DELETED_QNAIRE_RESP = "pidcTreeDisplayDeletedQnaireResp";

  /**
   * Constant for displaying Deleted PIDC Variant in PIDC Tree
   */
  public static final String PIDC_TREE_DISPLAY_DELETED_PIDC_VARIANT = "pidcTreeDisplayDeletedPidcVariant";

  /**
   * Review Data View ID
   */
  public static final String REVIEW_DATA_VIEW_ID = "com.bosch.caltool.icdm.common.ui.views.ReviewDataViewPart";

  /**
   * View ID for Rules History view
   */
  public static final String RULE_HISTORY_VIEW_ID = "com.bosch.caltool.icdm.ruleseditor.views.RulesHistoryViewPart";


  /**
   * Constanst defining Lower limit value name
   */
  public static final String LOWER_LIMIT_VALUE = "Lower Limit";
  /**
   * Constanst defining upper limit value name
   */
  public static final String UPPER_LIMIT_VALUE = "Upper Limit";
  /**
   * Constanst defining unit value name
   */
  public static final String UNIT_VALUE = "Unit";
  /**
   * Constanst defining ref value name
   */
  public static final String REF_VALUE = "Reference Value";

  /**
   * Constanst defining hint name
   */
  public static final String HINT_VALUE = "Hint";
  /**
   * Constant for defining remarks
   */
  public static final String REMARK_VALUE = "Remarks";

  /** Not Finished for Qnaire response */
  public static final String NOT_FINISHED = "Not Finished";

  /** Finished for Qnaire response */
  public static final String FINISHED = "Finished";

  /**
   * Constant for defining remarks
   */
  public static final String REMARK_UNICODE = "Remarks (Unicode)";
  /**
   * Constant defining the Series Statistics view ID
   */
  // ICDM-218
  // ICdm-521
  public static final String SERIES_STATISTICS_VIEW_ID =
      "com.bosch.caltool.icdm.common.ui.views.SeriesStatisticsViewPart";
  /**
   * Constant defining the FC/BC Usage view ID
   */
  // ICDM-218
  // ICdm-521
  public static final String FCBC_USAGE_VIEW_ID = "com.bosch.caltool.icdm.common.ui.views.FCBCUsageViewPart";

  /**
   * Access Rights Page title
   */
  public static final String ACCESS_RIGHTS_PAGE = "Access Rights";

  /**
   * The perspective id for the Calibration Data Review
   */
  public static final String ID_PERSP_CDR = "com.bosch.caltool.icdm.product.perspectives.CalibrationDataReview";

  /**
   * The perspective id for the Component package
   */
  public static final String ID_PERSP_COMP_PKG = "com.bosch.caltool.icdm.product.perspectives.ComponentPackages";

  /**
   * Constant defining the TypeFilterText label
   */
  public static final String TYPE_FILTER_TEXT_LABEL = "TypeFilterText.label";

  /**
   * Constant defining the empty string
   */
  // ICDM-351
  public static final String EMPTY_STRING = "";
  /**
   * Defines GridViewerColumn index 10
   */
  public static final int COLUMN_INDEX_11 = 11;
  /**
   * Defines GridViewerColumn index 10
   */
  public static final int COLUMN_INDEX_12 = 12;
  /**
   * Defines GridViewerColumn index 10
   */
  public static final int COLUMN_INDEX_13 = 13;
  /**
   * Defines GridViewerColumn index 10
   */
  public static final int COLUMN_INDEX_14 = 14;

  /**
   * Defines GridViewerColumn index 10
   */
  public static final int COLUMN_INDEX_10 = 10;
  /**
   * Defines GridViewerColumn index 9
   */
  public static final int COLUMN_INDEX_9 = 9;
  /**
   * Defines GridViewerColumn index 8
   */
  public static final int COLUMN_INDEX_8 = 8;
  /**
   * Defines GridViewerColumn index 7
   */
  public static final int COLUMN_INDEX_7 = 7;
  /**
   * Defines GridViewerColumn index 6
   */
  public static final int COLUMN_INDEX_6 = 6;
  /**
   * Defines GridViewerColumn index 5
   */
  public static final int COLUMN_INDEX_5 = 5;
  /**
   * Defines GridViewerColumn index 4
   */
  public static final int COLUMN_INDEX_4 = 4;
  /**
   * Defines GridViewerColumn index 3
   */
  public static final int COLUMN_INDEX_3 = 3;
  /**
   * Defines GridViewerColumn index 2
   */
  public static final int COLUMN_INDEX_2 = 2;
  /**
   * Defines GridViewerColumn index 1
   */
  public static final int COLUMN_INDEX_1 = 1;
  /**
   * Defines GridViewerColumn index 0
   */
  public static final int COLUMN_INDEX_0 = 0;
  /**
   * cell value for Not applicable cells in qnaire response summary page
   */
  public static final String CELL_VALUE_NOT_APPLICABLE = "<NOT APPLICABLE>";
  // ICdm-521
  /**
   * Constanst defining All value name
   */
  public static final String STATISTICAL_VALUES = "All Values Used in Statistics";
  /**
   * Constanst defining Max value name
   */
  public static final String MAX_VALUE = "Max Value";
  /**
   * Constanst defining Peak value name
   */
  public static final String PEAK_VALUE = "Most Frequent Value";
  /**
   * Constanst defining Average name
   */
  public static final String AVERAGE = "Average";
  /**
   * Constanst defining Median name
   */
  public static final String MEDIAN = "Median";
  /**
   * Constanst defining Lower quartile name
   */
  public static final String LOWER_QUARTILE = "Lower Quartile";
  /**
   * Constanst defining Upper quartile name
   */
  public static final String UPPER_QUARTILE = "Upper Quartile";
  /**
   * Constanst defining Used in datasets name
   */
  public static final String USED_IN_DATASETS = "Used in Datasets";
  /**
   * Constanst defining Min value name
   */
  public static final String MIN_VALUE = "Min Value";
  /**
   * Constanst defining Different values name
   */
  public static final String DIFFERENT_VALUES = "Different Values";
  /**
   * Constanst defining graph name
   */
  public static final String GRAPH = "Graph";
  /**
   * Constanst defining table name
   */
  public static final String TABLE = "Table";
  /**
   * Constanst defining Datasets name
   */
  public static final String DATASETS = "Datasets";
  /**
   * //Icdm-674 create the Serial Index Column for the series statistics view part table Constant defining serial Index
   */
  public static final String SERIALINDEX = "Sl. No";
  /**
   * Constanst defining unit name
   */
  public static final String UNIT = "Unit";
  /**
   * Constanst defining value name
   */
  public static final String VALUE = "Value";
  /**
   * Constanst defining Series Statistics Info title name
   */
  // ICDM-218
  public static final String SERIES_STATISTICS_TITLE = "Statistical Information";
  /**
   * Constanst defining Table/Graph name
   */
  public static final String TABLE_GRAPH = "Table/Graph";

  // ICDM 533
  /**
   * vCDM APRJ link
   */
  public static final String VCDM_APRJ_PATH = "easee:CDM,";


  /**
   * Check value label
   */
  public static final String CHECK_VALUE = "Checked Value";

  /**
   * The HEX Value label
   */
  // ICDM-2498
  public static final String HEX_VALUE = "HEX Value";

  /**
   * The Reviewed Value label
   */
  // ICDM-2498
  public static final String RVWD_VALUE = "Reviewed Value";

  /**
   * Reference value in graph color - red color
   */
  public static final int REFVAL_GRAPH_COLOR_INDX = 1;

  /**
   * Check value in graph color - blue color
   */
  public static final int CHKVAL_GRAPH_COLOR_INDX = 0;

  // ICDM-705
  /**
   * Key constant for renaming
   */
  public static final int KEY_RENAME = SWT.F2;

  /**
   * Key constant for control
   */
  public static final int KEY_CONTROL = SWT.CONTROL;

  /**
   * Key constant for copy
   */
  public static final char KEY_COPY = 'c';

  /**
   * Key constant for paste
   */
  public static final char KEY_PASTE = 'v';

  /**
   * Key constant for delete
   */
  public static final int KEY_DELETE = SWT.DEL;

  /**
   * Key constant for control
   */
  public static final int KEY_CTRL = SWT.CTRL;

  /**
   * Shared path prefix
   */
  public static final String SHARED_PATH_PREFIX = "\\";
  /**
   * file path prefix
   */
  public static final String FILE_PATH_PREFIX = "file:";
  /**
   * http prefix
   */
  public static final String HTTP_PREFIX = "http://";
  /**
   * https prefix
   */
  public static final String HTTPS_PREFIX = "https://";

  /**
   * ICDM-1110 mail prefix
   */
  public static final String MAIL_PREFIX = "mailto:";

  /**
   * Display text to use current value
   */
  public static final String DISP_TEXT_USE_CUR_VAL = "<Use Current Value>";
  // ICDM-1173
  /**
   * Constant for exact match-yes
   */
  public static final String EXACT_MATCH_YES = "Yes";
  /**
   * Constant for exact match-no
   */
  public static final String EXACT_MATCH_NO = "No";
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
   * paco extension
   */
  public static final String PACO_EXTN = "XML";
  /**
   * cdfx extension
   */
  public static final String CDFX_EXTN = "CDFX";
  /**
   * dcm extension
   */
  public static final String DCM_EXTN = "DCM";
  /**
   * constant for file names in file dialog
   */
  public static final String[] FILE_NAMES = new String[] {
      "All Calibration Data Files (*.CDFX,*.DCM, *.XML,*.XLS,*.XLSX)",
      "CDFx Files (*.CDFX)",
      "DCM Files (*.DCM)",
      "PaCo Files (*.XML)" };


  /**
   * constant for file names in file dialog
   */
  public static final String[] FILE_NAMES_RULE_IMPORT = new String[] {
      "All Calibration Data Files (*.CDFX,*.DCM, *.XML,*.XLS,*.XLSX)",
      "CDFx Files (*.CDFX)",
      "DCM Files (*.DCM)",
      "PaCo Files (*.XML),",
      "Excel file(*.XLS)",
      "Excel 2007 Workbook(*.XLSX)" };


  /**
   * constant for file extensions in file dialog
   */
  public static final String[] FILE_EXTENSIONS_IMPORT =
      new String[] { "*.CDFx;*.DCM;*.xml;*.xls;*.xlsx", "*.CDFx", "*.DCM", "*.xml", "*.xls", "*.xlsx" };
  /**
   * constant for file extensions in file dialog
   */
  public static final String[] FILE_EXTENSIONS = new String[] { "*.CDFx;*.DCM;*.xml", "*.CDFx", "*.DCM", "*.xml" };
  /**
   * constant to store scratch pad import file name
   */
  public static final String IMPORT_TO_SCRATCH_PAD_NAME = "importToScratchPadName";

  /**
   * constant to store scratch pad file extn
   */
  public static final String IMPORT_TO_SCRATCH_PAD_EXTN = "importToScratchPadExtn";

  /**
   * Constant to display parent check value
   */
  public static final String TXT_PARENT_CHECK_VALUE = "Parent Checked Value";
  /**
   * Constant to display parent ref value
   */
  public static final String TXT_PARENT_REF_VALUE = "Parent Reference Value";
  // ICDM-1293
  /**
   * constant to store caldata file extn
   */
  public static final String IMPORT_CALDATA_FILE_EXTN = "importCalDataFileExtn";

  /**
   * constant to store caldata file name
   */
  public static final String IMPORT_CALDATA_FILE_NAME = "importCalDataFileName";

  /**
   * constant TEXT_FILTER
   */
  public static final String TEXT_FILTER = "type filter text";
  /**
   * item selected in a collection as one
   */
  public static final int SINGLE_SELECTION = 1;
  /**
   * Default tree collapse level
   */
  public static final int DEF_TREE_COLLAPSE_LEVEL = 2;
  /**
   * Default tree expand level
   */
  public static final int DEF_TREE_EXPAND_LEVEL = 2;

  // ICDM-1436
  /**
   * Defines constant for other versions node in the pidc tree
   */
  public static final String NODE_TYPE_OTHER_VERSIONS = "Other_Versions";

  /**
   * Defines constant for other versions node display in the pidc tree
   */
  public static final String OTHER_VERSIONS_NODE_DISPLAY = "Other Versions";

  /**
   * Review Questionnaires
   */
  public static final String REVIEW_QUESTIONNAIRES = "Review Questionnaires";

  /**
   * Node type indicator for 'Review Results' node in the PIDC and Favorites trees
   */
  public static final String NODE_TYPE_REV_RES = "Review_Results";

  // iCDM-1982
  /**
   * Node type indicator for both 'Review Results' and 'Review Questionnaire' node in the PIDC and Favourites Trees
   */
  public static final String NODE_TYPE_NO_VARIANT = ApicConstants.DUMMY_VAR_NODE_NOVAR;

  // iCDM-1241
  /**
   * Defines constant for review result node display in the pidc tree
   */
  public static final String REVIEW_RESULTS_NODE_DISPLAY = "Review Results";

  /**
   * Nat table properties path
   */
  public static final String NAT_TABLE_PROPERTIES_PATH =
      CommonUtils.getICDMWorkspaceFilePath() + File.separator + "natTable.properties";


  /**
   * ICDM-1502 character constant for add link
   */
  public static final char CHAR_CONSTANT_FOR_ADD = 'a';

  /**
   * ICDM-1502 character constant for edit link
   */
  public static final char CHAR_CONSTANT_FOR_EDIT = 'e';

  /**
   * ICDM-1502 character constant for delete link
   */
  public static final char CHAR_CONSTANT_FOR_DELETE = 'd';


  /**
   * KEY Constant for shift key
   */
  public static final int KEY_SHIFT = SWT.SHIFT;

  /**
   * Filtered for Radio button
   */
  public static final String FILTERED = "Filtered";

  /**
   * Complete for Radio button
   */
  public static final String COMPLETE = "Complete";

  /**
   * Under score
   */
  public static final String UNDERSCORE = "_";

  /**
   * Dot
   */
  public static final String DOT = ".";

  /**
   * ending index of file name to be displayed
   */
  public static final int MAXIMUM_FILE_NAME_SIZE = 50;

  /**
   * Defines GridViewerColumn index 15
   */
  public static final int COLUMN_INDEX_15 = 15;

  // ICDM-2439
  /**
   * Defines GridViewerColumn index 16
   */
  public static final int COLUMN_INDEX_16 = 16;

  // ICDM-2439
  /**
   * Defines GridViewerColumn index 18
   */
  public static final int COLUMN_INDEX_18 = 18;

  // ICDM-2151
  /**
   * Defines GridViewerColumn index 17
   */
  public static final int COLUMN_INDEX_17 = 17;

  // ICDM-2605
  // Column index for Data Review Report page
  /**
   * SSD class column index in Data Review Report Page
   */
  public static final int SSD_CLASS_COL_INDEX = 0;

  /**
   * Parameter type column index in Data Review Report Page
   */
  public static final int PARAM_TYPE_COL_INDEX = 1;

  /**
   * Parameter name column index in Data Review Report Page
   */
  public static final int PARAM_NAME_COL_INDEX = 2;

  /**
   * Function name column index in Data Review Report Page
   */
  public static final int FUNC_COL_INDEX = 3;

  /**
   * Function version column index in Data Review Report Page
   */
  public static final int FUNC_VERS_COL_INDEX = 4;

  /**
   * Work Package column index in Data Review Report Page
   */
  public static final int WP_COL_INDEX = 5;

  /**
   *
   */
  public static final int RESP_TYPE_COL_INDEX = 6;
  /**
   * Responsibility column index in Data Review Report Page
   */
  public static final int RESPONSIBILITY_COL_INDEX = 7;

  /**
   * WP_Finished column index in Data Review Report Page
   */
  public static final int WP_FINISHED_COL_INDEX = 8;

  /**
   * Code Word column index in Data Review Report Page
   */
  public static final int CW_COL_INDEX = 9;

  /**
   * Latest a2l file column index in Data Review Report Page
   */
  public static final int LATEST_A2L_COL_INDEX = 10;

  /**
   * Latest function column index in Data Review Report Page
   */
  public static final int LATEST_FUNC_COL_INDEX = 11;

  /**
   * Reviewed info column index in Data Review Report Page
   */
  public static final int RVWD_COL_INDEX = 12;

  /**
   * NAT_TABLE group constant for T_Messages
   */
  public static final String NAT_TABLE_GROUP = "NAT_TABLE";

  /** Filter for Is Fc With Params - Yes */
  public static final String FILTER_IS_FC_WITH_PARAMS = "Is Fc with Params : Yes";

  /** Filter for Is Fc With Params - No */
  public static final String FILTER_IS_FC_WITH_PARAMS_NO = "Is Fc with Params : No";

  /** Filter for Is Fc in Sdom - Yes */
  public static final String FILTER_IS_FC_IN_SDOM_YES = "Is Fc in SDOM : Yes";

  /** Filter for Is Fc in Sdom - No */
  public static final String FILTER_IS_FC_IN_SDOM_NO = "Is Fc in SDOM : No";

  public static final String FILTER_IS_IN_ICDM_A2L_NO = "Is in iCDM A2L : No";

  public static final String FILTER_IS_IN_ICDM_A2L_YES = "Is in iCDM A2L : Yes";

  public static final String FILTER_IS_DELETED_YES = "is Deleted : Yes";

  public static final String FILTER_IS_DELETED_NO = "is Deleted : No";
  public static final String FILTER_IS_CONTACT_ASSIGNED_YES = "is Contact Assigned : Yes";

  public static final String FILTER_IS_CONTACT_ASSIGNED_NO = "is Contact Assigned : No";

  public static final String FILTER_IS_RESPONSIBLE_ASSIGNED_YES = "Responsibility : Non Default and Locally Defined";

  public static final String FILTER_IS_RESPONSIBLE_ASSIGNED_NO = "Responsibility : Default";

  public static final String FILTER_IS_RESPONSIBLE_ASSIGNED_INHERITED = "Responsibility : Non Default and Inherited";

  public static final String FILTER_IS_NAME_AT_CUSTOMER_ASSIGNED_YES =
      "WP Name at Customer : Assigned and Locally Defined";

  public static final String FILTER_IS_NAME_AT_CUSTOMER_ASSIGNED_NO = "WP Name at Customer : Not Assigned";

  public static final String FILTER_IS_NAME_AT_CUSTOMER_INHERITED = "WP Name at Customer : Assigned and Inherited";

  public static final String FILTER_IS_WP_ASSIGNED_YES = "Work Package : Non Default";

  public static final String FILTER_IS_WP_ASSIGNED_NO = "Work Package : Default";

  public static final String FILTER_HAS_RELEVANT_PT_TYPE_YES = "has Relevant PT type : Yes";

  public static final String FILTER_HAS_RELEVANT_PT_TYPE_NO = "has Relevant PT type : No";

  public static final String FILTER_DEPENDENT_ATTRIBUTES = "Dependent Attributes";

  public static final String FILTER_NON_DEPENDENT_ATTRIBUTES = "Non Dependent Attributes";

  public static final String FILTER_MANDATORY = "Mandatory";

  public static final String FILTER_NON_MANDATORY = "Non-Mandatory";

  public static final String FILTER_VARIANT_ATTRIBUTES = "Variant Attributes";

  public static final String FILTER_VARIANT_COCWP = "Variant CoC WorkPackages";

  public static final String FILTER_NON_VARIANT_ATTRIBUTES = "Non Variant Attributes";

  public static final String FILTER_NON_VARIANT_COCWP = "Non Variant CoC WorkPackages";

  public static final String FILTER_VALUE_DEFINED = "Value defined";

  public static final String FILTER_VALUE_NOT_DEFINED = "No Value defined";

  public static final String FILTER_STRUCTURED_ATTRIBUTES = "Structured Attributes";

  public static final String FILTER_NOT_STRUCTURED_ATTRIBUTES = "Not Structured Attributes";

  public static final String FILTER_NEW_ATTRIBUTES = "New Attributes";

  public static final String FILTER_NEW_COC_WP = "New CoC WorkPackages";

  public static final String FILTER_GROUPED_ATTRIBUTES = "Grouped Attributes";

  public static final String FILTER_NOT_GROUPED_ATTRIBUTES = "Not Grouped Attributes";

  public static final String FILTER_COMPLIANCE_PARAMETERS = "Compliance Parameters";

  public static final String FILTER_NON_COMPLIANCE_PARAMETERS = "Non Compliance Parameters";

  /**
   * Tooltip for all coc wp predefined filter
   */
  public static final String FILTER_ALL_COC_WP = "All CoC Work Packages (Includes deleted CoC WP(s) also)";
  /**
   * QSSD Parameter filter
   */
  public static final String FILTER_QSSD_PARAMETERS = "QSSD Parameters";
  /**
   * Non QSSD Parameter filter
   */
  public static final String FILTER_NON_QSSD_PARAMETERS = "Non QSSD Parameters";

  public static final String FILTER_RIVET = "Class:Rivet";

  public static final String FILTER_NAIL = "Class:Nail";

  public static final String FILTER_SCREW = "Class:Screw";

  public static final String FILTER_CODEWORD_YES = "Codeword:Yes";

  public static final String FILTER_CODEWORD_NO = "Codeword:No";

  public static final String FILTER_UNDEFINED = "Class:Undefined";

  public static final String FILTER_STATUS_AVAILABLE = "Status : Available";

  public static final String FILTER_STATUS_NOT_AVAILABLE = "Status : Not Available";

  public static final String FILTER_VALUE_AVAILABLE = "Value : Available";

  public static final String FILTER_VALUE_NOT_AVAILABLE = "Value : Not Available";

  public static final String FILTER_LAB_PARAM = "Parameters selected by LAB file ";

  public static final String FILTER_NOT_LAB_PARAM = "Parameters not selected by LAB file ";

  public static final String ADD_RISK_EVAL = "Add Risk Evaluation";

  public static final String RISK_EVAL_TREE_SETTINGS = "Risk Evaluation Tree Settings";

  public static final String NATTABLE_RESET_STATE = "Reset columns to default";

  public static final String FILTER_IS_LOADED_WTO_ERROR = "Has Errors : No";

  public static final String FILTER_IS_LOADED_WITH_ERROR = "Has Errors : Yes";

  public static final String FILTER_SHOW_VARIANT_ONLY = "Show only entries of current Variant(s)";

  /**
   * Tooltip for Filter to show WP-Parameter mapping at selected variant group level
   */
  public static final String FILTER_SHOW_VARIANT_GROUP_ONLY = "Show parameters customized at variant group level";

  /**
   * Tooltip for Filter to show WP-Parameter mapping not at selected variant group level
   */
  public static final String FILTER_HIDE_VARIANT_GROUP_ONLY = "Show parameters NOT customized at variant group level";
  /**
   * Tooltip for Filter to show WP at selected variant group level
   */
  public static final String FILTER_SHOW_VARIANT_GROUP_WP_ONLY =
      " Show Work Packages customized at variant group level";
  /**
   * Tooltip for Filter to show WP at other variant group level
   */
  public static final String FILTER_SHOW_OTHER_VARIANT_GROUP_WP_ONLY =
      " Show Work package customization in other variant groups";
  /**
   * Tooltip for Filter to show WP at default or Pidc level
   */
  public static final String FILTER_SHOW_NON_VARIANT_GROUP_WP_ONLY = "Show non-customized Work Packages";

  public static final String FILTER_IMPORT_CODEX = "Import Codex File Wizard";

  public static final int RVW_COMMENT_COL_INDEX = 13;

  public static final int RVW_QNAIRE_STATUS_COL_INDEX = 14;

  public static final int RVW_DESCRIPTION_COL_INDEX = 15;

  public static final String CALDATA_PARAM_FILTER_PAGE = "Parameters";

  public static final String CALDATA_FUNCTION_FILTER_PAGE = "Functions";

  public static final String CALDATA_SYSCON_FILTER_PAGE = "System Constants";

  public static final String CALDATA_CUSTOMER_FILTER_PAGE = "Customers";

  public static final String CALDATA_PLATFORM_FILTER_PAGE = "ECU Platform";

  /**
   * Preferrence key to enable server connection state change systray notification
   */
  public static final String PREF_SHOW_CON_STATE_NOTIF_ENABLED = "connectionstate.change.notification.enabled";
  /**
   * Error message to be shown when the deleted A2LResponsibility is selected
   */
  public static final String DELETED_RESP_ERROR_MSG = "Selected user record is deleted.Please select any other user.";
  /**
   * Column number for unicode remarks in ruleset/function editor
   */
  public static final int PARAM_LISTPAGE_UNICODE_COL_NUM = 15;

  // Column index for Compare Hex with Cdfx page
  /**
   * SSD Class column index in Compare Hex with Cdfx page
   */
  public static final int SSD_CLASS_INDEX = 0;
  /**
   * Type column index in Compare Hex with Cdfx page
   */
  public static final int TYPE_INDEX = 1;
  /**
   * Parameter column index in Compare Hex with Cdfx page
   */
  public static final int PARAMTETER_INDEX = 2;
  /**
   * Function column index in Compare Hex with Cdfx page
   */
  public static final int FUNC_INDEX = 3;
  /**
   * Function Version column index in Compare Hex with Cdfx page
   */
  public static final int FUNC_VERS_INDEX = 4;
  /**
   * WP column index in Compare Hex with Cdfx page
   */
  public static final int WP_INDEX = 5;
  /**
   * Responsible Type column index in Compare Hex with Cdfx page
   */
  public static final int RESP_TYPE_INDEX = 6;
  /**
   * RESP column index in Compare Hex with Cdfx page
   */
  public static final int RESP_INDEX = 7;

  /**
   * RESP column index in Compare Hex with Cdfx page
   */
  public static final int WP_FINISHED_INDEX = 8;

  /**
   * Latest A2L Version column index in Compare Hex with Cdfx page
   */
  public static final int LATEST_A2L_VERS_INDEX = 9;
  /**
   * Latest Function version column index in Compare Hex with Cdfx page
   */
  public static final int LATEST_FUNC_VERS_INDEX = 10;
  /**
   * Questionnaire status column index in Compare Hex with Cdfx page
   */
  public static final int QNAIRE_STATUS = 11;
  /**
   * Reviewed column index in Compare Hex with Cdfx page
   */
  public static final int REVIEWED_INDEX = 12;
  /**
   * Equal column index in Compare Hex with Cdfx page
   */
  public static final int EQUAL_INDEX = 13;
  /**
   * HEX Value column index in Compare Hex with Cdfx page
   */
  public static final int HEX_VALUE_INDEX = 14;
  /**
   * Reviewed Value column index in Compare Hex with Cdfx page
   */
  public static final int REVIEWED_VALUE_INDEX = 15;
  /**
   * Compli result column index in Compare Hex with Cdfx page
   */
  public static final int COMPLI_RESULT_INDEX = 16;
  /**
   * Reviewed column index in Compare Hex with Cdfx page
   */
  public static final int REVIEW_SCORE_INDEX = 17;
  /**
   * Latest review comments column index in Compare Hex with Cdfx page
   */
  public static final int LATEST_RVW_CMNT_INDEX = 18;
  /**
   * Review Description column index in Compare Hex with Cdfx page
   */
  public static final int REVIEW_DESCRIPTION_INDEX = 19;

//Column index for workpackage page
  /**
   * Work Package column index in workpackage page
   */
  public static final int WORK_PACKAGE = 0;
  /**
   * Responsible column index in workpackage page
   */
  public static final int RESPONSIBLE = 1;
  /**
   * Responsible type column index in workpackage page
   */
  public static final int RESPONSIBLE_TYPE = 2;
  /**
   * Overall workpackage column index in workpackage page
   */
  public static final int OVERALL_WORK_PACKAGE = 3;
  /**
   * Workpackage column index in workpackage page
   */
  public static final int WORK_PACKAGE_FINISHED = 4;
  /**
   * Questionnaire column index in workpackage page
   */
  public static final int QUESTIONNAIRE_ANSWERED_AND_BASELINED = 5;
  /**
   * Parameter column index in workpackage page
   */
  public static final int PARAMETER_OF_RB_RESPONSIBILITY_REVIEWED = 6;
  /**
   * Hex file column index in workpackage page
   */
  public static final int HEX_FILE_EQUAL_TO_REVIEWED_DATA = 7;

//Column index for questionnaire results page
  /**
   * Work Package column index in questionnaire results page
   */
  public static final int Q_WORK_PACKAGE = 0;
  /**
   * Responsible column index in questionnaire results page
   */
  public static final int Q_RESPONSIBLE = 1;
  /**
   * Responsible type column index in questionnaire results page
   */
  public static final int Q_RESPONSIBLE_TYPE = 2;
  /**
   * Questionnaire column index in questionnaire results page
   */
  public static final int Q_QUESTIONNAIRE = 3;
  /**
   * Ready for production column index in questionnaire results page
   */
  public static final int Q_READY_FOR_PRODUCTION = 4;
  /**
   * Baseline existing column index in questionnaire results page
   */
  public static final int Q_BASELINE_EXISTING = 5;
  /**
   * Positive column index in questionnaire results page
   */
  public static final int Q_POSITIVE = 6;
  /**
   * Negative column index in questionnaire results page
   */
  public static final int Q_NEGATIVE = 7;
  /**
   * Neutral column index in questionnaire results page
   */
  public static final int Q_NEUTRAL = 8;
  /**
   * Version name column index in questionnaire results page
   */
  public static final int Q_VERSION_NAME = 9;
  /**
   * Reviewed by column index in questionnaire results page
   */
  public static final int Q_REVIEWED_BY = 10;
  /**
   * Reviewed on column index in questionnaire results page
   */
  public static final int Q_REVIEWED_ON = 11;
  /**
   * Result link column index in questionnaire results page
   */
  public static final int Q_RESULTS_LINK = 12;

  /**
   * Rev num of Qnaire resp working set version
   */
  public static final long WORKING_SET_REV_NUM = 0L;

  /**
   * flag value for ICC not Relevant
   */
  public static final String ICC_RELEVANT_N = "N";
  /**
   * flag value for ICC Relevant
   */
  public static final String ICC_RELEVANT_Y = "Y";
  /**
   * freeze nat table column in data review repost editor
   */

  public static final String NATTABLE_FREEZE_COLUMN = "Freeze";

  /**
   * Constant to denote exception has occurred
   */
  public static final String EXCEPTION = "Exception : ";

  /**
   * Constant for Context menu to send Rule Set Link via mail
   */
  public static final String SEND_RULE_LINK_OF_PARAMETER = "Send Rule Link of Parameter";


  /**
   * Constant for Context menu to send Rule Link via mail
   */
  public static final String SEND_RULE_SET_LINK = "Send Ruleset Link";

  /**
   * Label Constant for Yes
   */
  public static final String LABEL_FOR_YES = "Yes";
  /**
   * Label Constant for No
   */
  public static final String LABEL_FOR_NO = "No";
  /**
   * Label Constant for Not Applicable
   */
  public static final String NOT_APPLICABLE = "N/A";

  public static final String NO_REVIEW_RESULT_FOUND = "No Review Result found";

  /**
   * Private constructor to prevent instantiation
   */
  private CommonUIConstants() {
    // Private constructor to prevent instantiation
  }
}
