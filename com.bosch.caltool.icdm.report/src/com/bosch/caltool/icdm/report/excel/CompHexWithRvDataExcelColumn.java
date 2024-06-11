/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;


/**
 * @author bru2cob
 */
public enum CompHexWithRvDataExcelColumn {

                                          /**
                                           * Unique Instance for Single class implementation
                                           */
                                          INSTANCE;

  /**
   * Auto filter range for Hex Compare Result sheet
   */
  final String HEX_COMPARE_REPORT_AUTOFILTERRANGE = "A1:Q";


  /**
   * @return the unique instance of this class
   */
  public static CompHexWithRvDataExcelColumn getInstance() {
    return INSTANCE;
  }

  /**
   * Columns for compare report sheet display
   */
  protected static final String[] compareSheetHeader = new String[] {
      ExcelClientConstants.COMPLIANCE_NAME,
      ExcelClientConstants.TYPE,
      ExcelClientConstants.PARAMETER,
      ExcelClientConstants.FUNCTION,
      ExcelClientConstants.FC_VERSION,
      ExcelClientConstants.WP,
      ExcelClientConstants.RESP_TYPE,
      ExcelClientConstants.WP_FINISHED,
      ExcelClientConstants.RESP,
      ExcelClientConstants.LATEST_A2L_VER,
      ExcelClientConstants.LATEST_FUNC_VER,
      ExcelClientConstants.QUESTIONNAIRE_STATUS,
      ExcelClientConstants.IS_REVIEWED,
      ExcelClientConstants.EQUAL,
      ExcelClientConstants.HEX_VALUE,
      ExcelClientConstants.REVIEWED_VALUE,
      ExcelClientConstants.COMPLI_RESULT,
      ExcelClientConstants.REVIEW_SCORE,
      ExcelClientConstants.REVIEW_COMMENTS,
      ExcelClientConstants.REVIEW_RESULT_DESCRIPTION };


  /**
   * Columns for compare info sheet
   */
  protected static final String[] compareInfoSheetHeader = new String[] {
      ExcelClientConstants.HEX_FILE,
      ExcelClientConstants.A2LFILE,
      ExcelClientConstants.COMP_VARIANT,
      ExcelClientConstants.TOTAL_PARAMS,
      ExcelClientConstants.EQUAL_REVIEWED,
      ExcelClientConstants.COMPLI_PARAMS,
      ExcelClientConstants.COMPLI_PARAMS_PASSED,
      ExcelClientConstants.NUM_PARAM_BSH_RESP,
      ExcelClientConstants.NUM_PARAM_BSH_RESP_RVW,
      ExcelClientConstants.PARAM_BSH_RESP_RVW,
      ExcelClientConstants.PARAM_BSH_RESP_QNAIRE,
      ExcelClientConstants.QNAIRE_NEGATIVE_ANSWER,
      ExcelClientConstants.CONSIDERED_PREVIOUS_REVIEWS };


}
