/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author bne4cob
 */
public class NodeAccess implements IDataObject, Comparable<NodeAccess>, Cloneable {

  /**
   *
   */
  private static final long serialVersionUID = 1496131661792441242L;

  private Long id;

  private Long userId;
  /**
   * User name
   */
  private String name;
  /**
   * User description
   */
  private String description;

  private Long nodeId;
  private String nodeType;

  private boolean read;
  private boolean write;
  private boolean grant;
  private boolean owner;

  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;
  private Long version;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;


  /**
   * @return the objId
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param objId the objId to set
   */
  @Override
  public void setId(final Long objId) {
    this.id = objId;
  }


  /**
   * @return the userId
   */
  public Long getUserId() {
    return this.userId;
  }


  /**
   * @param userId the userId to set
   */
  public void setUserId(final Long userId) {
    this.userId = userId;
  }


  /**
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the nodeId
   */
  public Long getNodeId() {
    return this.nodeId;
  }


  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final Long nodeId) {
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
   * @return the grant
   */
  public boolean isGrant() {
    return this.grant;
  }


  /**
   * @param grant the grant to set
   */
  public void setGrant(final boolean grant) {
    this.grant = grant;
  }


  /**
   * @return the read
   */
  public boolean isRead() {
    return this.read;
  }


  /**
   * @param read the read to set
   */
  public void setRead(final boolean read) {
    this.read = read;
  }


  /**
   * @return the write
   */
  public boolean isWrite() {
    return this.write;
  }


  /**
   * @param write the write to set
   */
  public void setWrite(final boolean write) {
    this.write = write;
  }


  /**
   * @return the owner
   */
  public boolean isOwner() {
    return this.owner;
  }


  /**
   * @param owner the owner to set
   */
  public void setOwner(final boolean owner) {
    this.owner = owner;
  }


  /**
   * @return the createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NodeAccess obj) {
    return ModelUtil.compare(getId(), obj.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getId() == null) ? 0 : getId().hashCode());
    return result;
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
    NodeAccess other = (NodeAccess) obj;
    if ((getId() == null) || (other.getId() == null)) {
      return false;
    }
    return getId().equals(other.getId());
  }

  @Override
  public NodeAccess clone() {
    try {
      return (NodeAccess) super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new CloneNotSupportedRuntimeException(e);
    }
  }
}

