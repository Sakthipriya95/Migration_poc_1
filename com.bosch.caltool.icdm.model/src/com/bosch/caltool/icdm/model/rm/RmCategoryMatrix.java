/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.rm;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author rgo7cob
 */
public class RmCategoryMatrix implements Comparable<RmCategoryMatrix>, IModel {


  /**
   *
   */
  private static final long serialVersionUID = -5445850427597585938L;

  private Long id;

  private Long version;

  private Long projChar;

  private Long category;


  private Long riskLvl;


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
  public int compareTo(final RmCategoryMatrix cat) {
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
      return ModelUtil.isEqual(getId(), ((RmCategoryMatrix) obj).getId());
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
   * @return the projChar
   */
  public Long getProjChar() {
    return this.projChar;
  }


  /**
   * @param projChar the projChar to set
   */
  public void setProjChar(final Long projChar) {
    this.projChar = projChar;
  }


  /**
   * @return the category
   */
  public Long getCategory() {
    return this.category;
  }


  /**
   * @param category the category to set
   */
  public void setCategory(final Long category) {
    this.category = category;
  }


  /**
   * @return the riskLvl
   */
  public Long getRiskLvl() {
    return this.riskLvl;
  }


  /**
   * @param riskLvl the riskLvl to set
   */
  public void setRiskLvl(final Long riskLvl) {
    this.riskLvl = riskLvl;
  }

}
