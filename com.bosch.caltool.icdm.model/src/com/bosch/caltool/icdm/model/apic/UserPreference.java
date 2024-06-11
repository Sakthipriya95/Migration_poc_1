/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * tUserPreference Model class
 *
 * @author EKIR1KOR
 */
public class UserPreference implements Cloneable, Comparable<UserPreference>, IModel {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 58845976002077L;
  /**
   * User Pref Id
   */
  private Long id;
  /**
   * User Id
   */
  private Long userId;
  /**
   * User Pref Key
   */
  private String userPrefKey;
  /**
   * User Pref Val
   */
  private String userPrefVal;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;

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
   * @return userPrefKey
   */
  public String getUserPrefKey() {
    return this.userPrefKey;
  }

  /**
   * @param userPrefKey set userPrefKey
   */
  public void setUserPrefKey(final String userPrefKey) {
    this.userPrefKey = userPrefKey;
  }

  /**
   * @return userPrefVal
   */
  public String getUserPrefVal() {
    return this.userPrefVal;
  }

  /**
   * @param userPrefVal set userPrefVal
   */
  public void setUserPrefVal(final String userPrefVal) {
    this.userPrefVal = userPrefVal;
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
   * @return version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * @param version set version
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserPreference clone() {
    try {
      return (UserPreference) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      // NOT APPLICABLE
    }
    return new UserPreference();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final UserPreference object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((UserPreference) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }
}
