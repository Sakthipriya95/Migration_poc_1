/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrColumnValue implements IModel, Comparable<EmrColumnValue> {

  /**
   *
   */
  private static final long serialVersionUID = 6437213929287504068L;


  /** The id. */
  private Long id;


  /** The version. */
  private Long version;

  /** The value. */
  private String value;

  /** Col id **/
  private Long colId;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrColumnValue cat) {
    return ModelUtil.compare(getId(), cat.getId());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((EmrColumnValue) obj).getId());

    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
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
  public void setId(final Long catId) {
    this.id = catId;
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
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value value
   */
  public void setValue(final String value) {
    this.value = value;
  }


  /**
   * @return the colId
   */
  public Long getColId() {
    return this.colId;
  }


  /**
   * @param colId the colId to set
   */
  public void setColId(final Long colId) {
    this.colId = colId;
  }


}
