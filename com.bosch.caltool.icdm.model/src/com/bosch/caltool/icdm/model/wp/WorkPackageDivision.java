/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.wp;

import com.bosch.caltool.datamodel.core.IDataObject;

/**
 * @author bne4cob
 */
public class WorkPackageDivision implements IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = -1828561241374590667L;

  private Long id;
  private String name;
  private String description;
  private Long wpId;
  private Long divAttrValId;
  private String divName;
  private String wpName;
  private String wpDesc;
  private String wpGroup;
  private String wpResource;
  private Long wpResId;
  private String wpIdMcr;
  private Long contactPersonId;
  private String cntctPersonDispNm;
  private Long contactPersonSecondId;
  private String cntctPersonSecDispNm;
  private String createdDate;
  private String createdUser;
  private String modifiedDate;
  private String modifiedUser;
  private Long version;
  private String deleted;
  private String wpdComment;
  private String iccRelevantFlag;
  private String crpObdRelevantFlag;
  private String crpObdComment;
  private String crpEmissionRelevantFlag;
  private String crpEmissionComment;
  private String crpSoundRelevantFlag;
  private String crpSoundComment;


  /**
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the divAttrValId
   */
  public Long getDivAttrValId() {
    return this.divAttrValId;
  }


  /**
   * @param divAttrValId the divAttrValId to set
   */
  public void setDivAttrValId(final Long divAttrValId) {
    this.divAttrValId = divAttrValId;
  }


  /**
   * @return the divName
   */
  public String getDivName() {
    return this.divName;
  }


  /**
   * @param divName the divName to set
   */
  public void setDivName(final String divName) {
    this.divName = divName;
  }


  /**
   * @return the wpResId
   */
  public Long getWpResId() {
    return this.wpResId;
  }


  /**
   * @param wpResId the wpResId to set
   */
  public void setWpResId(final Long wpResId) {
    this.wpResId = wpResId;
  }


  /**
   * @return the createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
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
   * @return the modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
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
   * @return the wpId
   */
  public Long getWpId() {
    return this.wpId;
  }


  /**
   * @param wpId the wpId to set
   */
  public void setWpId(final Long wpId) {
    this.wpId = wpId;
  }


  /**
   * @return the wpGroup
   */
  public String getWpGroup() {
    return this.wpGroup;
  }


  /**
   * @param wpGroup the wpGroup to set
   */
  public void setWpGroup(final String wpGroup) {
    this.wpGroup = wpGroup;
  }

  /**
   * @return the wpDivId
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * @param wpDivId the wpDivId to set
   */
  @Override
  public void setId(final Long wpDivId) {
    this.id = wpDivId;
  }

  /**
   * @return the wpName
   */
  public String getWpName() {
    return this.wpName;
  }

  /**
   * @param wpName the wpName to set
   */
  public void setWpName(final String wpName) {
    this.wpName = wpName;
  }

  /**
   * @return the wpDesc
   */
  public String getWpDesc() {
    return this.wpDesc;
  }

  /**
   * @param wpDesc the wpDesc to set
   */
  public void setWpDesc(final String wpDesc) {
    this.wpDesc = wpDesc;
  }

  /**
   * @return the wpResource
   */
  public String getWpResource() {
    return this.wpResource;
  }

  /**
   * @param wpResource the wpResource to set
   */
  public void setWpResource(final String wpResource) {
    this.wpResource = wpResource;
  }

  /**
   * @return the wpIdMcr
   */
  public String getWpIdMcr() {
    return this.wpIdMcr;
  }

  /**
   * @param wpIdMcr the wpIdMcr to set
   */
  public void setWpIdMcr(final String wpIdMcr) {
    this.wpIdMcr = wpIdMcr;
  }

  /**
   * @return the contactPersonId
   */
  public Long getContactPersonId() {
    return this.contactPersonId;
  }


  /**
   * @return the cntctPersonDispNm
   */
  public synchronized String getCntctPersonDispNm() {
    return this.cntctPersonDispNm;
  }


  /**
   * @param cntctPersonDispNm the cntctPersonDispNm to set
   */
  public synchronized void setCntctPersonDispNm(final String cntctPersonDispNm) {
    this.cntctPersonDispNm = cntctPersonDispNm;
  }


  /**
   * @return the cntctPersonSecDispNm
   */
  public synchronized String getCntctPersonSecDispNm() {
    return this.cntctPersonSecDispNm;
  }


  /**
   * @param cntctPersonSecDispNm the cntctPersonSecDispNm to set
   */
  public synchronized void setCntctPersonSecDispNm(final String cntctPersonSecDispNm) {
    this.cntctPersonSecDispNm = cntctPersonSecDispNm;
  }


  /**
   * @param contactPersonId the contactPersonId to set
   */
  public void setContactPersonId(final Long contactPersonId) {
    this.contactPersonId = contactPersonId;
  }

  /**
   * @return the contactPersonSecondId
   */
  public Long getContactPersonSecondId() {
    return this.contactPersonSecondId;
  }

  /**
   * @param contactPersonSecondId the contactPersonSecondId to set
   */
  public void setContactPersonSecondId(final Long contactPersonSecondId) {
    this.contactPersonSecondId = contactPersonSecondId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "WorkPackageDetails [wpDivId=" + this.id + ", wpName=" + this.wpName + ", wpDesc=" + this.wpDesc +
        ", wpResource=" + this.wpResource + ", wpIdMcr=" + this.wpIdMcr + ", contactPersonId=" + this.contactPersonId +
        ", contactPersonSecondId=" + this.contactPersonSecondId + "]";
  }


  /**
   * @return the deleted
   */
  public String getDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final String deleted) {
    this.deleted = deleted;
  }


  /**
   * @return the wpdComment
   */
  public String getWpdComment() {
    return this.wpdComment;
  }

  /**
   * @param wpdComment the wpdComment to set
   */
  public void setWpdComment(final String wpdComment) {
    this.wpdComment = wpdComment;
  }


  /**
   * @return the iccRelevantFlag
   */
  public String getIccRelevantFlag() {
    return this.iccRelevantFlag;
  }


  /**
   * @param iccRelevantFlag the iccRelevantFlag to set
   */
  public void setIccRelevantFlag(final String iccRelevantFlag) {
    this.iccRelevantFlag = iccRelevantFlag;
  }


  /**
   * @return the crpObdRelevantFlag
   */
  public String getCrpObdRelevantFlag() {
    return this.crpObdRelevantFlag;
  }


  /**
   * @param crpObdRelevantFlag the crpObdRelevantFlag to set
   */
  public void setCrpObdRelevantFlag(final String crpObdRelevantFlag) {
    this.crpObdRelevantFlag = crpObdRelevantFlag;
  }


  /**
   * @return the crpObdComment
   */
  public String getCrpObdComment() {
    return this.crpObdComment;
  }


  /**
   * @param crpObdComment the crpObdComment to set
   */
  public void setCrpObdComment(final String crpObdComment) {
    this.crpObdComment = crpObdComment;
  }


  /**
   * @return the crpEmissionRelevantFlag
   */
  public String getCrpEmissionRelevantFlag() {
    return this.crpEmissionRelevantFlag;
  }


  /**
   * @param crpEmissionRelevantFlag the crpEmissionRelevantFlag to set
   */
  public void setCrpEmissionRelevantFlag(final String crpEmissionRelevantFlag) {
    this.crpEmissionRelevantFlag = crpEmissionRelevantFlag;
  }


  /**
   * @return the crpEmissionComment
   */
  public String getCrpEmissionComment() {
    return this.crpEmissionComment;
  }


  /**
   * @param crpEmissionComment the crpEmissionComment to set
   */
  public void setCrpEmissionComment(final String crpEmissionComment) {
    this.crpEmissionComment = crpEmissionComment;
  }


  /**
   * @return the crpSoundRelevantFlag
   */
  public String getCrpSoundRelevantFlag() {
    return this.crpSoundRelevantFlag;
  }


  /**
   * @param crpSoundRelevantFlag the crpSoundRelevantFlag to set
   */
  public void setCrpSoundRelevantFlag(final String crpSoundRelevantFlag) {
    this.crpSoundRelevantFlag = crpSoundRelevantFlag;
  }


  /**
   * @return the crpSoundComment
   */
  public String getCrpSoundComment() {
    return this.crpSoundComment;
  }


  /**
   * @param crpSoundComment the crpSoundComment to set
   */
  public void setCrpSoundComment(final String crpSoundComment) {
    this.crpSoundComment = crpSoundComment;
  }

}
