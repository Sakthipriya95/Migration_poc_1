/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report;

/**
 * @author and4cob
 */
public final class ReportBOConstants {

  /**
   * Constant for Work Pakage name
   */
  public static final String WORKPACKAGE_NAME = "Workpackage Name";

  /**
   * Constant for Responsibility Type
   */
  public static final String RESP_TYPE = "Responsibility Type";

  /**
   * Constant for Responsibility Name
   */
  public static final String RESPONSIBILITY_STR = "Responsibility Name";

  /**
   * Constant for storing Exported parameter count
   */
  public static final String TOTAL = "Total";

  /**
   * Not reviewed score rating
   */
  public static final String RATING_NOT_REVIEWED = "0%";
  /**
   * Prelim calib reviewed score rating
   */
  public static final String RATING_PRELIM_CAL = "25%";
  /**
   * Calibrated reviewed score rating
   */

  public static final String RATING_CALIBRATED = "50%";
  /**
   * check value review score
   */
  public static final String RATING_CHECKED = "75%";
  /**
   * completed review rating
   */
  public static final String RATING_COMPLETED = "100%";
  /**
   *
   */
  public static final String PROJECT = "Project";
  /**
   *
   */
  public static final String VARIANT = "Variant";
  /**
   *
   */
  public static final String SOFTWARE = "Software";
  /**
  *
  */
  public static final String MISSING = "Missing";
  /**
   * Columns for Project Information Sheet
   */
  private static final String[] projectInfoSheetHeader = new String[] { PROJECT, VARIANT, SOFTWARE };


  /**
   * Private constructor for utility class
   */
  private ReportBOConstants() {
    // Private constructor for utility class
  }

  /**
   * @return the projectinfosheetheader
   */
  public static String[] getProjectinfosheetheader() {
    return projectInfoSheetHeader;
  }
}

