/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * @author dja7cob
 */
public class UsecaseSectionType extends UsecaseTypeModel {


  private final Set<UseCaseSectionItemType> ucSItemTypeSet = new HashSet<>();

  /**
   * @return the id
   */
  @Override
  public Long getUcId() {
    return this.ucId;
  }


  /**
   * @param id the id to set
   */
  @Override
  public void setUcId(final Long id) {
    this.ucId = id;
  }


  /**
   * @return the ucGroupName
   */
  @Override
  public String getUcGroupName() {
    return this.ucGroupName;
  }


  /**
   * @param ucGroupName the ucGroupName to set
   */
  @Override
  public void setUcGroupName(final String ucGroupName) {
    this.ucGroupName = ucGroupName;
  }


  /**
   * @return the nameEng
   */
  @Override
  public String getNameEng() {
    return this.nameEng;
  }


  /**
   * @param nameEng the nameEng to set
   */
  @Override
  public void setNameEng(final String nameEng) {
    this.nameEng = nameEng;
  }


  /**
   * @return the descEng
   */
  @Override
  public String getDescEng() {
    return this.descEng;
  }


  /**
   * @param descEng the descEng to set
   */
  @Override
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }


  /**
   * @return the nameGer
   */
  @Override
  public String getNameGer() {
    return this.nameGer;
  }


  /**
   * @param nameGer the nameGer to set
   */
  @Override
  public void setNameGer(final String nameGer) {
    this.nameGer = nameGer;
  }


  /**
   * @return the descGer
   */
  @Override
  public String getDescGer() {
    return this.descGer;
  }


  /**
   * @param descGer the descGer to set
   */
  @Override
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }


  /**
   * @return the isDeleted
   */
  @Override
  public boolean isDeleted() {
    return this.isDeleted;
  }


  /**
   * @param isDeleted the isDeleted to set
   */
  @Override
  public void setDeleted(final boolean isDeleted) {
    this.isDeleted = isDeleted;
  }


  /**
   * @return the createdDate
   */
  @Override
  public Calendar getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  @Override
  public void setCreatedDate(final Calendar createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the modifiedDate
   */
  @Override
  public Calendar getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  @Override
  public void setModifiedDate(final Calendar modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }


  /**
   * @param createdUser the createdUser to set
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  /**
   * @return the modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }


  /**
   * @param modifiedUser the modifiedUser to set
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }


  /**
   * @return the changeNumber
   */
  @Override
  public Long getChangeNumber() {
    return this.changeNumber;
  }


  /**
   * @param changeNumber the changeNumber to set
   */
  @Override
  public void setChangeNumber(final Long changeNumber) {
    this.changeNumber = changeNumber;
  }


  /**
   * @return the ucItemTypeSet
   */
  public Set<UseCaseSectionItemType> getUcSItemTypeSet() {
    return this.ucSItemTypeSet;
  }
}
