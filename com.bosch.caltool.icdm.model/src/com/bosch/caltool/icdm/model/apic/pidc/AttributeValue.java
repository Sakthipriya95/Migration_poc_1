/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class AttributeValue {

  private Long valueId;

  private Long attrId;

  private String valueEng;

  private String valueGer;

  private boolean isDeleted;

  private String createdDate;

  private String createdUser;

  private String modifyDate;

  private String modifyUser;

  private Long changeNumber;

  private String clearingStatus;

  private boolean isCleared;

  private String valueDescEng;

  private String valueDescGer;


  /**
   * @return the valueId
   */
  public Long getValueId() {
    return this.valueId;
  }


  /**
   * @param valueId the valueId to set
   */
  public void setValueId(final Long valueId) {
    this.valueId = valueId;
  }


  /**
   * @return the attrId
   */
  public Long getAttrId() {
    return this.attrId;
  }


  /**
   * @param attrId the attrId to set
   */
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }


  /**
   * @return the valueEng
   */
  public String getValueEng() {
    return this.valueEng;
  }


  /**
   * @param valueEng the valueEng to set
   */
  public void setValueEng(final String valueEng) {
    this.valueEng = valueEng;
  }


  /**
   * @return the valueGer
   */
  public String getValueGer() {
    return this.valueGer;
  }


  /**
   * @param valueGer the valueGer to set
   */
  public void setValueGer(final String valueGer) {
    this.valueGer = valueGer;
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


  /**
   * @return the valueDescEng
   */
  public String getValueDescEng() {
    return this.valueDescEng;
  }


  /**
   * @param valueDescEng the valueDescEng to set
   */
  public void setValueDescEng(final String valueDescEng) {
    this.valueDescEng = valueDescEng;
  }


  /**
   * @return the valueDescGer
   */
  public String getValueDescGer() {
    return this.valueDescGer;
  }


  /**
   * @param valueDescGer the valueDescGer to set
   */
  public void setValueDescGer(final String valueDescGer) {
    this.valueDescGer = valueDescGer;
  }


}
