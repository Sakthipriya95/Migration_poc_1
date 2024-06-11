/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrCategory implements IModel, Comparable<EmrCategory> {

  /**
   *
   */
  private static final long serialVersionUID = -5451737206572997401L;

  /** The id. */
  private Long id;

  /** The name. */
  private String name;

  /** The version. */
  private Long version;


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrCategory cat) {
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
      return ModelUtil.isEqual(getId(), ((EmrCategory) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((EmrCategory) obj).getName());
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
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * Sets the name.
   *
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }


}
