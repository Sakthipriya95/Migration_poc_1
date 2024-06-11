/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons.data;

import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * @author bne4cob
 */
public class PidcConsInfo extends AbstractValidationResult implements Comparable<PidcConsInfo> {
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  /**
   * Project ID
   */
  private Long projectID;

  /**
   * Project Name
   */
  private String projectName;

  /**
   * @return the projectID
   */
  public final Long getProjectID() {
    return this.projectID;
  }

  /**
   * @param projectID the projectID to set
   */
  public final void setProjectID(final Long projectID) {
    this.projectID = projectID;
  }


  /**
   * @return the projectName
   */
  public final String getProjectName() {
    return this.projectName;
  }

  /**
   * @param projectName the projectName to set
   */
  public final void setProjectName(final String projectName) {
    this.projectName = projectName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSummary() {
    return getProjectName() + '\t' + getProjectID() + '\t' + getErrorType();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcConsInfo other) {
    return getSummary().compareTo(other.getSummary());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getSummary() == null) ? 0 : getSummary().hashCode());
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
    PidcConsInfo other = (PidcConsInfo) obj;
    return CommonUtils.isEqual(getSummary(), other.getSummary());
  }

}
