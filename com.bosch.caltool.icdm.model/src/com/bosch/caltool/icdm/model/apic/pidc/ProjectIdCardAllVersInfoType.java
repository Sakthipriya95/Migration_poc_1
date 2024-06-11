/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.List;

/**
 * @author dmr1cob
 */
public class ProjectIdCardAllVersInfoType {

  private long id;

  private String name;

  private long versionNumber;

  private boolean isDeleted;

  private String createUser;

  private String modifyUser;

  private String createDate;

  private String modifyDate;

  private long changeNumber;

  private List<LevelAttrInfo> levelAttrInfoList;

  private String clearingStatus;

  private List<PidcVersionType> pidcVersionTypeList;

  private boolean isCleared;

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
   * @return the createUser
   */
  public String getCreateUser() {
    return this.createUser;
  }


  /**
   * @param createUser the createUser to set
   */
  public void setCreateUser(final String createUser) {
    this.createUser = createUser;
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
   * @return the createDate
   */
  public String getCreateDate() {
    return this.createDate;
  }


  /**
   * @param createDate the createDate to set
   */
  public void setCreateDate(final String createDate) {
    this.createDate = createDate;
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


  /**
   * @return the levelAttrInfoList
   */
  public List<LevelAttrInfo> getLevelAttrInfoList() {
    return this.levelAttrInfoList;
  }


  /**
   * @param levelAttrInfoList the levelAttrInfoList to set
   */
  public void setLevelAttrInfoList(final List<LevelAttrInfo> levelAttrInfoList) {
    this.levelAttrInfoList = levelAttrInfoList;
  }


  /**
   * @return the clearingStatus
   */
  public String getClearingStatus() {
    return this.clearingStatus;
  }


  /**
   * @param clearingStatus the clearingStatus to set
   */
  public void setClearingStatus(final String clearingStatus) {
    this.clearingStatus = clearingStatus;
  }


  /**
   * @return the pidcVersionTypeList
   */
  public List<PidcVersionType> getPidcVersionTypeList() {
    return this.pidcVersionTypeList;
  }


  /**
   * @param pidcVersionTypeList the pidcVersionTypeList to set
   */
  public void setPidcVersionTypeList(final List<PidcVersionType> pidcVersionTypeList) {
    this.pidcVersionTypeList = pidcVersionTypeList;
  }


  /**
   * @return the isCleared
   */
  public boolean isCleared() {
    return this.isCleared;
  }


  /**
   * @param isCleared the isCleared to set
   */
  public void setCleared(final boolean isCleared) {
    this.isCleared = isCleared;
  }


}
