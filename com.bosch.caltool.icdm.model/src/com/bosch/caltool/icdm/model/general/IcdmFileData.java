/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class IcdmFileData implements IModel, Comparable<IcdmFileData> {

  /**
   *
   */
  private static final long serialVersionUID = -7760934361102547872L;

  /** The id. */
  private Long id;

  /** The version. */
  private Long version;

  /** The icdm file id. */
  private Long icdmFileId;

  private byte[] fileData;


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
   * @return the fileData
   */
  public byte[] getFileData() {
    return this.fileData;
  }


  /**
   * @param fileData the fileData to set
   */
  public void setFileData(final byte[] fileData) {
    if (fileData != null) {
      this.fileData = fileData.clone();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IcdmFileData fileData) {
    return ModelUtil.compare(getId(), fileData.getId());
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
      return ModelUtil.isEqual(getId(), ((IcdmFileData) obj).getId());
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
