/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrColumn implements IModel, Comparable<EmrColumn> {

  /**
   *
   */
  private static final long serialVersionUID = -2042640348818762652L;

  /** The id. */
  private Long id;

  /** The name. */
  private String name;

  /** The version. */
  private Long version;

  /** normalized flag **/
  private String nomalizedFlag;


  /**
   * @return the nomalizedFlag
   */
  public String getNomalizedFlag() {
    return this.nomalizedFlag;
  }


  /**
   * @param nomalizedFlag the nomalizedFlag to set
   */
  public void setNomalizedFlag(final String nomalizedFlag) {
    this.nomalizedFlag = nomalizedFlag;
  }


  /**
   * @return the numericFlag
   */
  public String getNumericFlag() {
    return this.numericFlag;
  }


  /**
   * @param numericFlag the numericFlag to set
   */
  public void setNumericFlag(final String numericFlag) {
    this.numericFlag = numericFlag;
  }


  /** The numeric flag. */
  private String numericFlag;


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

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrColumn cat) {
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
      return ModelUtil.isEqual(getId(), ((EmrColumn) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((EmrColumn) obj).getName());
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

}
