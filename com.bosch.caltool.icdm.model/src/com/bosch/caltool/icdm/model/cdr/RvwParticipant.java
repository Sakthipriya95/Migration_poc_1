/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Review Paticipants Model class
 *
 * @author bru2cob
 */
public class RvwParticipant implements Comparable<RvwParticipant>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = -4636822040636402192L;
  /**
   * Participant Id
   */
  private Long id;
  /**
   * Result Id
   */
  private Long resultId;
  /**
   * User Id
   */
  private Long userId;
  /**
   * Activity Type
   */
  private String activityType;
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
   * user name
   */
  private String description;

  private boolean editFlag = false;

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
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return resultId
   */
  public Long getResultId() {
    return this.resultId;
  }

  /**
   * @param resultId set resultId
   */
  public void setResultId(final Long resultId) {
    this.resultId = resultId;
  }

  /**
   * @return userId
   */
  public Long getUserId() {
    return this.userId;
  }

  /**
   * @param userId set userId
   */
  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  /**
   * @return activityType
   */
  public String getActivityType() {
    return this.activityType;
  }

  /**
   * @param activityType set activityType
   */
  public void setActivityType(final String activityType) {
    this.activityType = activityType;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
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
  public int compareTo(final RvwParticipant object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((RvwParticipant) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the editFlag
   */
  public boolean isEditFlag() {
    return this.editFlag;
  }


  /**
   * @param editFlag the editFlag to set
   */
  public void setEditFlag(final boolean editFlag) {
    this.editFlag = editFlag;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

}
