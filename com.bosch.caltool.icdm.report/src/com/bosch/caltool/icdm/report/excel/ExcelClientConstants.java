/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import com.bosch.caltool.icdm.report.common.ExcelCommonConstants;

/**
 * Constants for Excel Report at iCDM Client Layer
 *
 * @author and4cob
 */
public final class ExcelClientConstants {


  public static final String H_DIFF = "Diff";
  public static final String RH_PART_NUMBER = "Part Number";
  public static final String RH_SPEC_LINK = "Specification";
  // ICDM-1603
  public static final String RH_ADDITIONAL_INFO_DESC = "Comment";
  public static final String ATTRIBUTE_RH_VALUE_ID = "Value_ID";
  public static final String ATTRIBUTE_RH_ATTR_ID = "Attr_ID";
  public static final String ATTRIBUTE_RH_ATTR_NAME = "Attr Name";
  public static final String ATTRIBUTE_RH_ATTR_DESCRIPTION = "Attr Description";
  public static final String ATTRIBUTE_RH_VALUE_DESCRIPTION = "Value Description";
  public static final String ATTRIBUTE_RH_VALUE_TYPE = ExcelCommonConstants.ATTRIBUTE_RH_VALUE_TYPE;
  public static final String RH_VALUE = "Value";
  public static final String RH_UNIT = ExcelCommonConstants.RH_UNIT;
  public static final String ATTRIBUTE_RH_DELETED_FLAG = "Deleted Flag";
  public static final String H_SUPER_GROUP = "Super Group";
  public static final String H_GROUP = "Group";
  public static final String H_NAME = "Name";
  public static final String H_DESCRIPTION = "Description";
  public static final String H_USED = "Used";
  public static final String H_VALUE = RH_VALUE.concat("                                       ");
  public static final String RH_SUPER_GROUP = "Super Group";
  public static final String RH_GROUP = "Group";
  public static final String RH_NAME = "Name";
  public static final String RH_DESCRIPTION = "Description";
  public static final String RH_USED = ExcelCommonConstants.RH_USED;
  public static final String RH_MISSING_VALUE = "Missing Value";
  public static final String RH_SUB_VARIANT = "Variant/SubVariant";
  public static final String ATTR_NAME_ENG = "ATTR_NAME_ENG";
  public static final String SUPER_GROUP_NAME_ENG = "SUPER_GROUP_NAME_ENG";
  public static final String GROUP_NAME_ENG = "GROUP_NAME_ENG";
  public static final String VARIANT = "<VARIANT>";
  public static final String SUB_VARIANT = "<VARIANT_CODED>";
  public static final String ATTRIBUTES = "Attributes";
  public static final String VARIANTS = "Variants";
  public static final String TEMP = "Temp";
  public static final String YES = "YES";
  public static final String YES_SENTENCE_CASE = "Yes";
  public static final String NO_SENTENCE_CASE = "No";
  public static final String DIFF_MARKER = "X";
  public static final String COMP_VARIANT = "Variant";
  public static final String COMP_SUBVARIANT = "SubVariant";
  public static final String COMMENTS = "Comments";
  public static final String STATUS = "Status";
  public static final String IMPORT_STATUS = "Import Status";
  public static final String FC_NAME = "FC Name";

  /**
   * ICDM-2444
   */
  public static final String COMPLIANCE_NAME = "SSD Class Type";
  public static final String PARAMETER = "Parameter";
  public static final String LOWER_LIMIT = "Lower Limit";
  public static final String UPPER_LIMIT = "Upper Limit";
  // ICDM-1928
  public static final String BITWISE_LIMIT = "Bitwise Limit";
  public static final String IS_BITWISE = "Bitwise";
  public static final String RESULT = "Result";

  // Task 241544
  public static final String SECONDARY_RESULT = "Secondary Result";
  public static final String RULE = "Rule";
  public static final String READY_FOR_SERIES = "Ready for series";
  public static final String SCORE = "Score";
  public static final String SCORE_DESCRIPTION = "Score Description";
  public static final String COMMENT = "Comment / Reason for Rule Violation";
  public static final String LONG_NAME = "Long Name";
  // 496338 - Add WP, Resp columns in NAT table in Review Result Editor
  public static final String WP_NAME = "Work Package";
  public static final String TYPE = "Type";
  public static final String CLASS = "Class";
  public static final String IS_CODE_WORD = "Code Word";
  public static final String REFERENCE_VALUE = "Reference Value";
  public static final String PARENT_REFERENCE_VALUE = "Parent Reference Value";
  public static final String PARENT_CHECKED_VALUE = "Parent Checked Value";
  public static final String HINT = "Hint";
  public static final String CHECKED_VALUE = "Checked Value";
  public static final String PROJECT = "Project";
  public static final String A2LFILE = "A2L File";
  public static final String CALIBRATION_ENGINEER = "Calibration Engineer";
  public static final String AUDITOR = "Auditor";
  public static final String OTH_PARTICIPANTS = "Other participants";
  public static final String WORKPACKAGE = "WorkPackage";
  public static final String FUNCTIONS_REVIEWWED = "Functions Reviewed";
  public static final String FILE_REVIEWED = "File Reviewed";
  public static final String REVIEW_VARIANT = "Variant";
  public static final String REFERENCE_VALUE_UNIT = "Ref Val Unit";
  public static final String CHECKED_VALUE_UNIT = "Checked Value's Unit";
  public static final String PARENT_REVIEW = "Parent Review";
  public static final String ATTRIBUTE_NORMALISED = "Normalised";
  public static final String FUNCTION = "Function";
  public static final String FILE_NAME = "File Name";
  public static final String REVIEW_STATUS = "Review Status";
  // Icdm-738 Show lab fun files in CDR result
  public static final String INPUT_FILES = "Input Files";
  public static final String REVIEW_DESCRIPTION = "Review Description";
  public static final String INTERNAL_FILES = "Internal Files";
  public static final String FILES_ATTACHED = "Files Attached";
  public static final String FC_VERSION = "Function Version";
  public static final String RH_ATTRIBUTES = "Attribute";
  public static final String RH_ATTRIBUTES_ID = "Attribute Id";

