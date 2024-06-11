/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class PidcVariantInfoTypeV2 {

  private long id;

  private long pidcId;

  private String name;

  private long versionNumber;

  private boolean isDeleted;

  private String createUser;

  private String createDate;

  private String modifyUser;

  private String modifyDate;

  private long changeNumber;


  /**
   * @return the id
   */
  public long getId() {
    return id;
  }


  /**
   * @param id the id to set
   */
  public void setId(long id) {
    this.id = id;
  }


  /**
   * @return the pidcId
   */
  public long getPidcId() {
    return pidcId;
  }


  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(long pidcId) {
    this.pidcId = pidcId;
  }


  /**
   * @return the name
   */
  public String getName() {
    return name;
  }


  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }


  /**
   * @return the versionNumber
   */
  public long getVersionNumber() {
    return versionNumber;
  }


  /**
   * @param versionNumber the versionNumber to set
   */
  public void setVersionNumber(long versionNumber) {
    this.versionNumber = versionNumber;
  }


  /**
   * @return the isDeleted
   */
  public boolean isDeleted() {
    return isDeleted;
  }


  /**
   * @param isDeleted the isDeleted to set
   */
  public void setDeleted(boolean isDeleted) {
    this.isDeleted = isDeleted;
  }


  /**
   * @return the createUser
   */
  public String getCreateUser() {
    return createUser;
  }


  /**
   * @param createUser the createUser to set
   */
  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }


  /**
   * @return the createDate
   */
  public String getCreateDate() {
    return createDate;
  }


  /**
   * @param createDate the createDate to set
   */
  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }


  /**
   * @return the modifyUser
   */
  public String getModifyUser() {
    return modifyUser;
  }


  /**
   * @param modifyUser the modifyUser to set
   */
  public void setModifyUser(String modifyUser) {
    this.modifyUser = modifyUser;
  }


  /**
   * @return the modifyDate
   */
  public String getModifyDate() {
    return modifyDate;
  }


  /**
   * @param modifyDate the modifyDate to set
   */
  public void setModifyDate(String modifyDate) {
    this.modifyDate = modifyDate;
  }


  /**
   * @return the changeNumber
   */
  public long getChangeNumber() {
    return changeNumber;
  }


  /**
   * @param changeNumber the changeNumber to set
   */
  public void setChangeNumber(long changeNumber) {
    this.changeNumber = changeNumber;
  }

}
