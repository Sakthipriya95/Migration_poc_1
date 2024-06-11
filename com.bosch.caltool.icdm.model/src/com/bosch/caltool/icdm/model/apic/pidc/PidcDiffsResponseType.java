/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmr1cob
 */
public class PidcDiffsResponseType {

  private Long pidcId;

  private Long oldChangeNumber;

  private Long newChangeNumber;

  private Long oldPidcVersionNumber;

  private Long newPidcVersionNumber;

  private String oldPidcStatus;

  private String newPidcStatus;

  private String oldIsDeleted;

  private String newIsDeleted;

  private String modifiedDate;

  private String modifiedUser;

  private Long pidcVersion;

  private List<PidcChangedAttrType> pidcChangedAttrTypeList = new ArrayList<>();

  private List<PidcChangedVariantType> pidcChangedVariantTypeList = new ArrayList<>();

  private List<PidcFocusMatrixVersType> pidcFocusMatrixVersTypeList = new ArrayList<>();

  private List<PidcFocusMatrixType> pidcFocusMatrixTypeList = new ArrayList<>();


  /**
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }


  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }


  /**
   * @return the oldChangeNumber
   */
  public Long getOldChangeNumber() {
    return this.oldChangeNumber;
  }


  /**
   * @param oldChangeNumber the oldChangeNumber to set
   */
  public void setOldChangeNumber(final Long oldChangeNumber) {
    this.oldChangeNumber = oldChangeNumber;
  }


  /**
   * @return the newChangeNumber
   */
  public Long getNewChangeNumber() {
    return this.newChangeNumber;
  }


  /**
   * @param newChangeNumber the newChangeNumber to set
   */
  public void setNewChangeNumber(final Long newChangeNumber) {
    this.newChangeNumber = newChangeNumber;
  }


  /**
   * @return the oldPidcVersionNumber
   */
  public Long getOldPidcVersionNumber() {
    return this.oldPidcVersionNumber;
  }


  /**
   * @param oldPidcVersionNumber the oldPidcVersionNumber to set
   */
  public void setOldPidcVersionNumber(final Long oldPidcVersionNumber) {
    this.oldPidcVersionNumber = oldPidcVersionNumber;
  }


  /**
   * @return the newPidcVersionNumber
   */
  public Long getNewPidcVersionNumber() {
    return this.newPidcVersionNumber;
  }


  /**
   * @param newPidcVersionNumber the newPidcVersionNumber to set
   */
  public void setNewPidcVersionNumber(final Long newPidcVersionNumber) {
    this.newPidcVersionNumber = newPidcVersionNumber;
  }


  /**
   * @return the oldPidcStatus
   */
  public String getOldPidcStatus() {
    return this.oldPidcStatus;
  }


  /**
   * @param oldPidcStatus the oldPidcStatus to set
   */
  public void setOldPidcStatus(final String oldPidcStatus) {
    this.oldPidcStatus = oldPidcStatus;
  }


  /**
   * @return the newPidcStatus
   */
  public String getNewPidcStatus() {
    return this.newPidcStatus;
  }


  /**
   * @param newPidcStatus the newPidcStatus to set
   */
  public void setNewPidcStatus(final String newPidcStatus) {
    this.newPidcStatus = newPidcStatus;
  }


  /**
   * @return the oldIsDeleted
   */
  public String getOldIsDeleted() {
    return this.oldIsDeleted;
  }


  /**
   * @param oldIsDeleted the oldIsDeleted to set
   */
  public void setOldIsDeleted(final String oldIsDeleted) {
    this.oldIsDeleted = oldIsDeleted;
  }


  /**
   * @return the newIsDeleted
   */
  public String getNewIsDeleted() {
    return this.newIsDeleted;
  }


  /**
   * @param newIsDeleted the newIsDeleted to set
   */
  public void setNewIsDeleted(final String newIsDeleted) {
    this.newIsDeleted = newIsDeleted;
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
   * @return the pidcVersion
   */
  public Long getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final Long pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the pidcChangedAttrTypeList
   */
  public List<PidcChangedAttrType> getPidcChangedAttrTypeList() {
    return this.pidcChangedAttrTypeList;
  }


  /**
   * @param pidcChangedAttrTypeList the pidcChangedAttrTypeList to set
   */
  public void setPidcChangedAttrTypeList(final List<PidcChangedAttrType> pidcChangedAttrTypeList) {
    this.pidcChangedAttrTypeList = pidcChangedAttrTypeList;
  }


  /**
   * @return the pidcChangedVariantTypeList
   */
  public List<PidcChangedVariantType> getPidcChangedVariantTypeList() {
    return this.pidcChangedVariantTypeList;
  }


  /**
   * @param pidcChangedVariantTypeList the pidcChangedVariantTypeList to set
   */
  public void setPidcChangedVariantTypeList(final List<PidcChangedVariantType> pidcChangedVariantTypeList) {
    this.pidcChangedVariantTypeList = pidcChangedVariantTypeList;
  }


  /**
   * @return the pidcFocusMatrixVersTypeList
   */
  public List<PidcFocusMatrixVersType> getPidcFocusMatrixVersTypeList() {
    return this.pidcFocusMatrixVersTypeList;
  }


  /**
   * @param pidcFocusMatrixVersTypeList the pidcFocusMatrixVersTypeList to set
   */
  public void setPidcFocusMatrixVersTypeList(final List<PidcFocusMatrixVersType> pidcFocusMatrixVersTypeList) {
    this.pidcFocusMatrixVersTypeList = pidcFocusMatrixVersTypeList;
  }


  /**
   * @return the pidcFocusMatrixTypeList
   */
  public List<PidcFocusMatrixType> getPidcFocusMatrixTypeList() {
    return this.pidcFocusMatrixTypeList;
  }


  /**
   * @param pidcFocusMatrixTypeList the pidcFocusMatrixTypeList to set
   */
  public void setPidcFocusMatrixTypeList(final List<PidcFocusMatrixType> pidcFocusMatrixTypeList) {
    this.pidcFocusMatrixTypeList = pidcFocusMatrixTypeList;
  }


}