  // ICDM-2230
  public static final String RH_ATTRIBUTES_CREATION_DATE = "Attribute Creation Date";
  public static final String RH_USAGE = ExcelCommonConstants.RH_USAGE;
  // ICDM-2345
  public static final String RVW_CREATION_DATE = "Reviewed Date";

  // ICDM-876
  public static final String REVIEW_TYPE = "Review Type";

  public static final int COLUMN_NUM_ZERO = 0;
  public static final int COLUMN_NUM_ONE = 1;
  public static final int COLUMN_NUM_TWO = 2;
  public static final int COLUMN_NUM_THREE = 3;
  public static final int COLUMN_NUM_FOUR = 4;
  public static final int COLUMN_NUM_FIVE = 5;
  public static final int COLUMN_NUM_SIX = 6;
  public static final int COLUMN_NUM_SEVEN = 7;
  public static final int COLUMN_NUM_EIGHT = 8;
  public static final int COLUMN_NUM_NINE = 9;
  public static final int COLUMN_NUM_TEN = 10;
  public static final int COLUMN_NUM_ELEVEN = 11;
  public static final int COLUMN_NUM_TWELEVE = 12;
  public static final int COLUMN_NUM_THIRTEEN = 13;
  public static final int COLUMN_NUM_FOURTEEN = 14;
  public static final int COLUMN_NUM_FIFTEEN = 15;
  public static final int COLUMN_NUM_SIXTEEN = 16;
  public static final int COLUMN_NUM_SEVENTEEN = 17;
  public static final int COLUMN_NUM_EIGHTEEN = 18;
  public static final int COLUMN_NUM_NINETEEN = 19;
  public static final int COLUMN_NUM_TWENTY = 20;

  public static final int ROW_NUM_ZERO = 0;
  public static final int ROW_NUM_ONE = 1;
  public static final int ROW_NUM_TWO = 2;
  public static final int ROW_NUM_THREE = 3;

  public static final int LEGEND_START_ROW = 1;

  public static final int COMMENT_HEIGHT_FIVE = 5;
  public static final int COMMENT_WIDTH_FOUR = 4;

  public static final String ATTRIBUTE_RH_MANDATORY = "Mandatory(Default)";
  public static final String ATTRIBUTE_FORMAT = "Format";
  public static final String RH_VALUE_CLEARING_STATUS = "Clearing Status";
  public static final String DELETED_FLAG = "Deleted";
  // Icdm-893 new Columns for attr Export
  public static final String VAL_EXT = "Value External";
  // Icdm-1004 modified the Constant Value
  public static final String CHARACTERISTIC = "Attribute Class";
  public static final String ATTR_EXT = "Attribute External";
  public static final String CHARVALUE = "Value Class";

  /**
   * Icdm-1215- UI Changes for Attr Values Constant for Attribute Values
   */
  public static final String ATTRIBUTE_VALUES = "Attribute Values used in Review";
  public static final String RESP = "RESP";

  // 230083
  public static final String WP = "WP";
  public static final String RESP_TYPE = "Responsibility Type";
  // 230083
  public static final String RESPONSIBILITY_STR = "Responsibility";
  public static final String WP_FINISHED = "WP Finished";

  public static final String PTYPE = "Type";
  public static final String LATEST_A2L_VER = "Latest A2L Version";
  public static final String LATEST_FUNC_VER = "Latest Function Version";
  public static final String IS_REVIEWED = "Reviewed";
  public static final String REVIEW_RESULT_DESCRIPTION = "Review Description";
  public static final String QUESTIONNAIRE_STATUS = "Questionnaire Status";
  public static final String REVIEW_DATA = "Review Data";
  public static final String EXACT_MATCH = "Exact Match";

  public static final String EXPORT_OPTIONS = "Export Options";

