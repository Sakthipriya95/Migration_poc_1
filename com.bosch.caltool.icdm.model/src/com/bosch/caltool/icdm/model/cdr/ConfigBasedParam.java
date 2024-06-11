/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * Icdm-1073 Listing the Rules in Config Based View
 *
 * @author rgo7cob
 */
public class ConfigBasedParam implements Comparable<ConfigBasedParam> {

  private final IParameter parameter;

  private boolean checked;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @param param abst param
   */
  public ConfigBasedParam(final IParameter param) {
    this.parameter = param;
  }


  /**
   * @return the parameter
   */
  public IParameter getParameter() {
    return this.parameter;
  }


  /**
   * @return the checked
   */
  public boolean isChecked() {
    return this.checked;
  }


  /**
   * @param checked the checked to set
   */
  public void setChecked(final boolean checked) {
    this.checked = checked;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ConfigBasedParam obj) {

    return ModelUtil.compare(getParameter().getName(), obj.getParameter().getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((this.parameter == null) ? 0 : this.parameter.getName().hashCode());
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
    ConfigBasedParam other = (ConfigBasedParam) obj;

    return ModelUtil.isEqualIgnoreCase(getParameter().getName(), other.getParameter().getName());
  }
}
