/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.OneToMany;
import javax.persistence.ParameterMode;
import javax.persistence.SequenceGenerator;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;


/**
 * @author GDH9COB
 */
@Entity
@Table(name = "T_LDB2_SSD_CRW_REVIEW")
@NamedStoredProcedureQuery(
    name = "UpdateUserAccess", 
    procedureName = "K5ESK_LDB2.PR_IDM_UPDATE_USER_ACCESS",  
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p1_password", type = String.class) 
    }
)
public class TLdb2SsdCrwReview {

  @Id
  @SequenceGenerator(name = "T_LDB2_SSD_CRW_REVIEW_SSDRVWID_GENERATOR", sequenceName = "SEQ_SSD_CRW", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_LDB2_SSD_CRW_REVIEW_SSDRVWID_GENERATOR")
  @Column(name = "SSD_RVW_ID", unique = true, nullable = false)
  private BigDecimal ssdRvwId;

  @Column(name = "REVIEW_ID", length = 100)
  private String reviewId;

  @Column(name = "REVIEW_CREATED_BY", length = 100)
  private String reviewCreatedBy;

  @Column(name = "STATUS", length = 200)
  private BigDecimal status;

  @Column(name = "CREATED_BY", length = 200)
  private String createdBy;

  @Column(name = "CREATED_DATE")
  private LocalDateTime createdDate;

  @Column(name = "MODIFIED_BY", length = 200)
  private String modifiedBy;

  @Column(name = "MODIFIED_DATE")
  private LocalDateTime modifiedDate;

  @Column(name = "REVIEW_DECISION", length = 100)
  private BigDecimal decision;

  @Column(name = "REVIEW_LINK")
  private String reviewLink;

  @Column(name = "STATUS_DESC")
  private String statusDescription;

  @Column(name = "ERROR_DESC")
  private String errorDescription;

  @Column(name = "REVIEW_CLOSED_DATE")
  private LocalDateTime reviewClosedDate;

  // bi-directional many-to-one association to TLdb2SsdCrwRule
  @OneToMany(mappedBy = "tLdb2SsdCrwReview")
  private Set<TLdb2SsdCrwRule> tLdb2SsdCrwRules;

  /**
   * @return the ssdRvwId
   */
  public BigDecimal getSsdRvwId() {
    return ssdRvwId;
  }

  /**
   * @param ssdRvwId the ssdRvwId to set
   */
  public void setSsdRvwId(BigDecimal ssdRvwId) {
    this.ssdRvwId = ssdRvwId;
  }

  /**
   * @return the createdBy
   */
  public String getCreatedBy() {
    return createdBy;
  }

  /**
   * @param createdBy the createdBy to set
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * @return the createdDate
   */
  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the modifiedBy
   */
  public String getModifiedBy() {
    return modifiedBy;
  }

  /**
   * @param modifiedBy the modifiedBy to set
   */
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * @return the modifiedDate
   */
  public LocalDateTime getModifiedDate() {
    return modifiedDate;
  }

  /**
   * @param modifiedDate the modifiedDate to set
   */
  public void setModifiedDate(LocalDateTime modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return the reviewClosedDate
   */
  public LocalDateTime getReviewClosedDate() {
    return reviewClosedDate;
  }

  /**
   * @param reviewClosedDate the reviewClosedDate to set
   */
  public void setReviewClosedDate(LocalDateTime reviewClosedDate) {
    this.reviewClosedDate = reviewClosedDate;
  }

  /**
   * @return the reviewCreatedBy
   */
  public String getReviewCreatedBy() {
    return reviewCreatedBy;
  }

  /**
   * @param reviewCreatedBy the reviewCreatedBy to set
   */
  public void setReviewCreatedBy(String reviewCreatedBy) {
    this.reviewCreatedBy = reviewCreatedBy;
  }

  /**
   * @return the reviewId
   */
  public String getReviewId() {
    return reviewId;
  }

  /**
   * @param reviewId the reviewId to set
   */
  public void setReviewId(String reviewId) {
    this.reviewId = reviewId;
  }

  /**
   * @return the status
   */
  public BigDecimal getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(BigDecimal status) {
    this.status = status;
  }

  /**
   * @return the decision
   */
  public BigDecimal getDecision() {
    return decision;
  }

  /**
   * @param decision the decision to set
   */
  public void setDecision(BigDecimal decision) {
    this.decision = decision;
  }

  /**
   * @return the tLdb2SsdCrwRules
   */
  public Set<TLdb2SsdCrwRule> gettLdb2SsdCrwRules() {
    return tLdb2SsdCrwRules;
  }

  /**
   * @param tLdb2SsdCrwRules the tLdb2SsdCrwRules to set
   */
  public void settLdb2SsdCrwRules(Set<TLdb2SsdCrwRule> tLdb2SsdCrwRules) {
    this.tLdb2SsdCrwRules = tLdb2SsdCrwRules;
  }

  /**
   * @return the reviewLink
   */
  public String getReviewLink() {
    return reviewLink;
  }

  /**
   * @param reviewLink the reviewLink to set
   */
  public void setReviewLink(String reviewLink) {
    this.reviewLink = reviewLink;
  }

  /**
   * @return the statusDescription
   */
  public String getStatusDescription() {
    return statusDescription;
  }

  /**
   * @param statusDescription the statusDescription to set
   */
  public void setStatusDescription(String statusDescription) {
    this.statusDescription = statusDescription;
  }

  /**
   * @return the errorDescription
   */
  public String getErrorDescription() {
    return errorDescription;
  }

  /**
   * @param errorDescription the errorDescription to set
   */
  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }
}
