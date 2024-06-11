/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class has been overridden the toString() method of java.lang.Object Class to show the list as a single string
 * joining with given delimiter
 *
 * @author svj7cob
 * @param <String> the string type
 */
@SuppressWarnings("hiding")
public class DelimiterJoinerCustomArrayList<String> extends ArrayList<String> {

  /**
   * serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * if the number to be included
   */
  private final boolean canTheListBeNumbered;
  /**
   * the given delimiter
   */
  private final java.lang.String delimiter;


  /**
   * This method implements toString() method of ArrayList. This method returns list having toString() method with the
   * feature of showing list as a single string joining with given delimiter <br>
   * For example, <br>
   * <code>
   * List<String> list = new getCustomArrayList<String>(true, ', ');<br>
   * list.add("one");<br>
   * list.add("two");<br>
   * list.add("three");<br>
   * System.out.println(list.toString());<br>
   * </code> <br>
   * gives the result as '1. one, 2. two, 3. three'
   *
   * @param canTheListBeNumberd , the flag if true, then serial number will be included in the list
   * @param delimitr delimiter
   */
  public DelimiterJoinerCustomArrayList(final boolean canTheListBeNumberd, final java.lang.String delimitr) {
    this.canTheListBeNumbered = canTheListBeNumberd;
    this.delimiter = delimitr;
  }


  @Override
  public java.lang.String toString() {
    Iterator<String> itr = iterator();
    if (!itr.hasNext()) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder();
    if (this.canTheListBeNumbered) {
      int count = 0;
      while (itr.hasNext()) {
        String str = itr.next();
        count++;
        stringBuilder.append(count).append(". ").append(str);
        stringBuilder.append(this.delimiter);
      }
    }
    else {
      while (itr.hasNext()) {
        String str = itr.next();
        stringBuilder.append(str);
        stringBuilder.append(this.delimiter);
      }
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - this.delimiter.length());
    return stringBuilder.toString();
  }
}
