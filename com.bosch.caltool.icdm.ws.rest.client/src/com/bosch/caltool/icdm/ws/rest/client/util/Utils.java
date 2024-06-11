/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.util;


/**
 * @author bne4cob
 */
public final class Utils {

  /**
   * Private constructor
   */
  private Utils() {
    // private Constructor
  }

  /**
   * @param str input
   * @return true if string is null or empty
   */
  public static boolean isEmptyString(final String str) {
    return (str == null) || str.isEmpty();
  }
}
