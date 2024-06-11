/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmr1cob
 */
public class PidcChangedSubVarType {

  private Long subVariantId;

  private Long oldValueId;

  private Long newValueId;

  private Long oldChangeNumber;

  private Long newChangeNumber;

  private String oldIsdeleted;

  private String newIsDeleted;

  private String modifiedDate;

  private String modifiedUser;

  private Long pidcVersion;

  private Long pidcVersChangeNumber;

  private String oldTextValueEng;

  private String newTextValueEng;

  private String oldTextValueGer;

  private String newTextValueGer;

  private List<PidcChangedAttrType> changedAttrList = new ArrayList<>();


  /**
   * @return the subVariantId
   */
  public Long getSubVariantId() {
    return this.subVariantId;
  }


  /**
   * @param subVariantId the subVariantId to set
   */
  public void setSubVariantId(final Long subVariantId) {
    this.subVariantId = subVariantId;
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
   * @return the oldIsdeleted
   */
  public String getOldIsdeleted() {
    return this.oldIsdeleted;
  }


  /**
   * @param oldIsdeleted the oldIsdeleted to set
   */
  public void setOldIsdeleted(final String oldIsdeleted) {
    this.oldIsdeleted = oldIsdeleted;
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
   * @return the pidcVersChangeNumber
   */
  public Long getPidcVersChangeNumber() {
    return this.pidcVersChangeNumber;
  }


  /**
   * @param pidcVersChangeNumber the pidcVersChangeNumber to set
   */
  public void setPidcVersChangeNumber(final Long pidcVersChangeNumber) {
    this.pidcVersChangeNumber = pidcVersChangeNumber;
  }


  /**
   * @return the changedAttrList
   */
  public List<PidcChangedAttrType> getChangedAttrList() {
    return this.changedAttrList;
  }


  /**
   * @param changedAttrList the changedAttrList to set
   */
  public void setChangedAttrList(final List<PidcChangedAttrType> changedAttrList) {
    this.changedAttrList = changedAttrList;
  }


  /**
   * @return the oldTextValueEng
   */
  public String getOldTextValueEng() {
    return this.oldTextValueEng;
  }


  /**
   * @param oldTextValueEng the oldTextValueEng to set
   */
  public void setOldTextValueEng(final String oldTextValueEng) {
    this.oldTextValueEng = oldTextValueEng;
  }


  /**
   * @return the newTextValueEng
   */
  public String getNewTextValueEng() {
    return this.newTextValueEng;
  }


  /**
   * @param newTextValueEng the newTextValueEng to set
   */
  public void setNewTextValueEng(final String newTextValueEng) {
    this.newTextValueEng = newTextValueEng;
  }


  /**
   * @return the oldTextValueGer
   */
  public String getOldTextValueGer() {
    return this.oldTextValueGer;
  }


  /**
   * @param oldTextValueGer the oldTextValueGer to set
   */
  public void setOldTextValueGer(final String oldTextValueGer) {
    this.oldTextValueGer = oldTextValueGer;
  }


  /**
   * @return the newTextValueGer
   */
  public String getNewTextValueGer() {
    return this.newTextValueGer;
  }


  /**
   * @param newTextValueGer the newTextValueGer to set
   */
  public void setNewTextValueGer(final String newTextValueGer) {
    this.newTextValueGer = newTextValueGer;
  }


}
