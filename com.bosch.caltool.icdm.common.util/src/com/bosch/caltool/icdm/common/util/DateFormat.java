/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.bosch.caltool.icdm.common.exception.IcdmException;


/**
 * Utility class to format dates
 *
 * @author bne4cob
 */
public class DateFormat {

  /**
   * Date Format : dd-MMM-yy hh.mm.ss.S a
   */
  public static final String DATE_FORMAT_01 = "dd-MMM-yy hh.mm.ss.S a";
  /**
   * Date Format : yyyy-MM-dd HH:mm:ss.S
   */
  public static final String DATE_FORMAT_02 = "yyyy-MM-dd HH:mm:ss.S";
  /**
   * Date Format : yyyy-MM-dd HH:mm:ss.S
   */
  public static final String DATE_FORMAT_03 = "yyyy-MM-dd HH:mm:ss.S";
  /**
   * Date Format : yyyy-MM-dd HH:mm:ss
   */
  public static final String DATE_FORMAT_04 = "yyyy-MM-dd HH:mm:ss";
  /**
   * Date Format : yyyy-MM-dd HH:mm
   */
  public static final String DATE_FORMAT_05 = "yyyy-MM-dd HH:mm";
  /**
   * Date Format : dd-MMM-yy hh.mm.ss.SSSSSS aa
   */
  public static final String DATE_FORMAT_06 = "dd-MMM-yy hh.mm.ss.SSSSSS aa";
  /**
   * Date Format : MMM dd yyyy HH:mm z
   */
  public static final String DATE_FORMAT_07 = "MMM dd yyyy HH:mm z";
  /**
   * Date Format : dd-MMM-yyyy hh.mm.ss a
   */
  public static final String DATE_FORMAT_08 = "dd-MMM-yyyy hh.mm.ss a";
  /**
   * Date Format : dd-MMM-yyyy
   */
  public static final String DATE_FORMAT_09 = "dd-MMM-yyyy";
  /**
   * Date Format : yyyy-MM-dd
   */
  public static final String DATE_FORMAT_10 = "yyyy-MM-dd";
  /**
   * Date Format : E MMM dd HH:mm:ss yyyy
   */
  public static final String DATE_FORMAT_11 = "E MMM dd HH:mm:ss yyyy";

  /**
   * Date Format : yyyy-MMM-dd
   */
  // iCDM-2614
  public static final String DATE_FORMAT_12 = "yyyy-MMM-dd";

  /**
   * Date Format : E MMM dd HH:mm:ss z yyyy
   */
  public static final String DATE_FORMAT_13 = "E MMM dd HH:mm:ss z yyyy";

  /**
   * Date Format : yyyy_MM_dd_HH_mm_ss for naming the pdf report
   */
  public static final String DATE_FORMAT_14 = "yyyy_MM_dd___HH_mm_ss";

  /**
   * Date Format : yyyy-MM-dd HH:mm:ss.S
   */
  public static final String DATE_FORMAT_15 = "yyyy-MM-dd HH:mm:ss SSS";

  /**
   * Date Format : yyyy-MM-dd HH:mm:ss.S This date format meets the one required by the CDFx standard.
   */
  public static final String DATE_FORMAT_16 = "yyyy-MM-dd'T'HH:mm:ss";

  /**
   * Date Format : yyyyMMdd_HHmmss for generating unique name for A2l Compliance Parameter List Report
   */
  public static final String DATE_FORMAT_17 = "yyyyMMdd_HHmmss";

  /**
   * Date Format : yyyyMMdd_HHmmss.SSS
   */
  public static final String DATE_FORMAT_18 = "yyyyMMdd_HHmmss.SSS";

  /**
   * Date Format : yyyyMMddHHmm
   */
  public static final String DATE_FORMAT_19 = "yyyyMMddHHmm";

  /**
   * Date Format : yyyyMMdd_HHmmssSSS<br>
   * e.g. 20221231_235959111
   */
  public static final String DATE_FORMAT_20 = "yyyyMMdd_HHmmssSSS";

  /**
   * Default date format : yyyy-MM-dd HH:mm:ss
   */
  public static final String DFLT_DATE_FORMAT = DATE_FORMAT_04;

  private DateFormat() {
    // Private constructor for constants class
  }

  /**
   * Returns Timestamp for the provided date as string
   *
   * @param format - dateformat
   * @param date - date to be formatted
   * @return time stamp object
   * @throws ParseException for invalid date format
   * @deprecated use same method in {@link DateUtil}
   */
  @Deprecated
  public static Timestamp convertStringToTimestamp(final String format, final String date) throws ParseException {
    return DateUtil.convertStringToTimestamp(format, date);
  }

  /**
   * Use same method in {@link DateUtil}
   * <p>
   * Returns Calendar for the provided date as string
   *
   * @param format - dateformat
   * @param date - date to be formatted
   * @return Calendar
   * @throws IcdmException date parse error
   */
  // Deprecated - Use DateUtil instead
  public static Calendar convertStringToCalendar(final String format, final String date) throws IcdmException {
    return DateUtil.convertStringToCalendar(format, date);
  }

  /**
   * use same method in {@link DateUtil}
   * <p>
   *
   * @param date Date
   * @param pattern String
   * @return String
   */
  // Deprecated - Use DateUtil instead
  public static String formatDateToString(final Date date, final String pattern) {
    return DateUtil.formatDateToString(date, pattern);
  }

}