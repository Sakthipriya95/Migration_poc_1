/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author NIP4COB
 */
public class ProjectIdCardVariantInfoType {

  private long id;
  private long pidcId;
  private String name;
  private long versionNumber;
  private boolean isDeleted;
  private String createdUser;
  private String createdDate;
  private String modifyUser;
  private String modifyDate;
  private long changeNumber;

  /**
   * @return the id
   */
  public long getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(final long id) {
    this.id = id;
  }

  /**
   * @return the pidcId
   */
  public long getPidcId() {
    return this.pidcId;
  }

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final long pidcId) {
    this.pidcId = pidcId;
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
   * @return the versionNumber
   */
  public long getVersionNumber() {
    return this.versionNumber;
  }

  /**
   * @param versionNumber the versionNumber to set
   */
  public void setVersionNumber(final long versionNumber) {
    this.versionNumber = versionNumber;
  }

  /**
   * @return the isDeleted
   */
  public boolean isDeleted() {
    return this.isDeleted;
  }

  /**
   * @param isDeleted the isDeleted to set
   */
  public void setDeleted(final boolean isDeleted) {
    this.isDeleted = isDeleted;
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
   * @return the modifyUser
   */
  public String getModifyUser() {
    return this.modifyUser;
  }

  /**
   * @param modifyUser the modifyUser to set
   */
  public void setModifyUser(final String modifyUser) {
    this.modifyUser = modifyUser;
  }

  /**
   * @return the modifyDate
   */
  public String getModifyDate() {
    return this.modifyDate;
  }

  /**
   * @param modifyDate the modifyDate to set
   */
  public void setModifyDate(final String modifyDate) {
    this.modifyDate = modifyDate;
  }

  /**
   * @return the changeNumber
   */
  public long getChangeNumber() {
    return this.changeNumber;
  }

  /**
   * @param changeNumber the changeNumber to set
   */
  public void setChangeNumber(final long changeNumber) {
    this.changeNumber = changeNumber;
  }
}
