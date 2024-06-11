/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comparator;


/**
 * @author adn1cob
 */
public abstract class AbstractCompareResult {

  /**
   * Object1 name for header
   */
  private String firstObjName;

  /**
   * Object2 name for header
   */
  private String secondObjName;

  /**
   * indicate difference
   */
  private boolean different;

  /**
   * @return the firstObjName
   */
  public String getCompareObjName1() {
    return this.firstObjName;
  }


  /**
   * @param compareObjName1 the firstObjName to set
   */
  public void setCompareObjName1(final String compareObjName1) {
    this.firstObjName = compareObjName1;
  }


  /**
   * @return the secondObjName
   */
  public String getCompareObjName2() {
    return this.secondObjName;
  }


  /**
   * @param compareObjName2 the secondObjName to set
   */
  public void setCompareObjName2(final String compareObjName2) {
    this.secondObjName = compareObjName2;
  }

  /**
   * @return the isDiff
   */
  public boolean isDiff() {
    return this.different;
  }


  /**
   * @param isDiff the isDiff to set
   */
  public void setDiff(final boolean isDiff) {
    this.different = isDiff;
  }

}
