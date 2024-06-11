/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * Util methods for date/calendar etc.
 *
 * @author bne4cob
 */
public final class DateUtil {

  /**
   * Universal timezone(UTC) key.
   */
  private static final String UNIVRSL_TIME_ZONE = "Universal";

  private DateUtil() {
    // Private construtor for util class
  }


  /**
   * Converts SQL timestamp in UTC to java calendar object. The calendar is an instance of Gregorian calendar, in the
   * default timezone
   *
   * @param timestamp time in sql timestamp
   * @return instance of Gregorian calendar, in the system default time zone
   */
  public static Calendar timestamp2calendar(final Timestamp timestamp) {
    return timestamp2calendar(timestamp, true);
  }

  /**
   * Converts SQL timestamp java calendar object. The calendar is an instance of Gregorian calendar. The timezone offset
   * is not considered.
   *
   * @param timestamp time in sql timestamp
   * @param addTimeOffset if true, timezone correction will be made to the output
   * @return instance of Gregorian calendar
   */
  public static Calendar timestamp2calendar(final Timestamp timestamp, final boolean addTimeOffset) {
    if (timestamp == null) {
      return null;
    }
    final TimeZone clientTZ = TimeZone.getDefault();
    final Calendar calendar = new GregorianCalendar();

    if (addTimeOffset) {
      long clTimems = timestamp.getTime() + clientTZ.getOffset(timestamp.getTime());
      calendar.setTimeInMillis(clTimems);
    }
    else {
      calendar.setTimeInMillis(timestamp.getTime());
    }

    return calendar;
  }

  /**
   * Converts SQL timestamp java calendar object. The calendar is an instance of Gregorian calendar. The timezone offset
   * is not considered.
   *
   * @param timestamp time in sql timestamp
   * @param timezoneID Time Zone ID
   * @return date in the given timeone
   */
  public static Date timestamp2Date(final Timestamp timestamp, final String timezoneID) {
    if (timestamp == null) {
      return null;
    }
    final Calendar calendar = new GregorianCalendar();
    final TimeZone clientTZ = TimeZone.getTimeZone(timezoneID);
    long clTimems = timestamp.getTime() + clientTZ.getOffset(timestamp.getTime());
    calendar.setTimeInMillis(clTimems);

    return calendar.getTime();
  }

  /**
   * Utility method to get the current UTC time as Timestamp object
   *
   * @return the current time
   */
  public static Timestamp getCurrentUtcTime() {
    return calendarToTimestamp(new GregorianCalendar(TimeZone.getTimeZone(UNIVRSL_TIME_ZONE)));
  }

