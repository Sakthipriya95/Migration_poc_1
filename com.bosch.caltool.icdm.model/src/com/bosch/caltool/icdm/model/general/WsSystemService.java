/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

import java.util.Date;

import com.bosch.caltool.datamodel.core.IModel;


/**
 * @author bne4cob
 */
public class WsSystemService implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = -4940795220693580715L;

  private Long id;

  private Long systemId;
  private Long serviceId;

  private Date createdDate;
  private String createdUser;
  private Date modifiedDate;
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
   * @return the systemId
   */
  public Long getSystemId() {
    return this.systemId;
  }


  /**
   * @param systemId the systemId to set
   */
  public void setSystemId(final Long systemId) {
    this.systemId = systemId;
  }


  /**
   * @return the serviceId
   */
  public Long getServiceId() {
    return this.serviceId;
  }


  /**
   * @param serviceId the serviceId to set
   */
  public void setServiceId(final Long serviceId) {
    this.serviceId = serviceId;
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
  public Date getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Date modifiedDate) {
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
