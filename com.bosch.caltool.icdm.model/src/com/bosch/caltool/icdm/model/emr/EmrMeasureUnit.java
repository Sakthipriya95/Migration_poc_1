/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrMeasureUnit implements IModel, Comparable<EmrMeasureUnit> {


  /**
   *
   */
  private static final long serialVersionUID = 6672535109518586954L;

  /** The id. */
  private Long id;

  /** The version. */
  private Long version;
  /** The measure unit name. */
  private String measureUnitName;


  /**
   * @return the measureUnitName
   */
  public String getMeasureUnitName() {
    return this.measureUnitName;
  }


  /**
   * @param measureUnitName the measureUnitName to set
   */
  public void setMeasureUnitName(final String measureUnitName) {
    this.measureUnitName = measureUnitName;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrMeasureUnit fileObj) {
    return ModelUtil.compare(getId(), fileObj.getId());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }


    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((EmrMeasureUnit) obj).getId());


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
