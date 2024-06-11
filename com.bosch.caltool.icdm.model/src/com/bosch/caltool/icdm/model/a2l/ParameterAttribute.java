/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.icdm.model.cdr.AbstractParameterAttribute;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author rgo7cob
 */
public class ParameterAttribute extends AbstractParameterAttribute implements Comparable<ParameterAttribute> {


  /**
   *
   */
  private static final long serialVersionUID = 8267542553484174930L;

  private Long id;

  private Long version;

  private Long paramId;

  private Long attrId;


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ParameterAttribute paramAttr) {

    return ModelUtil.compare(getName(), paramAttr.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getAttrId() {
    return this.attrId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getParamId() {
    return this.paramId;
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
  public void setId(final Long id) {
    this.id = id;

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
   * {@inheritDoc}
   */
  @Override
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setParamId(final Long paramId) {
    this.paramId = paramId;

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
    return ModelUtil.isEqual(getId(), ((ParameterAttribute) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
