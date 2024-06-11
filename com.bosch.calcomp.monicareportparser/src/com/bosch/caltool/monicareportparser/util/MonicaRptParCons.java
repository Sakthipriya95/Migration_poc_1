/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.monicareportparser.util;


/**
 * @author rgo7cob
 */
public class MonicaRptParCons {

  // Added for Sonar cube issues
  // Utility classes should not have public constructors
  private MonicaRptParCons() {

  }

  /**
   * NO_LABEL
   */
  public static final String ERR_NO_LABEL = "No parameter is available in the report";
  /**
   * NO_DCM
   */
  public static final String ERR_NO_DCM = "Dcm file path is not defined in the report";

  /**
   * No Sheets found in the excel file
   */
  public static final String ERR_SHEET_NOT_FOUND = "Sheet not found in the excel";

  /**
   * Problem in reading the MoniCa Input Stream
   */
  public static final String ERR_READ_MONICA_STREAM = "Problem in reading the MoniCa report inputstream";
  /**
   * NO_A2L
   */
  public static final String ERR_NO_A2L = "A2l file path is not defined in the report";

  /**
   * error thrown when the inputstream is null
   */
  public static final String ERR_IN_MONICA_INPUTSTREAM = "The MoniCa report inputstream is empty.";

  /**
   * comment constant
   */
  public static final String COMMENT = "COMMENT";
  
  /**
   * Reviewed By constant
   */
  public static final String REVIEWED_BY = "REVIEWED BY";
  /**
   * status constant
   */
  public static final String ICDM_STATUS = "ICDM-CHECK:";
  /**
   * dcm path constant
   */
  public static final String DCM_PATH = "DATA CONSERVE:";
  /**
   * a2l file path constant
   */
  public static final String A2L_FILE_PATH = "A2L FILE:";


}
