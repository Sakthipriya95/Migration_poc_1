/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class PidcChangedAttrType {

  private Long attrId;

  private Long oldValueId;

  private Long newValueId;

  private String oldUsed;

  private String newUsed;

  private String oldPartNumber;

  private String newPartNumber;

  private String oldSpecLink;

  private String newSpecLink;

  private String oldDesc;

  private String newDesc;

  private String oldValueIdClearingStatus;

  private String newValueIdClearingStatus;

  private Long changeNumber;

  private String modifyDate;

  private String modifyUser;

  private Long pidcVers;

  private String oldIsVariant;

  private String newIsVariant;

  private String level;

  private Long pidcVersChangeNum;

  private String oldFocusMatrix;

  private String newFocusMatrix;

  private String oldTranferVcdm;

  private String newTranferVcdm;

  private String pidcAction;


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
   * @return the oldValueId
   */
  public Long getOldValueId() {
    return this.oldValueId;
  }


  /**
   * @param oldValueId the oldValueId to set
   */
  public void setOldValueId(final Long oldValueId) {
    this.oldValueId = oldValueId;
  }


  /**
   * @return the newValueId
   */
  public Long getNewValueId() {
    return this.newValueId;
  }


  /**
   * @param newValueId the newValueId to set
   */
  public void setNewValueId(final Long newValueId) {
    this.newValueId = newValueId;
  }


  /**
   * @return the oldUsed
   */
  public String getOldUsed() {
    return this.oldUsed;
  }


  /**
   * @param oldUsed the oldUsed to set
   */
  public void setOldUsed(final String oldUsed) {
    this.oldUsed = oldUsed;
  }


  /**
   * @return the newUsed
   */
  public String getNewUsed() {
    return this.newUsed;
  }


  /**
   * @param newUsed the newUsed to set
   */
  public void setNewUsed(final String newUsed) {
    this.newUsed = newUsed;
  }


  /**
   * @return the oldPartNumber
   */
  public String getOldPartNumber() {
    return this.oldPartNumber;
  }


  /**
   * @param oldPartNumber the oldPartNumber to set
   */
  public void setOldPartNumber(final String oldPartNumber) {
    this.oldPartNumber = oldPartNumber;
  }


  /**
   * @return the newPartNumber
   */
  public String getNewPartNumber() {
    return this.newPartNumber;
  }


  /**
   * @param newPartNumber the newPartNumber to set
   */
  public void setNewPartNumber(final String newPartNumber) {
    this.newPartNumber = newPartNumber;
  }


  /**
   * @return the oldSpecLink
   */
  public String getOldSpecLink() {
    return this.oldSpecLink;
  }


  /**
   * @param oldSpecLink the oldSpecLink to set
   */
  public void setOldSpecLink(final String oldSpecLink) {
    this.oldSpecLink = oldSpecLink;
  }


  /**
   * @return the newSpecLink
   */
  public String getNewSpecLink() {
    return this.newSpecLink;
  }


  /**
   * @param newSpecLink the newSpecLink to set
   */
  public void setNewSpecLink(final String newSpecLink) {
    this.newSpecLink = newSpecLink;
  }


  /**
   * @return the oldDesc
   */
  public String getOldDesc() {
    return this.oldDesc;
  }


  /**
   * @param oldDesc the oldDesc to set
   */
  public void setOldDesc(final String oldDesc) {
    this.oldDesc = oldDesc;
  }


  /**
   * @return the newDesc
   */
  public String getNewDesc() {
    return this.newDesc;
  }


  /**
   * @param newDesc the newDesc to set
   */
  public void setNewDesc(final String newDesc) {
    this.newDesc = newDesc;
  }


  /**
   * @return the oldValueIdClearingStatus
   */
  public String getOldValueIdClearingStatus() {
    return this.oldValueIdClearingStatus;
  }


  /**
   * @param oldValueIdClearingStatus the oldValueIdClearingStatus to set
   */
  public void setOldValueIdClearingStatus(final String oldValueIdClearingStatus) {
    this.oldValueIdClearingStatus = oldValueIdClearingStatus;
  }


  /**
   * @return the newValueIdClearingStatus
   */
  public String getNewValueIdClearingStatus() {
    return this.newValueIdClearingStatus;
  }


  /**
   * @param newValueIdClearingStatus the newValueIdClearingStatus to set
   */
  public void setNewValueIdClearingStatus(final String newValueIdClearingStatus) {
    this.newValueIdClearingStatus = newValueIdClearingStatus;
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
   * @return the pidcVers
   */
  public Long getPidcVers() {
    return this.pidcVers;
  }


  /**
   * @param pidcVers the pidcVers to set
   */
  public void setPidcVers(final Long pidcVers) {
    this.pidcVers = pidcVers;
  }


  /**
   * @return the oldIsVariant
   */
  public String getOldIsVariant() {
    return this.oldIsVariant;
  }


  /**
   * @param oldIsVariant the oldIsVariant to set
   */
  public void setOldIsVariant(final String oldIsVariant) {
    this.oldIsVariant = oldIsVariant;
  }


  /**
   * @return the newIsVariant
   */
  public String getNewIsVariant() {
    return this.newIsVariant;
  }


  /**
   * @param newIsVariant the newIsVariant to set
   */
  public void setNewIsVariant(final String newIsVariant) {
    this.newIsVariant = newIsVariant;
  }


  /**
   * @return the level
   */
  public String getLevel() {
    return this.level;
  }


  /**
   * @param level the level to set
   */
  public void setLevel(final String level) {
    this.level = level;
  }


  /**
   * @return the pidcVersChangeNum
   */
  public Long getPidcVersChangeNum() {
    return this.pidcVersChangeNum;
  }


  /**
   * @param pidcVersChangeNum the pidcVersChangeNum to set
   */
  public void setPidcVersChangeNum(final Long pidcVersChangeNum) {
    this.pidcVersChangeNum = pidcVersChangeNum;
  }


  /**
   * @return the oldFocusMatrix
   */
  public String getOldFocusMatrix() {
    return this.oldFocusMatrix;
  }


  /**
   * @param oldFocusMatrix the oldFocusMatrix to set
   */
  public void setOldFocusMatrix(final String oldFocusMatrix) {
    this.oldFocusMatrix = oldFocusMatrix;
  }


  /**
   * @return the newFocusMatrix
   */
  public String getNewFocusMatrix() {
    return this.newFocusMatrix;
  }


  /**
   * @param newFocusMatrix the newFocusMatrix to set
   */
  public void setNewFocusMatrix(final String newFocusMatrix) {
    this.newFocusMatrix = newFocusMatrix;
  }


  /**
   * @return the oldTranferVcdm
   */
  public String getOldTranferVcdm() {
    return this.oldTranferVcdm;
  }


  /**
   * @param oldTranferVcdm the oldTranferVcdm to set
   */
  public void setOldTranferVcdm(final String oldTranferVcdm) {
    this.oldTranferVcdm = oldTranferVcdm;
  }


  /**
   * @return the newTranferVcdm
   */
  public String getNewTranferVcdm() {
    return this.newTranferVcdm;
  }


  /**
   * @param newTranferVcdm the newTranferVcdm to set
   */
  public void setNewTranferVcdm(final String newTranferVcdm) {
    this.newTranferVcdm = newTranferVcdm;
  }


  /**
   * @return the pidcAction
   */
  public String getPidcAction() {
    return this.pidcAction;
  }


  /**
   * @param pidcAction the pidcAction to set
   */
  public void setPidcAction(final String pidcAction) {
    this.pidcAction = pidcAction;
  }

  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }

    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getAttrId(), ((PidcChangedAttrType) obj).getAttrId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getAttrId());
  }

}
