/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class EmrUploadError implements IModel, Comparable<EmrUploadError> {

  /**
   *
   */
  private static final long serialVersionUID = 777347819185981089L;

  /** The id. */
  private Long id;

  /** The version. */
  private Long version;


  /** The error message. */
  private String errorMessage;

  /** The cell content. */
  private String errorData;

  /** The row number. */
  private Long rowNumber;

  /** file id */
  private Long fileId;

  /**
   * error category
   */
  private String errorCategory;

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
   * @return the errorMessage
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }


  /**
   * @param errorMessage the errorMessage to set
   */
  public void setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * @return the rowNumber
   */
  public Long getRowNumber() {
    return this.rowNumber;
  }


  /**
   * @param rowNumber the rowNumber to set
   */
  public void setRowNumber(final Long rowNumber) {
    this.rowNumber = rowNumber;
  }


  /**
   * @return the fileId
   */
  public Long getFileId() {
    return this.fileId;
  }


  /**
   * @param fileId the fileId to set
   */
  public void setFileId(final Long fileId) {
    this.fileId = fileId;
  }

  /**
   * @return the errorCategory
   */
  public String getErrorCategory() {
    return this.errorCategory;
  }

  /**
   * @param errorCategory the errorCategory to set
   */
  public void setErrorCategory(final String errorCategory) {
    this.errorCategory = errorCategory;
  }


  /**
   * @return the errorData
   */
  public String getErrorData() {
    return this.errorData;
  }


  /**
   * @param errorData the errorData to set
   */
  public void setErrorData(final String errorData) {
    this.errorData = errorData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrUploadError cat) {
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
      return ModelUtil.isEqual(getId(), ((EmrUploadError) obj).getId());
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
