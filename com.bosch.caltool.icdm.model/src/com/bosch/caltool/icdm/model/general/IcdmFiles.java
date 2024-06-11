/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bru2cob
 */
public class IcdmFiles implements IModel, Comparable<IcdmFiles> {

  /**
   *
   */
  private static final long serialVersionUID = -5825157581148992365L;

  /** The id. */
  private Long id;

  /** The version. */
  private Long version;

  /** name of file **/
  private String name;

  /** File count. */
  private Long fileCount;

  /** Node id */
  private long nodeId;

  /** Node type */
  private String nodeType;

  /** created date **/
  private Date createdDate;

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IcdmFiles o) {
    int compareResult = ModelUtil.compare(getName(), o.getName());
    if (compareResult == 0) {
      compareResult = ModelUtil.compare(getId(), o.getId());
    }
    return compareResult;
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
      return ModelUtil.isEqual(getId(), ((IcdmFiles) obj).getId());
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


  /**
   * @return the fileCount
   */
  public Long getFileCount() {
    return this.fileCount;
  }


  /**
   * @param fileCount the fileCount to set
   */
  public void setFileCount(final Long fileCount) {
    this.fileCount = fileCount;
  }


  /**
   * @return the nodeId
   */
  public long getNodeId() {
    return this.nodeId;
  }


  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final long nodeId) {
    this.nodeId = nodeId;
  }


  /**
   * @return the nodeType
   */
  public String getNodeType() {
    return this.nodeType;
  }


  /**
   * @param nodeType the nodeType to set
   */
  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
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


}
