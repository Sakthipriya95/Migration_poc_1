/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.ParseException;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HE2FE
 */
public final class ApicUtil {

  private static final int TOTAL_BISET_SIZE = 11;

  private static final int PARAM_BISET = 3;

  /**
   * Private constructor
   */
  private ApicUtil() {
    // Not applicable
  }

  /**
   * Use same method in {@link DateUtil}
   * <p>
   * Converts SQL timestamp in UTC to java calendar object. The calendar is an instance of Gregorian calendar, in the
   * default timezone
   *
   * @param timestamp time in sql timestamp
   * @return instance of Gregorian calendar, in the system default time zone
   */
  // Deprecated - Use DateUtil instead
  public static Calendar timestamp2calendar(final Timestamp timestamp) {
    return DateUtil.timestamp2calendar(timestamp);
  }

  /**
   * Converts SQL timestamp java calendar object. The calendar is an instance of Gregorian calendar. The timezone offset
   * is not considered.
   *
   * @param timestamp time in sql timestamp
   * @param addTimeOffset if true, timezone correction will be made to the output
   * @return instance of Gregorian calendar
   * @deprecated use same method in {@link DateUtil}
   */
  @Deprecated
  public static Calendar timestamp2calendar(final Timestamp timestamp, final boolean addTimeOffset) {
    return DateUtil.timestamp2calendar(timestamp, addTimeOffset);
  }

  /**
   * Converts SQL timestamp java calendar object. The calendar is an instance of Gregorian calendar. The timezone offset
   * is not considered.
   *
   * @param timestamp time in sql timestamp
   * @param timezoneID Time Zone ID
   * @return date in the given timeone
   * @deprecated use same method in {@link DateUtil}
   */
  @Deprecated
  public static Date timestamp2Date(final Timestamp timestamp, final String timezoneID) {
    return DateUtil.timestamp2Date(timestamp, timezoneID);
  }

  /**
   * Utility method to get the current UTC time as Timestamp object
   *
   * @return the current time
   * @deprecated use same method in {@link DateUtil}
   */
  @Deprecated
  public static Timestamp getCurrentUtcTime() {
    return DateUtil.getCurrentUtcTime();
  }

  /**
   * Utility method to convert calendar to Timestamp object
   *
   * @param calendar Calendar
   * @return timestamp
   * @deprecated use same method in {@link DateUtil}
   */
  // ICDM-2612
  @Deprecated
  public static Timestamp calendarToTimestamp(final Calendar calendar) {
    return DateUtil.calendarToTimestamp(calendar);
  }

  /**
   * Use same method in {@link DateUtil}
   * <p>
   *
   * @param format date format. Defined in <code>com.bosch.caltool.icdm.common.util.DateFormat</code> class.
   * @return the local current time in the given format as a string
   */
  // Deprecated - Use DateUtil instead
  public static String getCurrentTime(final String format) {
    return DateUtil.getCurrentTime(format);
  }

  /**
   * Converts the input date in UTC timezone java calendar object. The calendar is an instance of Gregorian calendar.
   * The timezone offset is not considered.
   *
   * @param date date
   * @return instance of Gregorian calendar
   * @deprecated use same method in {@link DateUtil}
   */
  @Deprecated
  public static Calendar getCalendarFromUTCDate(final Date date) {
    return DateUtil.getCalendarFromUTCDate(date);
  }

  /**
   * Compare two String values Consider a NULL value as LESS THAN a not NULL value.
   *
   * @param name first string
   * @param name2 second string
   * @return compare status
   */
  public static int compare(final String name, final String name2) {

    if ((name == null) && (name2 == null)) {
      // both Strings are NULL => return EQUAL
      return 0;
    }
    if (name == null) {
      // first String is NULL => return LESS THAN
      return -1;
    }
    if (name2 == null) {
      // second String is NULL => return GREATER THAN
      return 1;
    }

    // both String are not NULL, compare them
    final Collator collator = Collator.getInstance(Locale.GERMAN);
    collator.setStrength(Collator.IDENTICAL);
    return collator.compare(name, name2);


  }

  /**
   * Compares two comparable objects of same type
   *
   * @param <C> Object type implementing <code>java.lang.Comparable</code>
   * @param obj1 first object
   * @param obj2 second object
   * @return comparison result
   */
  public static <C extends Comparable<C>> int compare(final C obj1, final C obj2) {
    if ((obj1 == null) && (obj2 == null)) {
      // both object are NULL => return EQUAL
      return 0;
    }
    if (obj1 == null) {
      // first object is NULL => return LESS THAN
      return -1;
    }
    if (obj2 == null) {
      // second object is NULL => return GREATER THAN
      return 1;
    }
    return obj1.compareTo(obj2);

  }

  /**
   * Compare two Calendar instances
   *
   * @param date1 The first date
   * @param date2 The second date
   * @return compare result
   */
  public static int compareCalendar(final Calendar date1, final Calendar date2) {
    return compare(date1, date2);
  }

