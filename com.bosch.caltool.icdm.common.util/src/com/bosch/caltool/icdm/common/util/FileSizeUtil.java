/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class to manipulate file size and gives string representation
 *
 * @author svj7cob
 */
public final class FileSizeUtil {

  /**
   * Reference value to be checked for each byte calculation
   */
  private static final double CHECK_REFRENCE = 1024.0;

  /**
   * Scaling upto the no. of decimal places
   */
  private static final int SCALING_DECIMAL = 2;

  /**
   * private constructor to initialize
   */
  private FileSizeUtil() {

  }

  /**
   * Gets the file size
   * 
   * @param size the size of the file
   * @return file size in string representation
   */
  public static String getFormattedSize(final Long size) {
    String returnValue;
    if (size >= CHECK_REFRENCE) {
      double kiloBytes = size / CHECK_REFRENCE;
      if (kiloBytes >= CHECK_REFRENCE) {
        double megaBytes = kiloBytes / CHECK_REFRENCE;
        if (megaBytes >= CHECK_REFRENCE) {
          double gigabytes = megaBytes / CHECK_REFRENCE;
          if (gigabytes >= CHECK_REFRENCE) {
            returnValue = getNormalizedValue(gigabytes) + " GB";
          }
          else {
            returnValue = getNormalizedValue(gigabytes) + " GB (" + getNormalizedValue(megaBytes) + " MB)";
          }
        }
        else {
          returnValue = getNormalizedValue(megaBytes) + " MB (" + getNormalizedValue(kiloBytes) + " KB)";
        }
      }
      else {
        returnValue = getNormalizedValue(kiloBytes) + " KB (" + getNormalizedValue(size) + " Bytes)";
      }
    }
    else {
      returnValue = size + " Bytes";
    }
    return returnValue;
  }

  private static BigDecimal getNormalizedValue(final double val) {
    BigDecimal bigDecimal = BigDecimal.valueOf(val);
    bigDecimal = bigDecimal.setScale(SCALING_DECIMAL, RoundingMode.HALF_EVEN);
    return bigDecimal;
  }

}
