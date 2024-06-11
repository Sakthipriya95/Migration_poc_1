/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrPidcVariant implements IModel, Comparable<EmrPidcVariant> {

  /**
   *
   */
  private static final long serialVersionUID = 6509345473259842687L;

  /** The id. */
  private Long id;

  /** The version. */
  private Long version;

  /** emission standard id */
  private Long emissionStdId;

  /** file id */
  private Long emrFileId;

  /** pidc Variant id */
  private Long pidcVariantId;

  /** EMS variant */
  private Long emrVariant;

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
   * @return the emissionStdId
   */
  public Long getEmissionStdId() {
    return this.emissionStdId;
  }


  /**
   * @param emissionStdId the emissionStdId to set
   */
  public void setEmissionStdId(final Long emissionStdId) {
    this.emissionStdId = emissionStdId;
  }

  /**
   * @return the pidcVariantId
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }


  /**
   * @param pidcVariantId the pidcVariantId to set
   */
  public void setPidcVariantId(final Long pidcVariantId) {
    this.pidcVariantId = pidcVariantId;
  }


  /**
   * @return the emrFileId
   */
  public Long getEmrFileId() {
    return this.emrFileId;
  }


  /**
   * @param emrFileId the emrFileId to set
   */
  public void setEmrFileId(final Long emrFileId) {
    this.emrFileId = emrFileId;
  }


  /**
   * @return the emsVariant
   */
  public Long getEmrVariant() {
    return this.emrVariant;
  }


  /**
   * @param emrVariant the emsVariant to set
   */
  public void setEmrVariant(final Long emrVariant) {
    this.emrVariant = emrVariant;
  }

  /*
   * @Override public EmrPidcVariant clone() { try { return (EmrPidcVariant) super.clone(); } catch
   * (CloneNotSupportedException e) { throw new CloneNotSupportedRuntimeException(e); } }
   */

  /**
   * Copy Constructor
   *
   * @param emrPidcVariant EmrPidcVariant Object
   */
  public EmrPidcVariant(final EmrPidcVariant emrPidcVariant) {
    this.emissionStdId = emrPidcVariant.emissionStdId;
    this.emrFileId = emrPidcVariant.emrFileId;
    this.emrVariant = emrPidcVariant.emrVariant;
    this.id = emrPidcVariant.id;
    this.pidcVariantId = emrPidcVariant.pidcVariantId;
    this.version = emrPidcVariant.version;
  }


  /**
   *
   */
  public EmrPidcVariant() {
    // Public constructor
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrPidcVariant cat) {
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
      if ((null == getId()) && (null == ((EmrPidcVariant) obj).getId())) {
        // The id is null which means the object is not yet created in the DB
        return ModelUtil.isEqual(getEmissionStdId(), ((EmrPidcVariant) obj).getEmissionStdId()) &&
            ModelUtil.isEqual(getEmrFileId(), ((EmrPidcVariant) obj).getEmrFileId()) &&
            ModelUtil.isEqual(getPidcVariantId(), ((EmrPidcVariant) obj).getPidcVariantId());
      }
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((EmrPidcVariant) obj).getId());
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