  /**
   * Icdm-1539
   */
  public static final String DEFAULT_RULE = "Default Rule";
  /**
   * ICDM-1746 constant for review creator
   */
  public static final String REVIEW_CREATOR = "Review Creator";
  /**
   * index1 constant
   */
  public static final int INDEX_1_CONSTANT = 4;

  /**
   * index2 constant
   */
  public static final int INDEX_2_CONSTANT = 9;

  /**
   * index constant
   */
  public static final int INDEX_CONSTANT = 4;
  /**
   * ICDM-2229
   */
  public static final String RH_ATTR_CREATED_DATE = "Attribute Creation Date";


  public static final String EQUAL = "Equal";

  public static final String COMPLI_RESULT = "COMPLI_RESULT";


  public static final String TOTAL_PARAMS = "Total Number of Parameters:";
  public static final String REVIEWED_PARAMS = "Reviewed Parameters:";
  public static final String EQUAL_HEX_CDFX = "Equal in HEX and Review:";
  public static final String EQUAL_REVIEWED = "Equal and Reviewed:";
  public static final String HEX_FILE = "Hex File";
  // ICDM-2537
  public static final String MODIFIED_USER = "Modified User";
  public static final String MODIFIED_DATE = "Modified Date";
  public static final String REMARKS = "Remarks";
  public static final String REVISION_ID = "Rule Revision";
  public static final String TOTAL_PARAMS_IN_A2L = "Total Parameters in A2L";
  public static final String COMPLI_PARAMS = "Compli parameters in A2L";
  public static final String COMPLI_PARAMS_FAILED = "Compli parameters Failed";
  public static final String COMPLI_PARAMS_PASSED = "Compli parameters Passed";
  public static final String REVIEWED_NOT_EQUAL = "Parameters Reviewed Not Equal";
  public static final String PARAMS_FILTERED = "Filtered Parameters";
  public static final String PARAMS_FILTERED_REV = "Fltered Parameters Reviewed";
  public static final String PARAMS_FILTERED_NOT_REV = "Filtered Parameters Reviewed Not equal";

  public static final String NUM_PARAM_BSH_RESP = "Number of Parameters in Bosch Responsiblity";
  public static final String NUM_PARAM_BSH_RESP_RVW = "Number of Parameters in Bosch Responsiblity Reviewed";
  public static final String PARAM_BSH_RESP_RVW = "Parameters in Bosch Responsibility Reviewed";
  public static final String PARAM_BSH_RESP_QNAIRE = "Parameters in Bosch Responsibility with Completed Questionnaire";
  public static final String QNAIRE_NEGATIVE_ANSWER = "Number of questionnaires with negative answer included";
  public static final String CONSIDERED_PREVIOUS_REVIEWS = "Considered Reviews of previous PIDC Versions";

  public static final String HEX_VALUE = "HEX Value";
  public static final String REVIEWED_VALUE = "Reviewed Value";
  public static final String REVIEW_COMMENTS = "Latest Review Comments";

  public static final String REVIEW_SCORE = "Review Score";


  // Task 424716

  public static final String PARAMETER_NAME = "Parameter Name";
  public static final String FUNCTION_NAME = "Function Name";
  public static final String FUNCTION_VERSION = "Function Version";
  public static final String VARIANT_GROUP = "Variant Group";
  public static final String RESPONSBILITIES = "Responsibilities";
  public static final String BC = "BC";
  public static final String RESPONSIBILITY_TYPE = "Responsibility Type";
  public static final String DEPENDENT_LABEL = "Dependent Label(Y/N)";
  public static final String READ_ONLY_LABEL = "Read only Label(Y/N)";


  // 560483 - Export all Columns available.Shown and Hidden Columns

  public static final String PARAMETER_HINT = "Parameter Hint";
  public static final String IMPORTED_VALUE = "Imported Value";
  public static final String CDFX_STATUS = "CDFX_Status";
  public static final String CDFX_USER = "CDFX_User";
  public static final String CDFX_DATE = "CDFX_Date";
  public static final String CDFX_WORK_PACKAGE = "CDFX_Work Package";
  public static final String CDFX_PROJECT = "CDFX_Project";
  public static final String CDFX_TARGET_VARIANT = "CDFX_Target Variant";
  public static final String CDFX_TEST_OBJECT = "CDFX_Test Object";
  public static final String CDFX_PROGRAM_IDENTIFIER = "CDFX_Program Identifier";
  public static final String CDFX_DATA_IDENTIFIER = "CDFX_Data Identifier";
  public static final String CDFX_REMARK = "CDFX_Remark";
  public static final String REF_VAL_MATURITY_LEVEL = "Ref Val Maturity Level";

  public static final String PARAMETER_TYPE = "Parameter Type";
  public static final String PARAMETER_RESP = "Parameter Responsible";
  public static final String SYSTEM_ELEMENT = "System Element";
  public static final String HW_COMPONENT = "HW Component";


  /**
   * Private constructor for utility class
   */
  private ExcelClientConstants() {
    // Private constructor for utility class
  }
}
