/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import com.bosch.caltool.datamodel.core.IModel;


/**
 * @author bne4cob
 */
public class WsSystem implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = -4940795220693580715L;

  private Long id;

  private String systemType;
  private String systemToken;

  private String servAccessType;

  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;

  private Long version;

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
   * @return the systemType
   */
  public String getSystemType() {
    return this.systemType;
  }


  /**
   * @param systemType the systemType to set
   */
  public void setSystemType(final String systemType) {
    this.systemType = systemType;
  }


  /**
   * @return the systemToken
   */
  public String getSystemToken() {
    return this.systemToken;
  }


  /**
   * @param systemToken the systemToken to set
   */
  public void setSystemToken(final String systemToken) {
    this.systemToken = systemToken;
  }


  /**
   * @return the servAccessType
   */
  public String getServAccessType() {
    return this.servAccessType;
  }


  /**
   * @param servAccessType the servAccessType to set
   */
  public void setServAccessType(final String servAccessType) {
    this.servAccessType = servAccessType;
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


}
