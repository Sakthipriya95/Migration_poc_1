/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author msp5cob
 */
public class UserLoginInfo implements Comparable<UserLoginInfo>, IModel {


  /**
   *
   */
  private static final long serialVersionUID = 7304835122391639417L;

  private Long id;

  private String userNtId;

  private Long azureLoginCount;

  private Long ldapLoginCount;

  private String createdDate;

  private String createdUser;

  private String modifiedDate;

  private String modifiedUser;

  private Long version;


  /**
   * @return the userNtId
   */
  public String getUserNtId() {
    return this.userNtId;
  }


  /**
   * @param userNtId the userNtId to set
   */
  public void setUserNtId(final String userNtId) {
    this.userNtId = userNtId;
  }


  /**
   * @return the azureLoginCount
   */
  public Long getAzureLoginCount() {
    return this.azureLoginCount;
  }


  /**
   * @param azureLoginCount the azureLoginCount to set
   */
  public void setAzureLoginCount(final Long azureLoginCount) {
    this.azureLoginCount = azureLoginCount;
  }


  /**
   * @return the ldapLoginCount
   */
  public Long getLdapLoginCount() {
    return this.ldapLoginCount;
  }


  /**
   * @param ldapLoginCount the ldapLoginCount to set
   */
  public void setLdapLoginCount(final Long ldapLoginCount) {
    this.ldapLoginCount = ldapLoginCount;
  }


  /**
   * @return the createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
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
    return ModelUtil.isEqual(getId(), ((UserLoginInfo) obj).getId());
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
  public int compareTo(final UserLoginInfo o) {
    return ModelUtil.compare(getId(), o.getId());
  }

}
