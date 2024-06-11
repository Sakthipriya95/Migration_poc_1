/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author rgo7cob
 */
public class SSDProjectVersion implements Comparable<SSDProjectVersion> {


  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Value column
                            */
                           SORT_VER_NAME,
                           /**
                            * Unit column
                            */
                           SORT_VER_NUM,
                           /**
                            * Description column
                            */
                           SORT_VER_DESC,

  }

  /**
   * version id
   */
  private long versionId;
  /**
   * version number
   */

  private String versionNumber;
  /**
   * version name
   */
  private String versionName;
  /**
   * version desc
   */

  private String versionDesc;
  /**
   * project Id
   */
  private long projectId;

  /**
   * @return the versionId
   */
  public long getVersionId() {
    return this.versionId;
  }


  /**
   * @param versionId the versionId to set
   */
  public void setVersionId(final long versionId) {
    this.versionId = versionId;
  }


  /**
   * @return the versionNumber
   */
  public String getVersionNumber() {
    return this.versionNumber;
  }


  /**
   * @param versionNumber the versionNumber to set
   */
  public void setVersionNumber(final String versionNumber) {
    this.versionNumber = versionNumber;
  }


  /**
   * @return the versionName
   */
  public String getVersionName() {
    return this.versionName;
  }


  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }


  /**
   * @return the versionDesc
   */
  public String getVersionDesc() {
    return this.versionDesc;
  }


  /**
   * @param versionDesc the versionDesc to set
   */
  public void setVersionDesc(final String versionDesc) {
    this.versionDesc = versionDesc;
  }


  /**
   * @return the projectId
   */
  public long getProjectId() {
    return this.projectId;
  }


  /**
   * @param projectId the projectId to set
   */
  public void setProjectId(final long projectId) {
    this.projectId = projectId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final SSDProjectVersion projVersion) {

    return ModelUtil.compare(this.versionName, projVersion.versionName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }


  /**
   * @param version2 version2
   * @param sortColumn sort column name
   * @return
   */
  public int compareTo(final SSDProjectVersion version2, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {

      case SORT_VER_NAME:
        compareResult = getVersionName().compareTo(version2.getVersionName());
        break;
      case SORT_VER_NUM:
        compareResult = getVersionNumber().compareTo(version2.getVersionNumber());
        break;
      // Icdm-830 Data Model Changes
      case SORT_VER_DESC:
        compareResult = getVersionDesc().compareTo(version2.getVersionDesc());
        break;

      default:
        compareResult = 0;
        break;
    }

    return compareResult;
  }
}
