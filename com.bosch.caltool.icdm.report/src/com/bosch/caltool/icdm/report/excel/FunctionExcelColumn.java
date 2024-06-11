/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;


/**
 * @author NDV4KOR
 */
public enum FunctionExcelColumn {
                                 /**
                                  * Unique Instance for Single class implementation
                                  */
                                 INSTANCE;

  /**
   * Columns for Review Rule sheet display
   */
  public final String[] functionSheetHeader = new String[] {
// ICDM-2444
      ExcelClientConstants.COMPLIANCE_NAME,
      ExcelClientConstants.PARAMETER,
      ExcelClientConstants.LONG_NAME,
      ExcelClientConstants.CLASS,
      ExcelClientConstants.IS_CODE_WORD,
      ExcelClientConstants.LOWER_LIMIT,
      ExcelClientConstants.UPPER_LIMIT,
      ExcelClientConstants.REFERENCE_VALUE,
      ExcelClientConstants.EXACT_MATCH,
      ExcelClientConstants.RH_UNIT,
      ExcelClientConstants.READY_FOR_SERIES,
// ICDM-2537
      ExcelClientConstants.MODIFIED_USER,
      ExcelClientConstants.MODIFIED_DATE,
      ExcelClientConstants.REVISION_ID,
      ExcelClientConstants.REMARKS, };

  /**
   * @return the unique instance of this class
   */
  public static FunctionExcelColumn getInstance() {
    return INSTANCE;
  }
}
