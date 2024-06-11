/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.excel;


/**
 * Defines excel constants
 * 
 * @author rvu1cob
 */
public final class ExcelConstants {

  /**
   * File type of excel files
   */
  public static final String[] FILTER_EXTNS = new String[] { "xlsx", "xls" };

  /**
   * File type of excel files
   */
  public static final String[] FILTER_NAMES = new String[] { "Excel 2007 Workbook(*.xlsx)",
      "Excel 97-2003 Workbook(*.xls)" };

  /**
   * Excel extension used as Filter in File dialog
   */
  public static final String[] FILTER_EXCEL_EXTN_WITH_STAR = new String[] { "*.xlsx", "*.xls" };

  /**
   * Private constructor for utility class
   */
  private ExcelConstants() {
    // Nothing to do
  }


}
