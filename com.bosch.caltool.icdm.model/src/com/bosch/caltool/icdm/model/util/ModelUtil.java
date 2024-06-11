/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.util;

import java.text.Collator;
import java.util.Locale;
import java.util.Objects;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.datamodel.core.IDataObject;

/**
 * @author bru2cob
 */
public class ModelUtil {

  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * Private constructor for utility class
   */
  private ModelUtil() {
    // Private constructor for utility class
  }

  /**
   * Checks whether two objects are equal. This will also consider the input(s) being null.
   *
   * @param <O> Any object
   * @param obj1 first object
   * @param obj2 second object
   * @return true/false
   */
  public static <O> boolean isEqual(final O obj1, final O obj2) {
    return Objects.equals(obj1, obj2);
  }


  /**
   * Generates hashcode for given values
   *
   * @param objs input
   * @return hashcode
   */
  public static int generateHashCode(final Object... objs) {
    int result = 1;
    for (Object obj : objs) {
      result = (HASHCODE_PRIME * result) + ((obj == null) ? 0 : obj.hashCode());
    }
    return result;


  }

  /**
   * Compare two String values Consider a NULL value as LESS THAN a not NULL value.
   *
   * @param name first string
   * @param name2 second string
   * @return compare status
   */
  public static int compare(final String name, final String name2) {

    if ((name == null) && (name2 == null)) {
      // both Strings are NULL => return EQUAL
      return 0;
    }
    if (name == null) {
      // first String is NULL => return LESS THAN
      return -1;
    }
    if (name2 == null) {
      // second String is NULL => return GREATER THAN
      return 1;
    }

    // both String are not NULL, compare them
    final Collator collator = Collator.getInstance(Locale.GERMAN);
    collator.setStrength(Collator.IDENTICAL);
    return collator.compare(name, name2);


  }

  /**
   * @param <O > basic object
   * @param obj1 object 1
   * @param obj2 object 2
   * @return -1/0/1
   */
  public static <O extends IBasicObject> int compare(final O obj1, final O obj2) {
    if ((obj1 == null) && (obj2 == null)) {
      // both Strings are NULL => return EQUAL
      return 0;
    }
    if (obj1 == null) {
      // first String is NULL => return LESS THAN
      return -1;
    }
    if (obj2 == null) {
      // second String is NULL => return GREATER THAN
      return 1;
    }

    return compare(obj1.getName(), obj2.getName());
  }

  /**
   * @param <O > data object
   * @param obj1 object 1
   * @param obj2 object 2
   * @return -1/0/1
   */
  public static <O extends IDataObject> int compare(final O obj1, final O obj2) {
    if ((obj1 == null) && (obj2 == null)) {
      // both Strings are NULL => return EQUAL
      return 0;
    }
    if (obj1 == null) {
      // first String is NULL => return LESS THAN
      return -1;
    }
    if (obj2 == null) {
      // second String is NULL => return GREATER THAN
      return 1;
    }

    int ret = compare((IBasicObject) obj1, (IBasicObject) obj2);

    return ret == 0 ? compare(obj1.getId(), obj2.getId()) : ret;
  }

  /**
   * This method returns an empty string if it is null.
   *
   * @param str str to be checked
   * @return either input string itself or empty string
   */
  public static String checkNull(final String str) {
    return (null == str) ? "" : str;
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
   * String equality check case insensitively. Uses equalsIgnoreCase() method. Checks for null also
   *
   * @param str1 first string
   * @param str2 second string
   * @return true, if strings are equal, case insensitive
   */
  public static boolean isEqualIgnoreCase(final String str1, final String str2) {
    if ((str1 == null) && (str2 == null)) {
      return true;
    }
    if ((str1 == null) || (str2 == null)) {
      // if any one obj is null, then they are not equal as per first condition
      return false;
    }
    return str1.equalsIgnoreCase(str2);
  }
}
