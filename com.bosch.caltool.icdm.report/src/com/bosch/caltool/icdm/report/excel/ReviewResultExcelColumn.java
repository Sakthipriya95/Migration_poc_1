/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;


/**
 * @author bru2cob
 */
public enum ReviewResultExcelColumn {

                                     /**
                                      * Unique Instance for Single class implementation
                                      */
                                     INSTANCE;

  /**
   * Auto filter range for Review Result sheet
   */
  final String REVIEW_RESULT_AUTOFILTERRANGE = "A2:S";


  /**
   * @return the unique instance of this class
   */
  public static ReviewResultExcelColumn getInstance() {
    return INSTANCE;
  }

  /**
   * Columns for Review result sheet display
   */
  public static final String[] resultSheetHeader = new String[] {
      // ICDM-2444
      ExcelClientConstants.COMPLIANCE_NAME,
      ExcelClientConstants.PARAMETER,
      ExcelClientConstants.LONG_NAME,
      // 496338 - Add WP, Resp columns in NAT table in Review Result Editor
      ExcelClientConstants.WP_NAME,
      ExcelClientConstants.RESPONSIBILITY_STR,
      ExcelClientConstants.RESPONSIBILITY_TYPE,
      ExcelClientConstants.TYPE,
      ExcelClientConstants.CLASS,
      ExcelClientConstants.IS_CODE_WORD,
      ExcelClientConstants.IS_BITWISE,
      ExcelClientConstants.PARAMETER_HINT,
      ExcelClientConstants.FC_NAME,
      ExcelClientConstants.LOWER_LIMIT,
      ExcelClientConstants.UPPER_LIMIT,
      ExcelClientConstants.BITWISE_LIMIT,
      ExcelClientConstants.READY_FOR_SERIES,
      ExcelClientConstants.REFERENCE_VALUE,
      ExcelClientConstants.REFERENCE_VALUE_UNIT,
      ExcelClientConstants.EXACT_MATCH,
      ExcelClientConstants.CHECKED_VALUE,
      ExcelClientConstants.PARENT_CHECKED_VALUE,
      ExcelClientConstants.PARENT_REFERENCE_VALUE,
      ExcelClientConstants.CHECKED_VALUE_UNIT,
      ExcelClientConstants.IMPORTED_VALUE,
      ExcelClientConstants.RESULT,

      // Task 241544
      ExcelClientConstants.SECONDARY_RESULT,
      ExcelClientConstants.SCORE,
      ExcelClientConstants.SCORE_DESCRIPTION,
      ExcelClientConstants.COMMENT,


      ExcelClientConstants.CDFX_STATUS,
      ExcelClientConstants.CDFX_USER,
      ExcelClientConstants.CDFX_DATE,
      ExcelClientConstants.CDFX_WORK_PACKAGE,
      ExcelClientConstants.CDFX_PROJECT,
      ExcelClientConstants.CDFX_TARGET_VARIANT,
      ExcelClientConstants.CDFX_TEST_OBJECT,
      ExcelClientConstants.CDFX_PROGRAM_IDENTIFIER,
      ExcelClientConstants.CDFX_DATA_IDENTIFIER,
      ExcelClientConstants.CDFX_REMARK,
      ExcelClientConstants.REF_VAL_MATURITY_LEVEL


  };
  /**
   * Columns for Review info sheet display // Icdm-738 Show lab fun files in CDR result
   */
  public static final String[] reviewInfoSheetHeader = new String[] {
      ExcelClientConstants.PROJECT,
      ExcelClientConstants.REVIEW_VARIANT,
      ExcelClientConstants.A2LFILE,
      ExcelClientConstants.REVIEW_CREATOR,
      ExcelClientConstants.CALIBRATION_ENGINEER,
      ExcelClientConstants.AUDITOR,
      ExcelClientConstants.OTH_PARTICIPANTS,
      ExcelClientConstants.RVW_CREATION_DATE, /* ICDM-2345 */
      ExcelClientConstants.WORKPACKAGE,
      ExcelClientConstants.INTERNAL_FILES,
      ExcelClientConstants.INPUT_FILES,
      ExcelClientConstants.PARENT_REVIEW,
      ExcelClientConstants.REVIEW_TYPE, /* ICDM 617 */
      ExcelClientConstants.REVIEW_DESCRIPTION,
      ExcelClientConstants.REVIEW_STATUS /* ICDM 668 */,
      ExcelClientConstants.FILE_REVIEWED,
      ExcelClientConstants.FILES_ATTACHED,
      ExcelClientConstants.FUNCTIONS_REVIEWWED,
      ExcelClientConstants.ATTRIBUTE_VALUES,
      ExcelClientConstants.EXPORT_OPTIONS };
}
