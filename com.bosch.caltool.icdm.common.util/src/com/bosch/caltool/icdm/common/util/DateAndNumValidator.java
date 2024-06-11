/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// APIC-59
public final class DateAndNumValidator {

  /**
   * DateAndNumValidator instance
   */
  private static DateAndNumValidator dateAndNumValidator;

  /**
   * The private constructor
   */
  private DateAndNumValidator() {

  }

  /**
   * This method returns DateAndNumValidator instance
   *
   * @return DateAndNumValidator
   */
  public static DateAndNumValidator getInstance() {
    if (dateAndNumValidator == null) {
      dateAndNumValidator = new DateAndNumValidator();
    }
    return dateAndNumValidator;
  }

  /**
   * @param dateFormat
   * @param inputDate
   * @return boolean
   */
  public boolean dateFormatValidator(final String dateFormat, final String inputDate) {
    String regexMMDDYYYY = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";
    String regexDDMMYYYY = "^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$";

    String regexYYYYMMDD = "^(19|20)\\d\\d$(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.]";
    boolean isValid = false;
    if ("MM/DD/YYYY".equalsIgnoreCase(dateFormat) || "MM-DD-YYYY".equalsIgnoreCase(dateFormat) ||
        "MM.DD.YYYY".equalsIgnoreCase(dateFormat) || "MM.DD.YY".equalsIgnoreCase(dateFormat) ||
        "MM-DD-YY".equalsIgnoreCase(dateFormat) || "MM/DD/YY".equalsIgnoreCase(dateFormat)) {
      isValid = Pattern.matches(regexMMDDYYYY, inputDate);
    }
    else if ("DD/MM/YYYY".equalsIgnoreCase(dateFormat) || "DD-MM-YYYY".equalsIgnoreCase(dateFormat) ||
        "DD.MM.YYYY".equalsIgnoreCase(dateFormat) || "DD.MM.YY".equalsIgnoreCase(dateFormat) ||
        "DD-MM-YY".equalsIgnoreCase(dateFormat) || "DD/MM/YY".equalsIgnoreCase(dateFormat)) {
      isValid = Pattern.matches(regexDDMMYYYY, inputDate);
    }
    else if ("YYYY/DD/MM".equalsIgnoreCase(dateFormat) || "YYYY-DD-MM".equalsIgnoreCase(dateFormat) ||
        "YYYY.DD.MM".equalsIgnoreCase(dateFormat) || "YY.DD.MM".equalsIgnoreCase(dateFormat) ||
        "YY-DD-MM".equalsIgnoreCase(dateFormat) || "YY/DD/MM".equalsIgnoreCase(dateFormat)) {
      isValid = Pattern.matches(regexYYYYMMDD, inputDate);
    }
    return isValid;
  }

  /**
   * This method returns
   *
   * @param numFormat
   * @param inputNumber
   * @return String
   * @throws Exception
   */
  public String getFormatNumber(final String numFormat, final String inputNumber) throws Exception {
    String formatNum = null;
    String inputNumberResult = null;
    if ((numFormat != null) && (inputNumber != null)) {
      if (!("".equals(numFormat) && "".equals(inputNumber))) {
        if (numFormat.contains(",")) {
          if (inputNumber.contains(".")) {
            inputNumberResult = inputNumber.replace(".", ",");
            formatNum = inputNumberResult;
          }
          else {
            formatNum = convertDecimalFormat(numFormat, inputNumberResult);

          }
        }
        else if (numFormat.contains(".")) {
          formatNum = convertDecimalFormat(numFormat, inputNumberResult);
        }
        else {
          formatNum = inputNumberResult;
        }

      }
    }
    return formatNum;
  }

  /**
   * Converts the number into string with given format
   *
   * @param numFormat
   * @param inputNumber
   * @return String of formatted number
   * @throws Exception
   */
  private String convertDecimalFormat(final String numFormat, final String inputNumber) throws Exception {
    String formatNumResult = null;
    try {
      DecimalFormat numberFormat = new DecimalFormat(numFormat);
      formatNumResult = numberFormat.format(Double.parseDouble(inputNumber));
    }
    catch (NumberFormatException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw exp;
    }
    return formatNumResult;
  }

