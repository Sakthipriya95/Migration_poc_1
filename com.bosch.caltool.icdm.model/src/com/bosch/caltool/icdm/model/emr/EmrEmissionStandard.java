/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrEmissionStandard implements IModel, Comparable<EmrEmissionStandard> {

  /**
   *
   */
  private static final long serialVersionUID = -8666013886308802565L;


  /** The id. */
  private Long id;


  /** The version. */
  private Long version;

  /** The emission standard flag. */
  private boolean emissionStandardFlag;

  /** The emission standard name. */
  private String emissionStandardName;

  /** The measures flag. */
  private boolean measuresFlag;

  /** The testcase flag. */
  private boolean testcaseFlag;

  /** parent id **/
  private Long parentId;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrEmissionStandard cat) {
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
      return ModelUtil.isEqual(getId(), ((EmrEmissionStandard) obj).getId());

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
   * @return the emissionStandardFlag
   */
  public boolean getEmissionStandardFlag() {
    return this.emissionStandardFlag;
  }


  /**
   * @param emissionStandardFlag the emissionStandardFlag to set
   */
  public void setEmissionStandardFlag(final boolean emissionStandardFlag) {
    this.emissionStandardFlag = emissionStandardFlag;
  }


  /**
   * @return the emissionStandardName
   */
  public String getEmissionStandardName() {
    return this.emissionStandardName;
  }


  /**
   * @param emissionStandardName the emissionStandardName to set
   */
  public void setEmissionStandardName(final String emissionStandardName) {
    this.emissionStandardName = emissionStandardName;
  }


  /**
   * @return the measuresFlag
   */
  public boolean getMeasuresFlag() {
    return this.measuresFlag;
  }


  /**
   * @param measuresFlag the measuresFlag to set
   */
  public void setMeasuresFlag(final boolean measuresFlag) {
    this.measuresFlag = measuresFlag;
  }


  /**
   * @return the testcaseFlag
   */
  public boolean getTestcaseFlag() {
    return this.testcaseFlag;
  }


  /**
   * @param testcaseFlag the testcaseFlag to set
   */
  public void setTestcaseFlag(final boolean testcaseFlag) {
    this.testcaseFlag = testcaseFlag;
  }


  /**
   * @return the parentId
   */
  public Long getParentId() {
    return this.parentId;
  }


  /**
   * @param parentId the parentId to set
   */
  public void setParentId(final Long parentId) {
    this.parentId = parentId;
  }

}
