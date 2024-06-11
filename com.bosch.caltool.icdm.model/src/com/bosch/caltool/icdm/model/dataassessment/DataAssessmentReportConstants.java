/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.dataassessment;

/**
 * @author TRL1COB
 */
public class DataAssessmentReportConstants {

  private DataAssessmentReportConstants() {
    // Private constructor
  }

  public static final String SERIAL_NUM = "S.no";
  public static final String VAR_SUBVAR = "Variant/SubVariant";
  public static final String SUPER_GROUP = "Super Group";
  public static final String GROUP = "Group";
  public static final String NAME = "Name";
  public static final String DESCRIPTION = "Description";
  public static final String USED = "Used";
  public static final String VALUE = "Value";
  public static final String PART_NUMBER = "Part Number";
  public static final String SPEC_LINK = "Specification";
  public static final String ADDITIONAL_INFO_DESC = "Comment";
  public static final String VAR_SUBVAR_ID = "Var/SubVarID";
  public static final String ATTR_ID = "Attr_ID";
  public static final String UNIT = "Unit";
  public static final String ATTR_CREATED_DATE = "Attribute Creation Date";
  public static final String ATTRIBUTE_RH_ATTR_NAME = "Attr Name";
  public static final String ATTRIBUTE_RH_ATTR_DESCRIPTION = "Attr Description";
  public static final String ATTRIBUTE_RH_VALUE_DESCRIPTION = "Value Description";
  public static final String ATTRIBUTE_RH_VALUE_TYPE = "Value_Type";
  public static final String ATTRIBUTE_NORMALISED = "Normalised";
  public static final String ATTRIBUTE_RH_VALUE_ID = "Value_ID";
  public static final String VARIANT = "<VARIANT>";
  public static final String SUB_VARIANT = "<VARIANT_CODED>";
  public static final String VARIANT_NAME = "Variant";
  public static final String BASELINE_FILE_STATUS = "File Generation Status";

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

  public static final String EMPTY_STRING = "";

  public static final String WORK_PACKAGE_COL = "Work Package";
  public static final String RESPONSIBLE_COL = "Responsible";
  public static final String WP_READY_FOR_PROD_COL = "Overall Work Package Ready For Production";
  public static final String WP_FINISHED_COL = "Work Package Finished";
  public static final String QNAIRE_ANS_AND_BASELINED_COL = "Questionnaire answered and Baselined";
  public static final String PARAM_REVIEWED_COL = "Parameter Reviewed";
  public static final String HEX_FILE_EQUAL_REV_VAL_COL = "HEX File Equal to Reviewed Value";

  public static final String QNAIRE_COL = "Questionnaire";
  public static final String READY_FOR_PROD_COL = "Ready for Production";
  public static final String BASELINE_EXISTING_COL = "Baseline Existing";
  public static final String POSITIVE_ANS_COL = "Positive";
  public static final String NEGATIVE_ANS_COL = "Negative";
  public static final String NEUTRAL_ANS_COL = "Neutral";
  public static final String VERSION_NAME_COL = "Version Name";
  public static final String REVIEWED_BY_COL = "Reviewed By";
  public static final String REVIEWED_ON_COL = "Reviewed On";
  public static final String QNAIRE_LINK_COL = "Link";
  public static final String BASELINE_NAME_COL = "Baseline Name";
  public static final String REMARKS_COL = "Remarks";
  public static final String CREATED_ON_COL = "Created On";
  public static final String ASSESSMENT_TYPE_COL = "Assessment Type";
  public static final String CONSIDERED_PREVIOUS_REVIEWS = "Considered Reviews of previous PIDC Versions";

  // Compare HEX Related constants
  public static final String COMPLIANCE_NAME = "SSD Class Type";
  public static final String TYPE = "Type";
  public static final String PARAMETER = "Parameter";
  public static final String FUNCTION = "Function";
  public static final String FC_VERSION = "Function Version";
  public static final String WP = "WP";
  public static final String RESP = "RESP";
  public static final String RESP_TYPE = "Responsibility Type";
  public static final String WP_FINISHED = "WP Finished";
  public static final String LATEST_A2L_VER = "Latest A2L Version";
  public static final String LATEST_FUNC_VER = "Latest Function Version";
  public static final String QUESTIONNAIRE_STATUS = "Questionnaire Status";
  public static final String IS_REVIEWED = "Reviewed";
  public static final String EQUAL = "Equal";
  public static final String HEX_VALUE = "HEX Value";
  public static final String REVIEWED_VALUE = "Reviewed Value";
  public static final String COMPLI_RESULT = "Compli Result";
  public static final String REVIEW_SCORE = "Review Score";
  public static final String REVIEW_COMMENTS = "Latest Review Comments";

