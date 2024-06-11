/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TRL1COB
 */
public class FileNameUtil {

  /**
   * Private constructor for utiltity class
   */
  private FileNameUtil() {
    // Private constructor for utiltity class
  }

  /**
   * Replace char for invalid chars in file name
   */
  private static final String REPLACE_VALID_CHAR = "_";

  /**
   * Format file name, replaces certain special characters with '_' (underscore)
   *
   * @param fileName file name to be formatted
   * @return formatted file name
   */
  public static String formatFileName(final String fileName, final String regex) {
    // File name must be present
    if (fileName != null) {
      Matcher matcher = Pattern.compile(regex).matcher(fileName);
      return matcher.replaceAll(REPLACE_VALID_CHAR);
    }
    return null;
  }
}
