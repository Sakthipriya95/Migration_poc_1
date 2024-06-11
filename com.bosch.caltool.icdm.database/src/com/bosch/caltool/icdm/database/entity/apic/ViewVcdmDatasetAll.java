/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.apic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.annotations.ReadOnly;

/**
 * @author PDH2COB
 */
@Entity
@ReadOnly
@Table(name = "viwe_all_datasets")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class ViewVcdmDatasetAll implements Serializable {

  private static final long serialVersionUID = 1L;


  private long easeedstId;
  private Timestamp creDate;
  private String creUser;
  private String customerName;
  private String deptName;
  private Timestamp easeedstCreDate;
  private String easeedstCreUser;
  private Timestamp easeedstModDate;
  private String easeedstModUser;
  private String easeedstState;
  private Long elementId;
  private String elementName;
  private Timestamp modDate;
  private String modUser;
  private String prodKey;
  private String progKey;
  private BigDecimal revision;
  private String variant;
  private String versionName;
  private Long calibratedLabels;
  private Long changedLabels;
  private Long checkedLabels;
  private Long completedLabels;
  private Long prelimcalibratedLabels;
  private Long noStateLabels;
  private Long totalLabels;
  private Long partitionLabel;


  @Id
  @Column(name = "EASEEDST_ID", unique = true, nullable = false)
  public long getEaseedstId() {
    return this.easeedstId;
  }

  public void setEaseedstId(final long easeedstId) {
    this.easeedstId = easeedstId;
  }

  @Column(name = "CALIBRATED_LABELS")
  public Long getCalibratedLabels() {
    return this.calibratedLabels;
  }

  public void setCalibratedLabels(final Long calibratedLabels) {
    this.calibratedLabels = calibratedLabels;
  }

  @Column(name = "CHANGED_LABELS")
  public Long getChangedLabels() {
    return this.changedLabels;
  }

  public void setChangedLabels(final Long changedLabels) {
    this.changedLabels = changedLabels;
  }

  @Column(name = "CHECKED_LABELS")
  public Long getCheckedLabels() {
    return this.checkedLabels;
  }

  public void setCheckedLabels(final Long checkedLabels) {
    this.checkedLabels = checkedLabels;
  }

  @Column(name = "COMPLETED_LABELS")
  public Long getCompletedLabels() {
    return this.completedLabels;
  }

  public void setCompletedLabels(final Long completedLabels) {
    this.completedLabels = completedLabels;
  }

  @Column(name = "NOSTATE_LABELS")
  public Long getNoStateLabels() {
    return this.noStateLabels;
  }

  public void setNoStateLabels(final Long noStateLabels) {
    this.noStateLabels = noStateLabels;
  }

  @Column(name = "PRELIMCALIBRATED_LABELS")
  public Long getPreLimLabels() {
    return this.prelimcalibratedLabels;
  }

  public void setPreLimLabels(final Long prelimcalibratedLabels) {
    this.prelimcalibratedLabels = prelimcalibratedLabels;
  }

  @Column(name = "TOTAL_LBL_IN_PARTITION")
  public Long getPartitionLabel() {
    return this.partitionLabel;
  }

  public void setPartitionLabel(final Long partitionLabel) {
    this.partitionLabel = partitionLabel;
  }

  @Column(name = "TOTAL_LABELS")
  public Long getTotalLabels() {
    return this.totalLabels;
  }

  public void setTotalLabels(final Long totalLabels) {
    this.totalLabels = totalLabels;
  }

  @Column(name = "CRE_DATE")
  public Timestamp getCreDate() {
    return this.creDate;
  }

  public void setCreDate(final Timestamp creDate) {
    this.creDate = creDate;
  }


  @Column(name = "CRE_USER", length = 30)
  public String getCreUser() {
    return this.creUser;
  }

  public void setCreUser(final String creUser) {
    this.creUser = creUser;
  }


  @Column(name = "CUSTOMER_NAME", length = 3000)
  public String getCustomerName() {
    return this.customerName;
  }

  public void setCustomerName(final String customerName) {
    this.customerName = customerName;
  }


  @Column(name = "DEPT_NAME", length = 30)
  public String getDeptName() {
    return this.deptName;
  }

  public void setDeptName(final String deptName) {
    this.deptName = deptName;
  }


  @Column(name = "EASEEDST_CRE_DATE")
  public Timestamp getEaseedstCreDate() {
    return this.easeedstCreDate;
  }

  public void setEaseedstCreDate(final Timestamp easeedstCreDate) {
    this.easeedstCreDate = easeedstCreDate;
  }


  @Column(name = "EASEEDST_CRE_USER", length = 30)
  public String getEaseedstCreUser() {
    return this.easeedstCreUser;
  }

  public void setEaseedstCreUser(final String easeedstCreUser) {
    this.easeedstCreUser = easeedstCreUser;
  }


  @Column(name = "EASEEDST_MOD_DATE")
  public Timestamp getEaseedstModDate() {
    return this.easeedstModDate;
  }

  public void setEaseedstModDate(final Timestamp easeedstModDate) {
    this.easeedstModDate = easeedstModDate;
  }


  @Column(name = "EASEEDST_MOD_USER", length = 30)
  public String getEaseedstModUser() {
    return this.easeedstModUser;
  }

  public void setEaseedstModUser(final String easeedstModUser) {
    this.easeedstModUser = easeedstModUser;
  }


  @Column(name = "EASEEDST_STATE", length = 100)
  public String getEaseedstState() {
    return this.easeedstState;
  }

  public void setEaseedstState(final String easeedstState) {
    this.easeedstState = easeedstState;
  }

  @Column(name = "ELEMENT_ID")
  public Long getElementId() {
    return this.elementId;
  }

  public void setElementId(final Long elementId) {
    this.elementId = elementId;
  }

  @Column(name = "ELEMENT_NAME")
  public String getElementName() {
    return this.elementName;
  }

  public void setElementName(final String elementName) {
    this.elementName = elementName;
  }


  @Column(name = "MOD_DATE")
  public Timestamp getModDate() {
    return this.modDate;
  }

  public void setModDate(final Timestamp modDate) {
    this.modDate = modDate;
  }


  @Column(name = "MOD_USER", length = 30)
  public String getModUser() {
    return this.modUser;
  }

  public void setModUser(final String modUser) {
    this.modUser = modUser;
  }


  @Column(name = "PROD_KEY", length = 100)
  public String getProdKey() {
    return this.prodKey;
  }

  public void setProdKey(final String prodKey) {
    this.prodKey = prodKey;
  }


  @Column(name = "PROG_KEY", length = 3000)
  public String getProgKey() {
    return this.progKey;
  }

  public void setProgKey(final String progKey) {
    this.progKey = progKey;
  }


  public BigDecimal getRevision() {
    return this.revision;
  }

  public void setRevision(final BigDecimal revision) {
    this.revision = revision;
  }


  @Column(length = 255)
  public String getVariant() {
    return this.variant;
  }

  public void setVariant(final String variant) {
    this.variant = variant;
  }


  @Column(name = "VERSION_NAME", length = 255)
  public String getVersionName() {
    return this.versionName;
  }

  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }

}
