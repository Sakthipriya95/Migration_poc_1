/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class RvwCommentTemplate implements Cloneable, Comparable<RvwCommentTemplate>, IDataObject {

  /**
   * 
   */
  private static final long serialVersionUID = 291739730251347437L;

  /**
   * Comment Id
   */
  private Long id;

  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Version
   */
  private Long version;

  /**
   * Display name
   */
  private String name;

  /**
   * Empty not used
   */
  private String description;


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
  public int compareTo(final RvwCommentTemplate object) {
    return ModelUtil.compare(getId(), object.getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((RvwCommentTemplate) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return the id
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
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
   * Empty not used
   * 
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * Empty not used
   * 
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }


}
