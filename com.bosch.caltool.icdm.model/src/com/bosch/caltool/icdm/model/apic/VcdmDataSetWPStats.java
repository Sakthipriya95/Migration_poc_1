/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

import java.util.Date;

/**
 * @author bne4cob
 */
public class VcdmDataSetWPStats {

  private Long objId;
  private Long aprjId;
  private String aprjName;
  private Long calibratedLabels;
  private Long changedLabels;
  private Long checkedLabels;
  private Long completedLabels;
  private Long nostateLabels;
  private Long prelimcalibratedLabels;
  private Long statusId;
  private String vcdmSoftware;
  private String vcdmVariant;
  private Date easeedstModDate;
  private Long easeeDstId;
  private Long revisionNo;
  private String workpkgName;

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
   * @return the nostateLabels
   */
  public Long getNostateLabels() {
    return this.nostateLabels;
  }


  /**
   * @param nostateLabels the nostateLabels to set
   */
  public void setNostateLabels(final Long nostateLabels) {
    this.nostateLabels = nostateLabels;
  }


  /**
   * @return the prelimcalibratedLabels
   */
  public Long getPrelimcalibratedLabels() {
    return this.prelimcalibratedLabels;
  }


  /**
   * @param prelimcalibratedLabels the prelimcalibratedLabels to set
   */
  public void setPrelimcalibratedLabels(final Long prelimcalibratedLabels) {
    this.prelimcalibratedLabels = prelimcalibratedLabels;
  }


  /**
   * @return the statusId
   */
  public Long getStatusId() {
    return this.statusId;
  }


  /**
   * @param statusId the statusId to set
   */
  public void setStatusId(final Long statusId) {
    this.statusId = statusId;
  }


  /**
   * @return the vcdmSoftware
   */
  public String getVcdmSoftware() {
    return this.vcdmSoftware;
  }


  /**
   * @param vcdmSoftware the vcdmSoftware to set
   */
  public void setVcdmSoftware(final String vcdmSoftware) {
    this.vcdmSoftware = vcdmSoftware;
  }


  /**
   * @return the vcdmVariant
   */
  public String getVcdmVariant() {
    return this.vcdmVariant;
  }


  /**
   * @param vcdmVariant the vcdmVariant to set
   */
  public void setVcdmVariant(final String vcdmVariant) {
    this.vcdmVariant = vcdmVariant;
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
   * @return the easeeDstId
   */
  public Long getEaseeDstId() {
    return this.easeeDstId;
  }


  /**
   * @param easeeDstId the easeeDstId to set
   */
  public void setEaseeDstId(final Long easeeDstId) {
    this.easeeDstId = easeeDstId;
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


  /**
   * @return the workpkgName
   */
  public String getWorkpkgName() {
    return this.workpkgName;
  }


  /**
   * @param workpkgName the workpkgName to set
   */
  public void setWorkpkgName(final String workpkgName) {
    this.workpkgName = workpkgName;
  }


}
