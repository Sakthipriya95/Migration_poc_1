/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.wp;

import java.util.Objects;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dja7cob Model class for WP resources
 */
public class WPResourceDetails implements Comparable<WPResourceDetails> {

  private String wpResCode;
  private Long wpResId;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the wpResId
   */
  public Long getWpResId() {
    return this.wpResId;
  }

  /**
   * @param wpResId the wpResId to set
   */
  public void setWpResId(final Long wpResId) {
    this.wpResId = wpResId;
  }

  /**
   * @return the wpResCode
   */
  public String getWpResCode() {
    return this.wpResCode;
  }

  /**
   * @param wpResCode the wpResCode to set
   */
  public void setWpResCode(final String wpResCode) {
    this.wpResCode = wpResCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WPResourceDetails o) {
    return ModelUtil.compare(getWpResCode(), o.getWpResCode());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getWpResId() == null) ? 0 : getWpResId().hashCode());
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
    WPResourceDetails other = (WPResourceDetails) obj;
    return Objects.equals(getWpResId(), other.getWpResId());
  }

}
