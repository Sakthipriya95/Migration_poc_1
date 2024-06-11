package com.bosch.ssd.api.jpa.review;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the T_LDB2_SSD_CRW_REVIEW database table.
 */
@Entity
@Table(name = "T_LDB2_SSD_CRW_REVIEW")
@NamedQueries({ @NamedQuery(name = "TLdb2SsdCrwReview.findAll", query = "SELECT t FROM TLdb2SsdCrwReview t"),
    @NamedQuery(name = "TLdb2SsdCrwReview.updateReviewStatus", query = "Update TLdb2SsdCrwReview SET status = :NEWSTATUS where REVIEWID = :RVW_ID "), })
@NamedNativeQueries({
    @NamedNativeQuery(name = "TLdb2SsdCrwReview.updateReviewStatusSSD", query = "Update T_LDB2_SSD_CRW_REVIEW SET status = ?, review_closed_date = ? where REVIEW_ID = ? "),
    @NamedNativeQuery(name = "TLdb2SsdCrwReview.updateReviewStatusSSDwithDecision", query = "Update T_LDB2_SSD_CRW_REVIEW SET status = ?, review_decision = ?, review_closed_date =? where REVIEW_ID = ? ") })


public class TLdb2SsdCrwReview implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "T_LDB2_SSD_CRW_REVIEW_SSDRVWID_GENERATOR", sequenceName = "SEQ_SSD_CRW")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_LDB2_SSD_CRW_REVIEW_SSDRVWID_GENERATOR")
  @Column(name = "SSD_RVW_ID", unique = true, nullable = false)
  private long ssdRvwId;

  @Column(name = "CREATED_BY", length = 200)
  private String createdBy;

  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  @Column(name = "MODIFIED_BY", length = 200)
  private String modifiedBy;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;
  
  @Column(name = "REVIEW_CLOSED_DATE")
  private Timestamp reviewClosedDate;

  @Column(name = "REVIEW_CREATED_BY", length = 100)
  private String reviewCreatedBy;

  @Column(name = "REVIEW_ID", length = 100)
  private String reviewId;

  @Column(length = 200)
  private long status;
  
  @Column(name = "REVIEW_DECISION", length = 100)
  private long decision;
 

  // bi-directional many-to-one association to TLdb2SsdCrwRule
  @OneToMany(mappedBy = "TLdb2SsdCrwReview")
  private List<TLdb2SsdCrwRule> TLdb2SsdCrwRules;

  /**
   * 
   */
  public TLdb2SsdCrwReview() {}

  /**
   * @return ssdRvwId
   */
  public long getSsdRvwId() {
    return this.ssdRvwId;
  }

  /**
   * @param ssdRvwId -ssdRvwId
   */
  public void setSsdRvwId(long ssdRvwId) {
    this.ssdRvwId = ssdRvwId;
  }

  /**
   * @return createdBy
   */
  public String getCreatedBy() {
    return this.createdBy;
  }

  /**
   * @param createdBy - createdBy
   */
  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  /**
   * @return createdDate
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate - createdDate
   */
  public void setCreatedDate(Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedBy
   */
  public String getModifiedBy() {
    return this.modifiedBy;
  }

  /**
   * @param modifiedBy - modifiedBy
   */
  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  /**
   * @return modifiedDate
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate -modifiedDate
   */
  public void setModifiedDate(Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }
  /**
   * @return reviewClosedDate
   */
  public Timestamp getReviewClosedDate() {
    return this.reviewClosedDate;
  }

  /**
   * @param reviewClosedDate - reviewClosedDate
   */
  public void setReviewClosedDate(Timestamp reviewClosedDate) {
    this.reviewClosedDate = reviewClosedDate;
  }
  /**
   * @return reviewCreatedBy
   */
  public String getReviewCreatedBy() {
    return this.reviewCreatedBy;
  }

  /**
   * @param reviewCreatedBy - reviewCreatedBy
   */
  public void setReviewCreatedBy(String reviewCreatedBy) {
    this.reviewCreatedBy = reviewCreatedBy;
  }

  /**
   * @return reviewId
   */
  public String getReviewId() {
    return this.reviewId;
  }

  /**
   * @param reviewId - reviewId
   */
  public void setReviewId(String reviewId) {
    this.reviewId = reviewId;
  }

  /**
   * @return status
   */
  public long getStatus() {
    return this.status;
  }

  /**
   * @param status - status
   */
  public void setStatus(long status) {
    this.status = status;
  }

  /**
   * @return TLdb2SsdCrwRules
   */
  public List<TLdb2SsdCrwRule> getTLdb2SsdCrwRules() {
    return this.TLdb2SsdCrwRules;
  }

  /**
   * @param TLdb2SsdCrwRules - TLdb2SsdCrwRules
   */
  public void setTLdb2SsdCrwRules(List<TLdb2SsdCrwRule> TLdb2SsdCrwRules) {
    this.TLdb2SsdCrwRules = TLdb2SsdCrwRules;
  }

  /**
   * @param TLdb2SsdCrwRule -  TLdb2SsdCrwRule
   * @return TLdb2SsdCrwRule
   */
  public TLdb2SsdCrwRule addTLdb2SsdCrwRule(TLdb2SsdCrwRule TLdb2SsdCrwRule) {
    getTLdb2SsdCrwRules().add(TLdb2SsdCrwRule);
    TLdb2SsdCrwRule.setTLdb2SsdCrwReview(this);

    return TLdb2SsdCrwRule;
  }

  /**
   * @param TLdb2SsdCrwRule - TLdb2SsdCrwRule
   * @return TLdb2SsdCrwRule
   */
  public TLdb2SsdCrwRule removeTLdb2SsdCrwRule(TLdb2SsdCrwRule TLdb2SsdCrwRule) {
    getTLdb2SsdCrwRules().remove(TLdb2SsdCrwRule);
    TLdb2SsdCrwRule.setTLdb2SsdCrwReview(null);

    return TLdb2SsdCrwRule;
  }

}