/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

// ICDM-1703
/**
 * @author bru2cob
 */
public enum CDRReportExcelColumn {

                                  /**
                                   * Unique Instance for Single class implementation
                                   */
                                  INSTANCE;

  /**
   * Columns for Review result sheet display
   */
  protected static final String[] cdrReportSheetHeader = new String[] {
      // ICDM-2444
      ExcelClientConstants.COMPLIANCE_NAME,
      ExcelClientConstants.PARAMETER,
      ExcelClientConstants.FUNCTION,
      ExcelClientConstants.FC_VERSION,
      ExcelClientConstants.WP,
      ExcelClientConstants.RESP_TYPE,
      ExcelClientConstants.RESPONSIBILITY_STR,
      ExcelClientConstants.WP_FINISHED,
      ExcelClientConstants.PTYPE,
      ExcelClientConstants.IS_CODE_WORD,
      ExcelClientConstants.LATEST_A2L_VER,
      ExcelClientConstants.LATEST_FUNC_VER,
      ExcelClientConstants.QUESTIONNAIRE_STATUS,
      ExcelClientConstants.IS_REVIEWED,
      ExcelClientConstants.REVIEW_COMMENTS,
      // ICDM-2584
      ExcelClientConstants.REVIEW_TYPE,
      ExcelClientConstants.REVIEW_RESULT_DESCRIPTION,
      ExcelClientConstants.REVIEW_DATA };

  /**
   * @return the unique instance of this class
   */
  public static CDRReportExcelColumn getInstance() {
    return INSTANCE;
  }


}
