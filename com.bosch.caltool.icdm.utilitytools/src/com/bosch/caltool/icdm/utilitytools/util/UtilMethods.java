/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.util;


/**
 * @author bne4cob
 */
public final class UtilMethods {

  /**
   * Checks whether two objects are equal
   * 
   * @param <A> first object type
   * @param <B> second object type
   * @param object1 first object
   * @param object2 second object
   * @return true/false
   */
  public static <A, B> boolean isEqual(final A object1, final B object2) {
    if (object1 == null) {
      if (object2 == null) {
        return true;
      }
      return false;
    }

    return object1.equals(object2);

  }
}
