/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

import java.util.Date;

/**
 * @author bne4cob
 */
public class VcdmDataSet {

  private Long objId;
  private String createdUser;
  private Date createdDate;
  private String modifiedUser;
  private Date modifiedDate;
  private Long calibratedLabels;
  private Long paritionLabels;
  private Long changedLabels;
  private Long checkedLabels;
  private Long completedLabels;
  private Long noStateLabels;
  private Long preLimLabels;
  private Long totalLabels;
  private Long aprjId;
  private String aprjName;
  private String easeedstState;
  private String programKey;
  private Date easeedstModDate;
  private String productKey;
  private Long revisionNo;

  /**
   * @return the id
   */
  public Long getId() {
    return this.objId;
  }

  /**
   * @param id the id to set
   */
  public void setId(final Long id) {
    this.objId = id;
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
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
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
   * @return the modifiedDate
   */
  public Date getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the calibratedLabels
   */
  public Long getCalibratedLabels() {
    return this.calibratedLabels;
  }


  /**
   * @param calibratedLabels the calibratedLabels to set
   */
  public void setCalibratedLabels(final Long calibratedLabels) {
    this.calibratedLabels = calibratedLabels;
  }


  /**
   * @return the paritionLabels
   */
  public Long getParitionLabels() {
    return this.paritionLabels;
  }


  /**
   * @param paritionLabels the paritionLabels to set
   */
  public void setParitionLabels(final Long paritionLabels) {
    this.paritionLabels = paritionLabels;
  }


  /**
   * @return the changedLabels
   */
  public Long getChangedLabels() {
    return this.changedLabels;
  }


  /**
   * @param changedLabels the changedLabels to set
   */
  public void setChangedLabels(final Long changedLabels) {
    this.changedLabels = changedLabels;
  }


  /**
   * @return the checkedLabels
   */
  public Long getCheckedLabels() {
    return this.checkedLabels;
  }


  /**
   * @param checkedLabels the checkedLabels to set
   */
  public void setCheckedLabels(final Long checkedLabels) {
    this.checkedLabels = checkedLabels;
  }


  /**
   * @return the completedLabels
   */
  public Long getCompletedLabels() {
    return this.completedLabels;
  }


  /**
   * @param completedLabels the completedLabels to set
   */
  public void setCompletedLabels(final Long completedLabels) {
    this.completedLabels = completedLabels;
  }


  /**
   * @return the noStateLabels
   */
  public Long getNoStateLabels() {
    return this.noStateLabels;
  }


  /**
   * @param noStateLabels the noStateLabels to set
   */
  public void setNoStateLabels(final Long noStateLabels) {
    this.noStateLabels = noStateLabels;
  }


  /**
   * @return the preLimLabels
   */
  public Long getPreLimLabels() {
    return this.preLimLabels;
  }


  /**
   * @param preLimLabels the preLimLabels to set
   */
  public void setPreLimLabels(final Long preLimLabels) {
    this.preLimLabels = preLimLabels;
  }


  /**
   * @return the totalLabels
   */
  public Long getTotalLabels() {
    return this.totalLabels;
  }


  /**
   * @param totalLabels the totalLabels to set
   */
  public void setTotalLabels(final Long totalLabels) {
    this.totalLabels = totalLabels;
  }


  /**
   * @return the aprjId
   */
  public Long getAprjId() {
    return this.aprjId;
  }


  /**
   * @param aprjId the aprjId to set
   */
  public void setAprjId(final Long aprjId) {
    this.aprjId = aprjId;
  }


  /**
   * @return the aprjName
   */
  public String getAprjName() {
    return this.aprjName;
  }


  /**
   * @param aprjName the aprjName to set
   */
  public void setAprjName(final String aprjName) {
    this.aprjName = aprjName;
  }


  /**
   * @return the easeedstState
   */
  public String getEaseedstState() {
    return this.easeedstState;
  }


  /**
   * @param easeedstState the easeedstState to set
   */
  public void setEaseedstState(final String easeedstState) {
    this.easeedstState = easeedstState;
  }


  /**
   * @return the programKey
   */
  public String getProgramKey() {
    return this.programKey;
  }


  /**
   * @param programKey the programKey to set
   */
  public void setProgramKey(final String programKey) {
    this.programKey = programKey;
  }


  /**
   * @return the easeedstModDate
   */
  public Date getEaseedstModDate() {
    return this.easeedstModDate;
  }


  /**
   * @param easeedstModDate the easeedstModDate to set
   */
  public void setEaseedstModDate(final Date easeedstModDate) {
    this.easeedstModDate = easeedstModDate;
  }


  /**
   * @return the productKey
   */
  public String getProductKey() {
    return this.productKey;
  }


  /**
   * @param productKey the productKey to set
   */
  public void setProductKey(final String productKey) {
    this.productKey = productKey;
  }


  /**
   * @return the revisionNo
   */
  public Long getRevisionNo() {
    return this.revisionNo;
  }


  /**
   * @param revisionNo the revisionNo to set
   */
  public void setRevisionNo(final Long revisionNo) {
    this.revisionNo = revisionNo;
  }

}