  public static final String HEX_FILE = "Hex File";
  public static final String A2LFILE = "A2L File";
  public static final String COMP_VARIANT = "Variant";
  public static final String TOTAL_PARAMS = "Total Number of Parameters";
  public static final String EQUAL_REVIEWED = "Equal and Reviewed";
  public static final String COMPLI_PARAMS = "Compli parameters in A2L";
  public static final String COMPLI_PARAMS_PASSED = "Compli parameters Passed";
  public static final String NUM_PARAM_BSH_RESP = "Number of Parameters in Bosch Responsiblity";
  public static final String NUM_PARAM_BSH_RESP_RVW = "Number of Parameters in Bosch Responsiblity Reviewed";
  public static final String PARAM_BSH_RESP_RVW = "Parameters in Bosch Responsibility Reviewed";
  public static final String PARAM_BSH_RESP_QNAIRE = "Parameters in Bosch Responsibility with Completed Questionnaire";
  public static final String QNAIRE_NEGATIVE_ANSWER = "Number of questionnaires with negative answer included";
  public static final String COMPARE_RESULTS = "Results Info";
  public static final String COUNT = "Count";

  // Combined Report specific constants
  public static final String LONG_NAME = "Long Name";
  public static final String WP_NAME = "Work Package";
  public static final String RESPONSIBILITY_STR = "Responsibility";
  public static final String RESPONSIBILITY_TYPE = "Responsibility Type";
  public static final String CLASS = "Class";
  public static final String IS_CODE_WORD = "Code Word";
  public static final String IS_BITWISE = "Bitwise";
  public static final String PARAMETER_HINT = "Parameter Hint";
  public static final String FC_NAME = "FC Name";
  public static final String LOWER_LIMIT = "Lower Limit";
  public static final String UPPER_LIMIT = "Upper Limit";
  public static final String BITWISE_LIMIT = "Bitwise Limit";
  public static final String RESULT = "Result";
  public static final String SECONDARY_RESULT = "Secondary Result";
  public static final String RULE = "Rule";
  public static final String READY_FOR_SERIES = "Ready for series";
  public static final String SCORE = "Score";
  public static final String SCORE_DESCRIPTION = "Score Description";
  public static final String COMMENT = "Comment";
  public static final String REFERENCE_VALUE = "Reference Value";
  public static final String REFERENCE_VALUE_UNIT = "Ref Val Unit";
  public static final String CHECKED_VALUE = "Checked Value";
  public static final String CHECKED_VALUE_UNIT = "Checked Value's Unit";
  public static final String EXACT_MATCH = "Exact Match";
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

  public static final String PROJECT = "Project";
  public static final String REVIEW_VARIANT = "Variant";
  public static final String REVIEW_CREATOR = "Review Creator";
  public static final String CALIBRATION_ENGINEER = "Calibration Engineer";
  public static final String AUDITOR = "Auditor";
  public static final String OTH_PARTICIPANTS = "Other participants";
  public static final String RVW_CREATION_DATE = "Reviewed Date";
  public static final String WORKPACKAGE = "Work Package";
  public static final String INTERNAL_FILES = "Internal Files";
  public static final String INPUT_FILES = "Input Files";
  public static final String PARENT_REVIEW = "Parent Review";
  public static final String REVIEW_TYPE = "Review Type";
  public static final String REVIEW_DESCRIPTION = "Review Description";
  public static final String REVIEW_STATUS = "Review Status";
  public static final String FILE_REVIEWED = "File Reviewed";
  public static final String FILES_ATTACHED = "Files Attached";
  public static final String FUNCTIONS_REVIEWWED = "Functions Reviewed";
  public static final String ATTRIBUTE_VALUES = "Attribute Values used in Review";
  public static final String EXPORT_OPTIONS = "Export Options";

  public static final String QNAIRE_STATUS = "Questionnaire Status";
  public static final String QNAIRE_STATUS_DETAILS = "Questionnaire Status Details";

  public static final String WRKPKG_STATUS = "Work Package Status";
  public static final String WRKPKG_STATUS_DETAILS = "Work Package Status Details";

}
