/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.Map;
import java.util.TreeMap;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * @author rgo7cob
 */
@Deprecated
public class WorkPackage implements Comparable<WorkPackage> {


  private final WorkPackageGroup wpGroup;
  private final String wpNumber;
  private final String nameEng;
  private final String nameGer;
  private Map<String, String> functionMap = new TreeMap<String, String>();
  private String source;
  private final Language currentLanguage;
  private final FCToWP fc2wp;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * enum to declare the sort columns
   */
  public enum SortColumns {
                           /**
                            * Group Name column
                            */
                           SORT_WP_GROUP_NAME,
                           /**
                            * Work Package Name
                            */
                           SORT_WP_NAME,
                           /**
                            * Work Package Number
                            */
                           SORT_WP_NUMBER

  }


  /**
   * @param currentLanguage language
   * @param wpGroup wpGroup
   * @param fcToWpValues FC2WP obj
   * @param source mapping source
   */
  public WorkPackage(final Language currentLanguage, final WorkPackageGroup wpGroup, final FCToWP fcToWpValues,
      final String source) {
    super();
    this.wpGroup = wpGroup;
    this.wpNumber = fcToWpValues.getWpNumber();
    this.nameEng = fcToWpValues.getWpNameE();
    this.nameGer = fcToWpValues.getWpNameG();
    this.currentLanguage = currentLanguage;
    this.source = source;
    this.fc2wp = fcToWpValues;
  }

  /**
   * @return the nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }


  /**
   * @return the nameGer
   */
  public String getNameGer() {
    return this.nameGer;
  }


  /**
   * @param functionMap the functionMap to set
   */
  public void setFunctionMap(final Map<String, String> functionMap) {
    this.functionMap = functionMap;
  }

  /**
   * @return the wpNumber
   */
  public String getWpNumber() {
    return this.wpNumber;
  }


  /**
   * @return the source
   */
  public String getSource() {
    return this.source;
  }


  /**
   * @param source the source to set
   */
  public void setSource(final String source) {
    this.source = source;
  }


  /**
   * @return the functionMap
   */
  public Map<String, String> getFunctionMap() {
    return this.functionMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WorkPackage arg0) {
    int value1 = ApicUtil.compare(getWpGroup().getGroupName(), arg0.getWpGroup().getGroupName());
    if (value1 == 0) {
      return ApicUtil.compare(getWpName(), arg0.getWpName());
    }
    return value1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getWpGroup() == null) ? 0 : getWpGroup().getGroupName().hashCode());
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
    WorkPackage other = (WorkPackage) obj;
    return CommonUtils.isEqual(getWpGroup().getGroupName(), other.getWpGroup().getGroupName());

  }

  /**
   * @return the wpName
   */
  public String getWpName() {
    String returnValue = null;

    if (this.currentLanguage != null) {
      if (this.currentLanguage == Language.ENGLISH) {
        returnValue = getNameEng();
      }
      else if (this.currentLanguage == Language.GERMAN) {
        returnValue = getNameGer();
        if (CommonUtils.isEmptyString(returnValue)) {
          returnValue = getNameEng();
        }
      }
    }
    if (CommonUtils.isEmptyString(returnValue)) {
      returnValue = ApicConstants.NAME_NOT_DEFINED;
    }
    return returnValue;
  }

  /**
   * @return the wpGroup
   */
  public WorkPackageGroup getWpGroup() {
    return this.wpGroup;
  }

  /**
   * @return the fc2wp
   */
  public FCToWP getFc2wp() {
    return this.fc2wp;
  }

  /**
   * @param workPackage workPackage
   * @param sortColumn sortColumn
   * @return the compare value
   */
  public int compareTo(final WorkPackage workPackage, final SortColumns sortColumn) {
    // TODO Auto-generated method stub
    int compareResult = 0;

    switch (sortColumn) {
      // Sort based on Work package group name
      case SORT_WP_GROUP_NAME:
        compareResult = ApicUtil.compare(getWpGroup().getGroupName(), workPackage.getWpGroup().getGroupName());
        break;
      // Sort based on Work package name
      case SORT_WP_NAME:
        compareResult = ApicUtil.compare(getWpName(), workPackage.getWpName());
        break;
      // Sort based on work packege number
      case SORT_WP_NUMBER:
        if (workPackage.getSource().equals(ApicConstants.FC_WP_MAPPING)) {
          compareResult = ApicUtil.compare(getWpNumber(), workPackage.getWpNumber());
        }
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ApicUtil.compare(getWpGroup().getGroupName(), workPackage.getWpGroup().getGroupName());
    }

    return compareResult;

  }

}