  /**
   * Compare two Calendar instances
   *
   * @param number1 The first date
   * @param number2 The second date
   * @return compare result
   */
  public static int compareBigDecimal(final BigDecimal number1, final BigDecimal number2) {
    return compare(number1, number2);
  }

  /**
   * Compare two long values
   *
   * @param value1 value1
   * @param value2 value2
   * @return compare result
   */
  public static int compareLong(final long value1, final long value2) {
    if (value1 < value2) {
      return -1;
    }
    else if (value1 > value2) {
      return 1;
    }
    else {
      return 0;
    }
  }

  /**
   * Compare two integer values
   *
   * @param value1 value1
   * @param value2 value2
   * @return compare result
   */
  public static int compareInt(final int value1, final int value2) {
    if (value1 < value2) {
      return -1;
    }
    else if (value1 > value2) {
      return 1;
    }
    else {
      return 0;
    }
  }

  /**
   * Compare two boolean values Consider TRUE as less than FALSE this will order TRUE to top in ascending order
   *
   * @param value1 value1
   * @param value2 value2
   * @return compare result
   */
  public static int compareBoolean(final boolean value1, final boolean value2) {
    if (value1 == value2) {
      return 0;
    }
    else if (value1) {
      return -1;
    }
    else {
      return 1;
    }
  }

  /**
   * Use same method in {@link DateUtil}
   * <p>
   * Method which formats date and returns as String
   *
   * @param format defines attribute format
   * @param dateValue defines attribute date value
   * @return String stringValue
   * @throws ParseException It throws ParseException if date value is incorrect
   */
  // Deprecated - Use DateUtil instead
  public static String formatDate(final String format, final String dateValue) throws ParseException {
    return DateUtil.formatDate(format, dateValue);
  }

  /**
   * Use same method in {@link DateUtil}
   * <p>
   * format the string date to required format
   *
   * @param inputFormat - input format of date string
   * @param date - string date
   * @param outputFormat - required output format
   * @return - String date in required format
   * @throws ParseException - exception while parsing
   */
  // Deprecated - Use DateUtil instead
  // Icdm-543
  public static String getFormattedDate(final String inputFormat, final String date, final String outputFormat)
      throws ParseException {

    return DateUtil.getFormattedDate(inputFormat, date, outputFormat);
  }

  /**
   * Use same method in {@link DateUtil}
   * <p>
   * format the Calender Obj
   *
   * @param date date
   * @param format format
   * @return String dateValue
   */
  // Icdm-543
  // Deprecated - Use DateUtil instead
  public static String getFormattedDate(final String format, final Calendar date) {
    return DateUtil.getFormattedDate(format, date);
  }

  /**
   * new method for getting the full name for user info
   *
   * @param firstName firstName of the User
   * @param lastName lastName of the User
   * @return the Full name First name + last name
   */
  // Icdm-1127
  public static String getFullName(final String firstName, final String lastName) {
    // Method is specific only to iCDM
    final StringBuilder strBuilder = new StringBuilder();
    if (!CommonUtils.isEmptyString(firstName)) {
      strBuilder.append(firstName).append(", ");
    }
    if (!CommonUtils.isEmptyString(lastName)) {
      strBuilder.append(lastName);
    }
    return strBuilder.toString();
  }


  /**
   * new Common method to check if the parameter is Variant coded.
   *
   * @param paramName paramName
   * @return true if the parameter name is Varaint coded
   */
  public static boolean isVariantCoded(final String paramName) {
    int indexOf = paramName.lastIndexOf('[');
    if (indexOf != -1) {
      char numChar = paramName.charAt(indexOf + 1);
      return (numChar >= '0') && (numChar <= '9');
    }
    return false;
  }

  /**
   * If the Variant coded param name is given then the last characters with [int] is removed and the base parameter name
   * is returned
   *
   * @param varcodedParamName paramName
   * @return the Base parameter name from the Varaint coded param name.
   */
  public static String getBaseParamName(final String varcodedParamName) {
    return CommonUtils.concatenate(getBaseParamFirstName(varcodedParamName), getBaseParamLastName(varcodedParamName));
  }

  /**
   * Get the parameter key, to be used to cache the parameters.
   *
   * @param name name of parameter
   * @return key
   */
  public static String getParamKey(final String name) {
    return name.toUpperCase(Locale.ENGLISH);
  }

  /**
   * Occurs in situation where parameter name is PT_drRmpTransP[0].Neg_C
   *
   * @param varcodedParamName varcodedParamName
   * @return the base param first name for example PT_drRmpTransP[0].Neg_C the first name is PT_drRmpTransP.
   */
  public static String getBaseParamFirstName(final String varcodedParamName) {
    return varcodedParamName.substring(0, varcodedParamName.lastIndexOf('['));
  }