  /**
   * This method returns formated date type
   *
   * @param format the given format
   * @return String of formatted date
   */
  public String formatDate(final String format) {
    String temp = format;

    if (temp.contains("Y")) {
      temp = temp.replace("Y", "y");
    }

    if (temp.contains("D")) {
      temp = temp.replace("D", "d");
    }

    // TODO : check this
    if (temp.contains("m")) {
      temp = temp.replace("m", "M");
    }

    return temp;
  }

  /**
   * gets the formatted date
   *
   * @param formatStyle the given format style
   * @param date
   * @param format
   * @return String
   * @throws ParseException
   */
  public String getFormatedDate(final String formatStyle, final String date, final String format)
      throws ParseException {
    String value = null;

    DateFormat startFormat = new SimpleDateFormat(formatStyle);
    if ((format != null) && !"".equals(format)) {
      String dateFormat = formatDate(format);
      DateFormat endFormat = new SimpleDateFormat(dateFormat);
      try {
        if ((date != null) && !"".equals(date)) {
          Date parsedDate = startFormat.parse(date);
          value = endFormat.format(parsedDate);
        }
      }
      catch (ParseException exp) {
        throw exp;
      }
    }
    return value;
  }

  /**
   * @param formatStyle
   * @param date
   * @param format
   * @return String
   * @throws ParseException
   */
  public String getFormatedTimeStampDate(final String formatStyle, final String date, final String format)
      throws ParseException {
    String value = null;

    DateFormat startFormat = new SimpleDateFormat(formatStyle);
    DateFormat endFormat = new SimpleDateFormat(format);
    try {
      if ((date != null) && !"".equals(date)) {
        Date parsedDate = startFormat.parse(date);
        value = endFormat.format(parsedDate);
      }
    }
    catch (ParseException e) {
      throw e;
    }
    return value;
  }

  /**
   * Return true, if the given date is valid
   *
   * @param format
   * @param date
   * @return boolean
   */
  public boolean isValidDate(final String format, final String date) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    Date testDate = null;
    try {
      testDate = sdf.parse(date);
    }
    catch (ParseException e) {
      return false;
    }
    return sdf.format(testDate).equals(date);
  }

  /**
   * Returns true if the input format matches with the given value
   *
   * @param format
   * @param input
   * @return boolean
   */
  public boolean isInputInNumFormat(final String format, final String input) {
    boolean isMatches = false;
    int dCount = 0;
    if (!("".equals(format))) {
      if (!("".equals(input))) {
        String regexPattern = "";
        if (format.contains(".")) {
          if (input.contains(".")) {
            isMatches = isInputNumValMatched(format, input, dCount, regexPattern);
          }
        }
        else {
          isMatches = isInputNumValMatched(format, input, dCount, regexPattern);
        }
      }
      else {
        isMatches = true;
      }
    }
    else {
      isMatches = true;
    }
    return isMatches;
  }

  /**
   * checks the input with the given pattern matcher
   *
   * @param format
   * @param input
   * @param dCount
   * @param regexPattern
   * @return boolean
   */
  private boolean isInputNumValMatched(final String format, final String input, final int dCount,
      final String regexPattern) {
    boolean isMatches;
    String regexPatternResult;
    regexPatternResult = generateRegex(format, dCount, regexPattern);
    Pattern pattern = Pattern.compile(regexPatternResult);
    Matcher matcher = pattern.matcher(input);
    if (matcher.matches()) {
      isMatches = true;
    }
    else {
      isMatches = false;
    }
    return isMatches;
  }

  /**
   * generates the specific regular expression
   *
   * @param format
   * @param dCount
   * @param regexPattern
   * @return String
   */
  private String generateRegex(final String format, int dCount, String regexPattern) { // NOPMD by dmo5cob on 8/8/14
                                                                                       // 11:02 AM
    for (int i = 0; i < format.length(); i++) {
      char item = format.charAt(i);
      boolean isDigit = Character.isDigit(item);
      if (isDigit) {
        dCount++;
      }
      else {
        regexPattern = regexPattern + "\\d{" + dCount + "}" + item;
        dCount = 0;
      }
    }
    regexPattern = regexPattern + "\\d{" + dCount + "}";
    return regexPattern;
  }

  /**
   * Converts the number value into given format
   *
   * @param format
   * @param numVal
   * @return String
   */
  public String convertInNumFormat(final String format, final String numVal) {
    DecimalFormat decimalFormat = new DecimalFormat(format);
    return decimalFormat.format(numVal);
  }
}
