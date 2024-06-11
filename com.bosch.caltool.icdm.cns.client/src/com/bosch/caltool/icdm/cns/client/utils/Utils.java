/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.utils;


/**
 * @author BNE4COB
 */
public final class Utils {

  private Utils() {
    // Private constructor
  }

  /**
   * @param str input
   * @return true, if input is null or empty
   */
  public static boolean isNullOrEmpty(final String str) {
    return (str == null) || str.isEmpty();
  }
}
