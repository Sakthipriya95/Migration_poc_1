/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author rgo7cob
 */
public class WpRespModel implements Comparable<WpRespModel> {


  private String wpName;

  private A2lResponsibility a2lResponsibility;

  private String wpRespName;

  private Long a2lWpId;

  // Mapped Parameter count
  private Long paramCount;

  /**
   * enum to declare the sort columns.
   */
  public enum SortColumns {

                           /** Workpackage Name. */
                           SORT_WP_NAME,

                           /** Work Package Responsible. */
                           SORT_WP_RESPONSIBLE,

                           /** Work Package Responsible. */
                           SORT_WP_PARAM_COUNT
  }


  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }


  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }


  /**
   * @return the a2lResponsibility
   */
  public A2lResponsibility getA2lResponsibility() {
    return this.a2lResponsibility;
  }


  /**
   * @param a2lResp the a2lResponsibility to set
   */
  public void setA2lResponsibility(final A2lResponsibility a2lResp) {
    this.a2lResponsibility = a2lResp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getA2lResponsibility().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    if (obj.getClass() == this.getClass()) {
      WpRespModel respModel = (WpRespModel) obj;
      // Removed the condition wp Resp model not null. beacuse type caste the object will throw Npe if Obj is null.

      int compare = ModelUtil.compare(getWpName(), respModel.getWpName());
      if (compare == 0) {
        compare = ModelUtil.compare(getA2lResponsibility().getId(), respModel.getA2lResponsibility().getId());
      }
      return compare == 0;

    }
    return false;
  }


  /**
   * Compare to.
   *
   * @param workPackage workPackage
   * @param sortColumn sortColumn
   * @return the compare value
   */
  public int compareTo(final WpRespModel workPackage, final SortColumns sortColumn) {
    int compareResult = 0;

    switch (sortColumn) {
      // Sort based on Work package group name
      case SORT_WP_NAME:
        compareResult = ModelUtil.compare(getWpName(), workPackage.getWpName());
        break;
      // Sort based on Work package name
      case SORT_WP_RESPONSIBLE:
        compareResult = ModelUtil.compare(getWpRespName(), workPackage.getWpRespName());
        break;
      // Sort based on Work package name
      case SORT_WP_PARAM_COUNT:
        compareResult = ModelUtil.compare(getParamCount(), workPackage.getParamCount());
        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ModelUtil.compare(getWpName(), workPackage.getWpName());
    }

    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WpRespModel arg0) {
    int value1 = ModelUtil.compare(getWpName(), arg0.getWpName());
    if (value1 == 0) {
      return ModelUtil.compare(getWpRespName(), arg0.getWpRespName());
    }
    return value1;
  }

  /**
   * @return the wpRespId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }


  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }


  /**
   * @return the wpRespName
   */
  public String getWpRespName() {
    return this.wpRespName;
  }


  /**
   * @param wpRespName the wpRespName to set
   */
  public void setWpRespName(final String wpRespName) {
    this.wpRespName = wpRespName;
  }


  /**
   * @return the paramCount
   */
  public Long getParamCount() {
    return this.paramCount;
  }


  /**
   * @param paramCount the paramCount to set
   */
  public void setParamCount(final Long paramCount) {
    this.paramCount = paramCount;
  }
}
