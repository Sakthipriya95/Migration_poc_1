/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;

/**
 * @author bne4cob
 */
public final class Utils {

  private static final DateTimeFormatter INST_FORMATTER =
      DateTimeFormatter.ofPattern(CnsCommonConstants.DEFALUT_DATE_FORMAT);

  private Utils() {
    // Private constructor
  }

  /**
   * @param str input
   * @return true, if input is null or empty
   */
  public static boolean isEmpty(final String str) {
    return (str == null) || str.isEmpty();
  }

  /**
   * @param instant input
   * @param id ZoneId
   * @return date formatted to default format defined by {@link CnsCommonConstants#DEFALUT_DATE_FORMAT}
   */
  public static String instantToString(final Instant instant, final ZoneId id) {
    ZoneId idToUse = id == null ? ZoneOffset.UTC : id;
    return instant == null ? null : INST_FORMATTER.withZone(idToUse).format(instant);
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
   * @param str string input. Input should not contain number separators. (,/.)
   * @return true, if input is integer
   */
  public static boolean isInteger(final String str) {
    if (isEmpty(str)) {
      return false;
    }

    String strToCheck = str.trim();
    for (int chrIdx = 0; chrIdx < strToCheck.length(); chrIdx++) {
      if (!Character.isDigit(strToCheck.charAt(chrIdx))) {
        return false;
      }
    }

    return true;
  }

}
