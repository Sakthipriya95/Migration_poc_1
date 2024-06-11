/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class Attribute {

  private Long id;

  private String nameEng;

  private String nameGer;

  private String descEng;

  private String descGer;

  private String unit;

  private String format;

  private boolean isNormalized;

  private boolean isDeleted;

  private boolean isMandatory;

  private int attrLevel;

  private Long groupId;

  private Long typeId;

  private String createDate;

  private String modifyDate;

  private String createUser;

  private String modifyUser;

  private Long changeNumber;


  /**
   * @return the id
   */
  public Long getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.id = id;
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
   * @return the unit
   */
  public String getUnit() {
    return this.unit;
  }


  /**
   * @param unit the unit to set
   */
  public void setUnit(final String unit) {
    this.unit = unit;
  }


  /**
   * @return the format
   */
  public String getFormat() {
    return this.format;
  }


  /**
   * @param format the format to set
   */
  public void setFormat(final String format) {
    this.format = format;
  }


  /**
   * @return the isNormalized
   */
  public boolean isNormalized() {
    return this.isNormalized;
  }


  /**
   * @param isNormalized the isNormalized to set
   */
  public void setNormalized(final boolean isNormalized) {
    this.isNormalized = isNormalized;
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
   * @return the isMandatory
   */
  public boolean isMandatory() {
    return this.isMandatory;
  }


  /**
   * @param isMandatory the isMandatory to set
   */
  public void setMandatory(final boolean isMandatory) {
    this.isMandatory = isMandatory;
  }


  /**
   * @return the attrLevel
   */
  public int getAttrLevel() {
    return this.attrLevel;
  }


  /**
   * @param attrLevel the attrLevel to set
   */
  public void setAttrLevel(final int attrLevel) {
    this.attrLevel = attrLevel;
  }


  /**
   * @return the groupId
   */
  public Long getGroupId() {
    return this.groupId;
  }


  /**
   * @param groupId the groupId to set
   */
  public void setGroupId(final Long groupId) {
    this.groupId = groupId;
  }


  /**
   * @return the typeId
   */
  public Long getTypeId() {
    return this.typeId;
  }


  /**
   * @param typeId the typeId to set
   */
  public void setTypeId(final Long typeId) {
    this.typeId = typeId;
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