  /**
   * Utility method to get the current UTC time as String object
   *
   * @param format date format. Defined in <code>com.bosch.caltool.icdm.common.util.DateFormat</code> class.
   * @return the current time
   */
  public static String getCurrentUtcTime(final String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UNIVRSL_TIME_ZONE));
    return simpleDateFormat.format(new GregorianCalendar().getTime());
  }

  /**
   * @return the current time as a calendar object
   */
  public static Calendar getCurrentUtcCalendar() {
    return new GregorianCalendar(TimeZone.getTimeZone(UNIVRSL_TIME_ZONE));
  }

  /**
   * Utility method to convert calendar to Timestamp object
   *
   * @param calendar Calendar
   * @return timestamp
   */
  // ICDM-2612
  public static Timestamp calendarToTimestamp(final Calendar calendar) {
    if (calendar == null) {
      return null;
    }

    final StringBuilder timeStr = new StringBuilder();
    timeStr.append(calendar.get(Calendar.YEAR)).append('-').append(calendar.get(Calendar.MONTH) + 1).append('-')
        .append(calendar.get(Calendar.DAY_OF_MONTH)).append(' ').append(calendar.get(Calendar.HOUR_OF_DAY)).append(':')
        .append(calendar.get(Calendar.MINUTE)).append(':').append(calendar.get(Calendar.SECOND)).append('.')
        .append(calendar.get(Calendar.MILLISECOND));

    return Timestamp.valueOf(timeStr.toString());

  }

  /**
   * @return the current time as a calendar object
   */
  public static Calendar getCurrentTime() {
    return new GregorianCalendar();
  }

  /**
   * @param format date format. Defined in <code>com.bosch.caltool.icdm.common.util.DateFormat</code> class.
   * @return the local current time in the given format as a string
   */
  public static String getCurrentTime(final String format) {
    return getFormattedDate(format, getCurrentTime());
  }


  /**
   * Converts the input date in UTC timezone java calendar object. The calendar is an instance of Gregorian calendar.
   * The timezone offset is not considered.
   *
   * @param date date
   * @return instance of Gregorian calendar
   */
  public static Calendar getCalendarFromUTCDate(final Date date) {
    return getCalendarFromDate(date, true);
  }

  /**
   * Converts SQL timestamp java calendar object. The calendar is an instance of Gregorian calendar. The timezone offset
   * is not considered.
   *
   * @param date date
   * @param addTimeOffset if true, timezone correction will be made to the output
   * @return instance of Gregorian calendar
   */
  public static Calendar getCalendarFromDate(final Date date, final boolean addTimeOffset) {
    if (date == null) {
      return null;
    }
    final TimeZone clientTZ = TimeZone.getDefault();
    final Calendar calendar = new GregorianCalendar();

    if (addTimeOffset) {
      long clTimems = date.getTime() + clientTZ.getOffset(date.getTime());
      calendar.setTimeInMillis(clTimems);
    }
    else {
      calendar.setTimeInMillis(date.getTime());
    }

    return calendar;
  }

  /**
   * Converts java date to java calendar object. The calendar is an instance of Gregorian calendar
   *
   * @param date date
   * @return instance of Gregorian calendar
   */
  public static Calendar getCalendarFromDate(final Date date) {
    if (date == null) {
      return null;
    }
    final Calendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(date.getTime());

    return calendar;
  }

  /**
   * Method which formats date and returns as String
   *
   * @param format defines attribute format
   * @param dateValue defines attribute date value
   * @return String stringValue
   * @throws ParseException It throws ParseException if date value is incorrect
   */
  public static String formatDate(final String format, final String dateValue) throws ParseException {
    String frmt = format;
    if (CommonUtils.isEmptyString(format)) {
      // Default date format: yyyy-MM-dd HH:mm:ss
      frmt = DateFormat.DFLT_DATE_FORMAT;
    }
    return DateAndNumValidator.getInstance().getFormatedDate(DateFormat.DFLT_DATE_FORMAT, dateValue, frmt);
  }

  /**
   * //Icdm-543 format the string date to required format
   *
   * @param inputFormat - input format of date string
   * @param date - string date
   * @param outputFormat - required output format
   * @return - String date in required format
   * @throws ParseException - exception while parsing
   */
  public static String getFormattedDate(final String inputFormat, final String date, final String outputFormat)
      throws ParseException {
    SimpleDateFormat startFormat = new SimpleDateFormat(inputFormat);
    Date parsedDate = startFormat.parse(date);
    SimpleDateFormat endFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());
    return endFormat.format(parsedDate);
  }

  /**
   * Format the date
   *
   * @param date date
   * @param format format
   * @return String dateValue
   */
  public static String getFormattedDate(final String format, final Date date) {
    final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
    return sdf.format(date);
  }

  /**
   * format the Calender Obj
   *
   * @param date Calendar
   * @param format format
   * @return String dateValue
   */
  //// Icdm-543
  public static String getFormattedDate(final String format, final Calendar date) {
    final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
    return sdf.format(date.getTime());
  }

  /**
   * Convert a Date Object to a Calender Object.
   *
   * @param date date
   * @return the Calender Object
   */
  public static Calendar dateToCalendar(final Date date) {
    if (date == null) {
      return null;
    }
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal;
  }


  /**
   * Returns Timestamp for the provided date as string
   *
   * @param format - dateformat
   * @param date - date to be formatted
   * @return time stamp object
   * @throws ParseException for invalid date format
   */
  public static Timestamp convertStringToTimestamp(final String format, final String date) throws ParseException {
    SimpleDateFormat dateFormat;
    String frmtToUse = format;
    if ((frmtToUse == null) || " ".equals(frmtToUse)) {
      frmtToUse = DateFormat.DFLT_DATE_FORMAT;
    }
    String temp = DateAndNumValidator.getInstance().formatDate(frmtToUse);
    dateFormat = new SimpleDateFormat(temp, Locale.getDefault());
    return new Timestamp(dateFormat.parse(date).getTime());
  }

  /**
   * Method to convert the date to default DATE_FORMAT_15 format
   *
   * @param format as input
   * @param date as input
   * @return default date format
   * @throws ParseException as exception
   */
  public static String convertStringToDefaultDateFormat(final String format, final String date) throws ParseException {
    return ApicUtil.formatDate(DateFormat.DATE_FORMAT_15, convertStringToTimestamp(format, date).toString());
  }

  /**
   * Returns Timestamp for the provided date as string as per DATE_FORMAT_15
   *
   * @param date - date to be formatted
   * @return time stamp object
   * @throws ParseException for invalid date format
   */
  public static Timestamp convertStringToTimestamp(final String date) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormat.DATE_FORMAT_15, Locale.getDefault());
    return new Timestamp(dateFormat.parse(date).getTime());
  }

  /**
   * Returns Calendar for the provided date as string
   *
   * @param format - dateformat
   * @param date - date to be formatted
   * @return Calendar
   * @throws IcdmException date parse error
   */
  public static Calendar convertStringToCalendar(final String format, final String date) throws IcdmException {
    if (date == null) {
      return null;
    }
    SimpleDateFormat dateFormat;

    dateFormat = new SimpleDateFormat(format, Locale.getDefault());

    final Calendar retCalendar = new GregorianCalendar();
    try {
      retCalendar.setTime(dateFormat.parse(date));
    }
    catch (ParseException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }

    return retCalendar;

  }

  /**
   * @param date Date
   * @param pattern String
   * @return String
   */
  public static String formatDateToString(final Date date, final String pattern) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    return dateFormat.format(date);
  }

  /**
   * @param date1 Calendar (older)
   * @param date2 Calendar (newer)
   * @return difference in number of days between date1 and date2
   */
  public static Long differenceInDays(final Calendar date1, final Calendar date2) {
    return TimeUnit.MILLISECONDS.toDays(Math.abs(date2.getTimeInMillis() - date1.getTimeInMillis()));
  }

}
