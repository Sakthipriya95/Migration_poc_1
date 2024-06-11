/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.ui.wizards;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * Model for Error table viewer (Error message and labels)
 */
public class ReleaseErrorModel implements Comparable<ReleaseErrorModel> {

  /**
   * Error msg
   */
  private String errorMessage;
  /**
   * Param names set
   */
  private SortedSet<String> labelSet = new TreeSet<String>();
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the var
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * @return set of labels
   */
  public SortedSet<String> getLabels() {
    return this.labelSet;
  }

  /**
   * @param var the var to set
   */
  public void setErrorMessage(final String err) {
    this.errorMessage = err;
  }

  /**
   * @return the value
   */
  public String getLabelCount() {
    return String.valueOf(this.labelSet.size());
  }

  /**
   * @param value the value to set
   */
  public void setLabels(final SortedSet<String> labelSet) {
    this.labelSet = labelSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReleaseErrorModel errObj) {
    return ApicUtil.compare(getErrorMessage(), errObj.getErrorMessage());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((this.errorMessage == null) ? 0 : this.errorMessage.hashCode());
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
    ReleaseErrorModel other = (ReleaseErrorModel) obj;
    // compare the error msgs
    return CommonUtils.isEqual(getErrorMessage(), other.getErrorMessage());
  }
}
