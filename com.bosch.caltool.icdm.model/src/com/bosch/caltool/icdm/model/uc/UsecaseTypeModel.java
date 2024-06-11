/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.Calendar;

/**
 * @author dja7cob
 */
public class UsecaseTypeModel {

  /**
   *
   */
  protected Long ucId;

  /**
   *
   */
  protected String ucGroupName;

  /**
   *
   */
  protected String nameEng;

  /**
   *
   */
  protected String descEng;

  /**
   *
   */
  protected String nameGer;

  /**
   *
   */
  protected String descGer;

  /**
   *
   */
  protected boolean isDeleted;

  /**
   *
   */
  protected Calendar createdDate;

  /**
   *
   */
  protected Calendar modifiedDate;

  /**
   *
   */
  protected String createdUser;

  /**
   *
   */
  protected String modifiedUser;

  /**
   *
   */
  protected Long changeNumber;


  /**
   * @return the ucId
   */
  public Long getUcId() {
    return this.ucId;
  }


  /**
   * @param ucId the ucId to set
   */
  public void setUcId(final Long ucId) {
    this.ucId = ucId;
  }


  /**
   * @return the ucGroupName
   */
  public String getUcGroupName() {
    return this.ucGroupName;
  }


  /**
   * @param ucGroupName the ucGroupName to set
   */
  public void setUcGroupName(final String ucGroupName) {
    this.ucGroupName = ucGroupName;
  }


  /**
   * @return the nameEng
   */
  public String getNameEng() {
    return this.nameEng;
  }


  /**
   * @param nameEng the nameEng to set
   */
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  /**
   * @return the descEng
   */
  public String getDescEng() {
    return this.descEng;
  }


  /**
   * @param descEng the descEng to set
   */
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }


  /**
   * @return the nameGer
   */
  public String getNameGer() {
    return this.nameGer;
  }


  /**
   * @param nameGer the nameGer to set
   */
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }


  /**
   * @return the descGer
   */
  public String getDescGer() {
    return this.descGer;
  }


  /**
   * @param descGer the descGer to set
   */
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
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
   * @return the createdDate
   */
  public Calendar getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Calendar createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the modifiedDate
   */
  public Calendar getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Calendar modifiedDate) {
    this.modifiedDate = modifiedDate;
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
   * @return the changeNumber
   */
  public Long getChangeNumber() {
    return this.changeNumber;
  }


  /**
   * @param changeNumber the changeNumber to set
   */
  public void setChangeNumber(final Long changeNumber) {
    this.changeNumber = changeNumber;
  }

}
