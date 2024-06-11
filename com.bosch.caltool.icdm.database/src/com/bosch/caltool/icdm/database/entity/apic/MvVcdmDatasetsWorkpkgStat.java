package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;


/**
 * The persistent class for the V_VCDM_DATASETS_WORKPKG_STAT database table.
 */
// ICDM-2469
@Entity
@ReadOnly
@Table(name = "V_VCDM_DATASETS_WORKPKG_STAT")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class MvVcdmDatasetsWorkpkgStat implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ID")
  private Long id;

  // 221731
  @Column(name = "EASEEDST_ID")
  private Long easeeDstId;

  // 221731
  private BigDecimal revision;

  @Column(name = "APRJ_ID")
  private Long aprjId;

  @Column(name = "APRJ_NAME")
  private String aprjName;

  @Column(name = "CALIBRATED_LABELS")
  private Long calibratedLabels;

  @Column(name = "CHANGED_LABELS")
  private Long changedLabels;

  @Column(name = "CHECKED_LABELS")
  private Long checkedLabels;

  @Column(name = "COMPLETED_LABELS")
  private Long completedLabels;

  @Column(name = "NOSTATE_LABELS")
  private Long nostateLabels;

  @Column(name = "PRELIMCALIBRATED_LABELS")
  private Long prelimcalibratedLabels;

  @Column(name = "STATUS_ID")
  private Long statusId;

  @Column(name = "VCDM_SOFTWARE")
  private String vcdmSoftware;

  @Column(name = "VCDM_VARIANT")
  private String vcdmVariant;

  @Column(name = "WORKPKG_NAME")
  private String workpkgName;

  @Temporal(TemporalType.DATE)
  @Column(name = "EASEEDST_MOD_DATE")
  private Date easeedstModDate;


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
   * @return the revision
   */
  public BigDecimal getRevision() {
    return this.revision;
  }


  /**
   * @param revision the revision to set
   */
  public void setRevision(final BigDecimal revision) {
    this.revision = revision;
  }


}