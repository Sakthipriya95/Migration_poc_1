/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

/**
 * @author dmr1cob
 */
public class AlternateAttr {

  private Long attrPk;

  private Long alternateAttrId;

  private Long attrId;

  private String createdDate;

  private String createdUser;

  private String modifiedDate;

  private String modifiedUser;

  private Long version;


  /**
   * @return the attrPk
   */
  public Long getAttrPk() {
    return this.attrPk;
  }


  /**
   * @param attrPk the attrPk to set
   */
  public void setAttrPk(final Long attrPk) {
    this.attrPk = attrPk;
  }


  /**
   * @return the alternateAttrId
   */
  public Long getAlternateAttrId() {
    return this.alternateAttrId;
  }


  /**
   * @param alternateAttrId the alternateAttrId to set
   */
  public void setAlternateAttrId(final Long alternateAttrId) {
    this.alternateAttrId = alternateAttrId;
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
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  public void setVersion(final Long version) {
    this.version = version;
  }


}
