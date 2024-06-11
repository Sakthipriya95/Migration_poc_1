/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.Map;
import java.util.TreeMap;

import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * The Class A2lWorkPackage.
 *
 * @author rgo7cob
 */
public class A2lWpObj implements Comparable<A2lWpObj> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -558217643685990620L;

  /** The wp group name. */
  private String wpGroupName;

  /** The wp number. */
  private String wpNumber;

  /** The source. */
  private String source;

  /** The fc 2 wp id. */
  private Long fc2wpId;

  /** The wp name. */
  private String wpName;

  /** The function map. */
  private Map<String, String> functionMap = new TreeMap<>();

  /** Defines constant for hash code prime. */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * enum to declare the sort columns.
   */
  public enum SortColumns {

                           /** Group Name column. */
                           SORT_WP_GROUP_NAME,

                           /** Work Package Name. */
                           SORT_WP_NAME,

                           /** Work Package Number. */
                           SORT_WP_NUMBER

  }


  /**
   * Sets the function map.
   *
   * @param functionMap the functionMap to set
   */
  public void setFunctionMap(final Map<String, String> functionMap) {
    this.functionMap = functionMap;
  }

  /**
   * Gets the wp number.
   *
   * @return the wpNumber
   */
  public String getWpNumber() {
    return this.wpNumber;
  }


  /**
   * Gets the source.
   *
   * @return the source
   */
  public String getSource() {
    return this.source;
  }


  /**
   * Sets the source.
   *
   * @param source the source to set
   */
  public void setSource(final String source) {
    this.source = source;
  }


  /**
   * Gets the function map.
   *
   * @return the functionMap
   */
  public Map<String, String> getFunctionMap() {
    return this.functionMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getWpGroupName() == null) ? 0 : getWpGroupName().hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    A2lWpObj other = (A2lWpObj) obj;
    return ModelUtil.isEqual(getWpGroupName(), other.getWpGroupName());
  }

  /**
   * Gets the wp name.
   *
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * Gets the fc 2 wp id.
   *
   * @return the fc2wpId
   */
  public Long getFc2wpId() {
    return this.fc2wpId;
  }


  /**
   * Sets the fc 2 wp id.
   *
   * @param fc2wpId the fc2wpId to set
   */
  public void setFc2wpId(final Long fc2wpId) {
    this.fc2wpId = fc2wpId;
  }


  /**
   * Sets the wp number.
   *
   * @param wpNumber the wpNumber to set
   */
  public void setWpNumber(final String wpNumber) {
    this.wpNumber = wpNumber;
  }


  /**
   * Sets the wp name.
   *
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }


  /**
   * Gets the wp group name.
   *
   * @return the wpGroupName
   */
  public String getWpGroupName() {
    return this.wpGroupName;
  }


  /**
   * Sets the wp group name.
   *
   * @param wpGroupName the wpGroupName to set
   */
  public void setWpGroupName(final String wpGroupName) {
    this.wpGroupName = wpGroupName;
  }

  /**
   * Compare to.
   *
   * @param workPackage workPackage
   * @param sortColumn sortColumn
   * @return the compare value
   */
  public int compareTo(final A2lWpObj workPackage, final SortColumns sortColumn) {
    int compareResult = 0;

    switch (sortColumn) {
      // Sort based on Work package group name
      case SORT_WP_GROUP_NAME:
        compareResult = ModelUtil.compare(getWpGroupName(), workPackage.getWpGroupName());
        break;
      // Sort based on Work package name
      case SORT_WP_NAME:
        compareResult = ModelUtil.compare(getWpName(), workPackage.getWpName());
        break;
      // Sort based on work packege number
      case SORT_WP_NUMBER:
        if (workPackage.getSource().equals(ApicConstants.FC_WP_MAPPING)) {
          compareResult = ModelUtil.compare(getWpNumber(), workPackage.getWpNumber());
        }
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ModelUtil.compare(getWpGroupName(), workPackage.getWpGroupName());
    }

    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWpObj arg0) {
    int value1 = ModelUtil.compare(getWpGroupName(), arg0.getWpGroupName());
    if (value1 == 0) {
      return ModelUtil.compare(getWpName(), arg0.getWpName());
    }
    return value1;
  }

}
