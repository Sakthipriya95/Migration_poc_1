/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrFile implements IModel, Comparable<EmrFile>, Cloneable {


  /**
   *
   */
  private static final long serialVersionUID = 86829992832584289L;

  /** The id. */
  private Long id;

  /** The version. */
  private Long version;

  /** The deleted flag. */
  private boolean deletedFlag;

  /** The description. */
  private String description;

  /** The is variant. */
  private boolean isVariant;

  /** The loaded without errors flag. */
  private boolean loadedWithoutErrorsFlag;

  /** icdm file id */
  private Long icdmFileId;

  /** pidc version id */
  private Long pidcVersId;

  /** created date **/
  private Date createdDate;

  /** name of file **/
  private String name;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrFile file) {
    int result = ModelUtil.compare(getName(), file.getName());
    if (result == 0) {
      result = ModelUtil.compare(getId(), file.getId());
    }
    return result;
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
      return ModelUtil.isEqual(getId(), ((EmrFile) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((EmrFile) obj).getName());

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
   * @return the deletedFlag
   */
  public boolean getDeletedFlag() {
    return this.deletedFlag;
  }


  /**
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(final boolean deletedFlag) {
    this.deletedFlag = deletedFlag;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the isVariant
   */
  public boolean getIsVariant() {
    return this.isVariant;
  }


  /**
   * @param isVariant the isVariant to set
   */
  public void setIsVariant(final boolean isVariant) {
    this.isVariant = isVariant;
  }


  /**
   * @return the loadedWithoutErrorsFlag
   */
  public boolean getLoadedWithoutErrorsFlag() {
    return this.loadedWithoutErrorsFlag;
  }


  /**
   * @param loadedWithoutErrorsFlag the loadedWithoutErrorsFlag to set
   */
  public void setLoadedWithoutErrorsFlag(final boolean loadedWithoutErrorsFlag) {
    this.loadedWithoutErrorsFlag = loadedWithoutErrorsFlag;
  }


  /**
   * @return the icdmFileId
   */
  public Long getIcdmFileId() {
    return this.icdmFileId;
  }


  /**
   * @param icdmFileId the icdmFileId to set
   */
  public void setIcdmFileId(final Long icdmFileId) {
    this.icdmFileId = icdmFileId;
  }


  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }


  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
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

  @Override
  public EmrFile clone() {
    try {
      return (EmrFile) super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new CloneNotSupportedRuntimeException(e);
    }
  }
}
