/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

import com.bosch.caltool.datamodel.core.IModel;


/**
 * @author bne4cob
 */
public class ApicAccessRight implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = 8446004420716670311L;
  private Long id;
  private Long userId;

  private String accessRight;
  private String moduleName;

  private boolean apicRead = false;
  private boolean apicWrite = false;
  private boolean pidcWrite = false;
  private boolean apicReadAll = false;

  private Long version;

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
   * @return the accessRight
   */
  public String getAccessRight() {
    return this.accessRight;
  }


  /**
   * @param accessRight the accessRight to set
   */
  public void setAccessRight(final String accessRight) {
    this.accessRight = accessRight;
  }


  /**
   * @return the moduleName
   */
  public String getModuleName() {
    return this.moduleName;
  }


  /**
   * @param moduleName the moduleName to set
   */
  public void setModuleName(final String moduleName) {
    this.moduleName = moduleName;
  }


  /**
   * @return the apicRead
   */
  public boolean isApicRead() {
    return this.apicRead;
  }


  /**
   * @param apicRead the apicRead to set
   */
  public void setApicRead(final boolean apicRead) {
    this.apicRead = apicRead;
  }


  /**
   * @return the apicWrite
   */
  public boolean isApicWrite() {
    return this.apicWrite;
  }


  /**
   * @param apicWrite the apicWrite to set
   */
  public void setApicWrite(final boolean apicWrite) {
    this.apicWrite = apicWrite;
  }


  /**
   * @return the pidcWrite
   */
  public boolean isPidcWrite() {
    return this.pidcWrite;
  }


  /**
   * @param pidcWrite the pidcWrite to set
   */
  public void setPidcWrite(final boolean pidcWrite) {
    this.pidcWrite = pidcWrite;
  }


  /**
   * @return the apicReadAll
   */
  public boolean isApicReadAll() {
    return this.apicReadAll;
  }


  /**
   * @param apicReadAll the apicReadAll to set
   */
  public void setApicReadAll(final boolean apicReadAll) {
    this.apicReadAll = apicReadAll;
  }

}
