/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink.utils;

/**
 * Utility methods
 *
 * @author bne4cob
 */
// ICDM-1649
public final class LinkUtils {

  /**
   * Private constructor for utility class
   */
  private LinkUtils() {
    // No implementation allowed
  }


  /**
   * Checks whether string is null or empty
   *
   * @param str input
   * @return true if string is null or empty
   */
  public static boolean isEmptyString(final String str) {
    return (str == null) || "".equals(str);
  }

}