  /**
   * Occurs in situation where parameter name is PT_drRmpTransP[0].Neg_C
   *
   * @param varcodedParamName varcodedParamName
   * @return the base parameter last name for example PT_drRmpTransP[0].Neg_C the first name is .Neg_C.
   */
  public static String getBaseParamLastName(final String varcodedParamName) {
    String lastName = "";
    if ((varcodedParamName.length() - 1) > varcodedParamName.lastIndexOf(']')) {
      lastName = varcodedParamName.substring(varcodedParamName.lastIndexOf(']') + 1, varcodedParamName.length());
    }
    return lastName;
  }

  /**
   * The Equals exact method in the Caldataphy Object uses Bit Set. The Param name check is done in BitSet number 3. so
   * ignoring it we get 11111110111
   *
   * @return the Bit set for Param name.
   */
  public static BitSet getBiSetForParamName() {
    BitSet checkBitSet = new BitSet(TOTAL_BISET_SIZE);
    for (int j = 0; j < TOTAL_BISET_SIZE; j++) {
      if (j != PARAM_BISET) {
        checkBitSet.set(j);
      }
    }
    return checkBitSet;
  }

  /**
   * Method used to compare Numbers represented as Strings.</br>
   * </br>
   * Double comparison is used when both input strings can be converted to numbers</br>
   * String comparison is used when both input strings cannot be converted to numbers</br>
   * If the first parameter cannot be converted to a number and the second parameter can be converted to a number, 1 is
   * returned</br>
   * If the first parameter can be converted to a number and the second parameter cannot be converted to a number, -1 is
   * returned</
   *
   * @param paramDisplayVal1 String
   * @param paramDisplayVal2 String
   * @return compareResult
   */
  public static int compareStringAndNum(final String paramDisplayVal1, final String paramDisplayVal2) {
    if ((!"".equals(paramDisplayVal1.trim()) && "".equals(paramDisplayVal2.trim())) ||
        (!isNumber(paramDisplayVal1) && isNumber(paramDisplayVal2))) {
      return 1;
    }
    else if (("".equals(paramDisplayVal1.trim()) && !"".equals(paramDisplayVal2.trim())) ||
        (isNumber(paramDisplayVal1) && !isNumber(paramDisplayVal2))) {
      return -1;
    }
    else if ("".equals(paramDisplayVal1.trim()) && "".equals(paramDisplayVal2.trim())) {
      return 0;
    }
    else if (!isNumber(paramDisplayVal1) && !isNumber(paramDisplayVal2)) {
      return ApicUtil.compare(paramDisplayVal1, paramDisplayVal2);
    }
    else if (isNumber(paramDisplayVal1) && isNumber(paramDisplayVal2)) {
      return Double.valueOf(paramDisplayVal1).compareTo(Double.valueOf(paramDisplayVal2));
    }
    return 0;
  }

  /**
   * Method which checks whether the passed string can be converted to a number
   *
   * @param text String
   * @return true if string can be converted to a number
   */
  public static boolean isNumber(final String text) {
    return text.matches("^[-+]?[0-9]*\\.?,?[0-9]+([eE][-+]?[0-9]+)?$");
  }

  /**
   * method to return the bse param name from all the params.
   *
   * @param labels labels
   * @return the map with Varaint coded param name as key and Base param name as Value
   */
  public static Map<String, String> getBaseParamMap(final Collection<String> labels) {
    Map<String, String> baseParamMap = new ConcurrentHashMap<>();

    for (String param : labels) {
      if (ApicUtil.isVariantCoded(param)) {
        baseParamMap.put(param, ApicUtil.getBaseParamName(param));
      }
    }
    return baseParamMap;
  }

  /**
   * @param inputLabels labelSet
   * @param paramsWithDepencies remove the params whose Base params has dependencies
   * @return Set of modified parameters
   */
  public static Set<String> removeBaseParamWithDep(final Collection<String> inputLabels,
      final Set<String> paramsWithDepencies) {
    Set<String> modifiedLabelSet = new HashSet<>();
    for (String label : inputLabels) {
      if (!ApicUtil.isVariantCoded(label) || !paramsWithDepencies.contains(ApicUtil.getBaseParamName(label))) {
        modifiedLabelSet.add(label);
      }
    }
    return modifiedLabelSet;
  }

  /**
   * Convert a Date Object to a Calender Object.
   *
   * @param date date
   * @return the Calender Object
   * @deprecated use same method in {@link DateUtil}
   */
  @Deprecated
  public static Calendar dateToCalendar(final Date date) {
    return DateUtil.dateToCalendar(date);
  }

  /**
   * @param language German or english
   * @param engTxt engname
   * @param gerTxt gerName
   * @param defaultStr default String to be retuned can be empty ot not Defined
   * @return the language Specific name
   */
  public static String getLangSpecTxt(final Language language, final String engTxt, final String gerTxt,
      final String defaultStr) {
    String retVal = null;
    if (language == Language.ENGLISH) {
      retVal = engTxt;
    }
    else if (language == Language.GERMAN) {
      retVal = CommonUtils.isEmptyString(gerTxt) ? engTxt : gerTxt;
    }
    return CommonUtils.isEmptyString(retVal) ? defaultStr : retVal;
  }

}