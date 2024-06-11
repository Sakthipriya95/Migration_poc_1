/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

import com.bosch.caltool.datamodel.core.IModel;

/**
 * @author rgo7cob
 */
public class Unit implements Comparable<Unit>, IModel {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 346349912955372527L;
  private Long id;
  private Long version;
  private String unitName;
  private String createdDate;
  private static final int HASH_CODE_PRIME_31 = 31;

  /*
   *
   */

  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getUnitName() == null) ? 0 : getUnitName().hashCode());
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
    Unit other = (Unit) obj;
    return getUnitName().equals(other.getUnitName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Unit other) {
    return getUnitName().compareTo(other.getUnitName());
  }


  /**
   * @return the unit
   */
  public String getUnitName() {
    return this.unitName;
  }


  /**
   * @param unitName the unit to set
   */
  public void setUnitName(final String unitName) {
    this.unitName = unitName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.id = objId;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

}
