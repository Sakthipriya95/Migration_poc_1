/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.mail.client.util;

import java.util.Set;

/**
 *
 */
public class CommonUtils {

  /**
   *
   */
  private CommonUtils() {
    // Utility class
  }

  /**
   * @param value to check whether it is not null/Empty
   * @return returns true if empty.
   */
  public static boolean isEmpty(final String value) {
    return ((value == null) || "".equals(value));
  }

  /**
   * @param values to check whether it is not null/Empty
   * @return returns true if empty.
   */
  public static boolean isEmpty(final Set<String> values) {
    return ((values == null) || values.isEmpty());
  }


  /**
   * @param value to check whether it is not null/Empty
   * @return returns false if empty/null,returns true if not null/empty
   */
  public static boolean isNotEmpty(final String value) {
    if ((value == null) || value.equals("")) {
      return false;
    }
    return true;
  }

  /**
   * @param values to check whether it is not null/Empty
   * @return returns false if empty/null,returns true if not null/empty
   */
  public static boolean isSetNotEmpty(final Set<String> values) {
    return values != null;
  }
}
