/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author GDH9COB
 */
@Entity
@Table(name = "T_LDB2_SSD_CRW_RULES")
public class TLdb2SsdCrwRule {

  @Id
  @SequenceGenerator(name = "T_LDB2_SSD_CRW_RULES_ID_GENERATOR", sequenceName = "SEQ_SSD_CRW", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_LDB2_SSD_CRW_RULES_ID_GENERATOR")
  @Column(unique = true, nullable = false)
  private BigDecimal id;

  @Column(name = "CREATED_BY", length = 200)
  private String createdBy;

  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;

  @Column(name = "IS_LATEST_RVW", length = 1)
  private String isLatestRvw;

  @Column(name = "LAB_LAB_ID")
  private BigDecimal labLabId;

  @Column(name = "LAB_OBJ_ID")
  private BigDecimal labObjId;

  @Column(name = "MODIFIED_BY", length = 200)
  private String modifiedBy;

  @Column(name = "MODIFIED_DATE")
  private LocalDateTime modifiedDate;

  @Column(name = "REV")
  private BigDecimal rev;

  // bi-directional many-to-one association to TLdb2SsdCrwReview
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ssdRvwId", nullable = false)
  private TLdb2SsdCrwReview tLdb2SsdCrwReview;


  /**
   *
   */
  public TLdb2SsdCrwRule() {
    // public constructor
  }


  /**
   * @return the id
   */
  public BigDecimal getId() {
    return this.id;
  }


  /**
   * @param id the id to set
   */
  public void setId(final BigDecimal id) {
    this.id = id;
  }


  /**
   * @return the createdBy
   */
  public String getCreatedBy() {
    return this.createdBy;
  }


  /**
   * @param createdBy the createdBy to set
   */
  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }


  /**
   * @return the createdDate
   */
  public LocalDateTime getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the isLatestRvw
   */
  public String getIsLatestRvw() {
    return this.isLatestRvw;
  }


  /**
   * @param isLatestRvw the isLatestRvw to set
   */
  public void setIsLatestRvw(final String isLatestRvw) {
    this.isLatestRvw = isLatestRvw;
  }


  /**
   * @return the labLabId
   */
  public BigDecimal getLabLabId() {
    return this.labLabId;
  }


  /**
   * @param labLabId the labLabId to set
   */
  public void setLabLabId(final BigDecimal labLabId) {
    this.labLabId = labLabId;
  }


  /**
   * @return the labObjId
   */
  public BigDecimal getLabObjId() {
    return this.labObjId;
  }


  /**
   * @param labObjId the labObjId to set
   */
  public void setLabObjId(final BigDecimal labObjId) {
    this.labObjId = labObjId;
  }


  /**
   * @return the modifiedBy
   */
  public String getModifiedBy() {
    return this.modifiedBy;
  }


  /**
   * @param modifiedBy the modifiedBy to set
   */
  public void setModifiedBy(final String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  /**
   * @return the modifiedDate
   */
  public LocalDateTime getModifiedDate() {
    return this.modifiedDate;
  }


  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(final LocalDateTime modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * @return the rev
   */
  public BigDecimal getRev() {
    return this.rev;
  }


  /**
   * @param rev the rev to set
   */
  public void setRev(final BigDecimal rev) {
    this.rev = rev;
  }


  /**
   * @return the tLdb2SsdCrwReview
   */
  public TLdb2SsdCrwReview gettLdb2SsdCrwReview() {
    return this.tLdb2SsdCrwReview;
  }


  /**
   * @param tLdb2SsdCrwReview the tLdb2SsdCrwReview to set
   */
  public void settLdb2SsdCrwReview(final TLdb2SsdCrwReview tLdb2SsdCrwReview) {
    this.tLdb2SsdCrwReview = tLdb2SsdCrwReview;
  }


}
