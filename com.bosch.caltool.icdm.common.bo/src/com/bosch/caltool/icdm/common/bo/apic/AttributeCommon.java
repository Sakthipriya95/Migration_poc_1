/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.apic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateAndNumValidator;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author say8cob
 */
public class AttributeCommon {

  private static final Set<String> DATE_FORMAT_SET = new HashSet<>(Arrays.asList(ApicConstants.ATTR_DATE_FORMATS));

  /**
   * Returns Timestamp for the provided date as string
   *
   * @param format - dateformat
   * @param date - date to be formatted
   * @return time stamp object
   * @throws ParseException for invalid date format
   */
  public static Timestamp convertAttrDateStringToTimestamp(final String format, final String date) throws ParseException {
    SimpleDateFormat dateFormat;
    String frmtToUse = format;
    if ((frmtToUse == null) || " ".equals(frmtToUse)) {
      frmtToUse = DateFormat.DFLT_DATE_FORMAT;
    }
    String tempDateFrmt = null;
    if (DATE_FORMAT_SET.contains(format)) {
      tempDateFrmt = DateAndNumValidator.getInstance().formatDate(frmtToUse);
    }
    dateFormat = new SimpleDateFormat(tempDateFrmt != null ? tempDateFrmt : frmtToUse, Locale.getDefault());
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
  public static String convertAttrDateStringToDefaultDateFormat(final String format, final String date) throws ParseException {
    return ApicUtil.formatDate(DateFormat.DATE_FORMAT_15, convertAttrDateStringToTimestamp(format, date).toString());
  }
}
