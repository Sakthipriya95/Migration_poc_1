/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author apj4cob
 */
public class CompliResultComparisionConstants {

  private CompliResultComparisionConstants() {
    throw new IllegalStateException("Utility class");
  }


  /** The a2l file name. */
  public static final String A2L_FILE_NAME = "Different A2L file name.A2l";
  /** The hex file path. */
  public static final String HEX_FILE_PATH =
      "testdata/compliresultcomparision/MRG1x-15B-FRG_050_00-Star23-Cx-M256xx-DES30-SFxxx-EAaxx-xxxxxx-xxxxx-xx-151008a_000_00_19562424.hex";
  /**
   * PIDC A2L mapping ID for A2L File: <br>
   * A2L File: Different A2L file name.A2l
   */
  public static final long PIDC_A2L_ID = 1174547229L;
  /**
   * PIDC A2L ID <br>
   */
  public static final String PIDC_A2L_ID_STR = "1174547229";
  /**
   * PIDC Variant: <br>
   * 270kw_sch
   */
  public static final long VAR_ID = 1174547227L;

  /**
   * PIDC Version: <br>
   * PIDC Demonstrator (New Test DB) (Version 1).
   */
  public static final long PIDC_VERS_ID = 790699170L;

  /**
   * A2L File: <br>
   * A2L File: Different A2L file name.A2l
   */
  public static final long A2L_ID = 1119935001L;

  /** The hex file name. */
  public static final String HEX_FILE_NAME =
      "MRG1x-15B-FRG_050_00-Star23-Cx-M256xx-DES30-SFxxx-EAaxx-xxxxxx-xxxxx-xx-151008a_000_00_19562424.hex";

  /**
   * SSD Label column name
   */
  public static final String SSD_LABEL_COL_NAME = "SSD-Label";
  /**
   * Check SSD excel report's first sheet
   */
  public static final int LOG_MESSAGE_SHEET_NUM = 0;
  /**
   * Name of Check ssd report from compare hex
   */
  public static final String COMPARE_HEX_JUNIT_EXCEL_REPORT_NAME = "CompareHex_JunitCompareCompliCompHexReport.xlsx";

  /**
   * Compliance Review Output folder path
   */
  public static final String OUTPUT_PATH =
      System.getProperty("java.io.tmpdir") + File.separator + "CompliReviewOutput__" +
          (new SimpleDateFormat("yyyyMMdd_hhmmss")).format(Calendar.getInstance().getTime()) + ".zip";

  /**
   * Check SSD Excel report name prefix
   */
  public static final String CHK_SSD_EXL_REPORT_NAME_PREFIX = "CompliReview_";
  /**
   * Check SSD External report identifier
   */
  public static final String CHK_SSD_EXL_REPORT_EXT_FILE = "External";
}
