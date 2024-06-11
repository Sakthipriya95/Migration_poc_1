/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author rgo7cob
 */
public class RmCategoryMeasures implements Comparable<RmCategoryMeasures>, IModel {


  /**
   *
   */
  private static final long serialVersionUID = 3426069812145696738L;

  private Long id;

  private Long version;

  private Long categoryId;

  private Long riskLevel;

  private String engMeasure;

  private String gerMeasure;

  private String name;


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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RmCategoryMeasures cat) {
    /**
     * Sorting by ID to ensure Order in forming Nattable header
     */
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
      return ModelUtil.isEqual(getId(), ((RmCategoryMeasures) obj).getId());
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
   * @return the categoryId
   */
  public Long getCategoryId() {
    return this.categoryId;
  }


  /**
   * @param categoryId the categoryId to set
   */
  public void setCategoryId(final Long categoryId) {
    this.categoryId = categoryId;
  }


  /**
   * @return the riskLevel
   */
  public Long getRiskLevel() {
    return this.riskLevel;
  }


  /**
   * @param riskLevel the riskLevel to set
   */
  public void setRiskLevel(final Long riskLevel) {
    this.riskLevel = riskLevel;
  }


  /**
   * @return the engMeasure
   */
  public String getEngMeasure() {
    return this.engMeasure;
  }


  /**
   * @param engMeasure the engMeasure to set
   */
  public void setEngMeasure(final String engMeasure) {
    this.engMeasure = engMeasure;
  }


  /**
   * @return the gerMeasure
   */
  public String getGerMeasure() {
    return this.gerMeasure;
  }


  /**
   * @param gerMeasure the gerMeasure to set
   */
  public void setGerMeasure(final String gerMeasure) {
    this.gerMeasure = gerMeasure;
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

}
